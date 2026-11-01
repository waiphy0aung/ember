#!/usr/bin/env bash
# Signs an APK that inspect-apk.sh already approved, identified by its sha256.
# Runs only apksigner (Java); never Gradle and never aapt2.
# Usage: sign-apk.sh <unsigned.apk> <signed.apk> <key-alias> <approved-sha256>
# Needs BUILD_TOOLS, KEYSTORE_BASE64 and SIGNING_PASSWORD.
set -euo pipefail
unsigned="${1:?unsigned apk}"
signed="${2:?output apk}"
alias="${3:?key alias}"
approved_sha="${4:?approved sha256}"
: "${BUILD_TOOLS:?pinned build-tools folder is missing}"
: "${KEYSTORE_BASE64:?signing key secret is missing}"
: "${SIGNING_PASSWORD:?signing password secret is missing}"

[[ "$approved_sha" =~ ^[0-9a-f]{64}$ ]] || { echo "::error::Approved sha256 is malformed"; exit 1; }
actual_sha="$(sha256sum "$unsigned" | cut -d' ' -f1)"
if [ "$actual_sha" != "$approved_sha" ]; then
  echo "::error::Refusing to sign: this APK is not the one that passed inspection"
  exit 1
fi

key_file="$(mktemp)"
trap 'rm -f "$key_file"' EXIT
base64 -d <<< "$KEYSTORE_BASE64" > "$key_file"
"$BUILD_TOOLS/apksigner" sign --ks "$key_file" --ks-key-alias "$alias" \
  --ks-pass env:SIGNING_PASSWORD --key-pass env:SIGNING_PASSWORD \
  --out "$signed" "$unsigned"
"$BUILD_TOOLS/apksigner" verify --print-certs "$signed" | grep -E "Signer #1 certificate (DN|SHA-256)"
