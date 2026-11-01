#!/usr/bin/env bash
# Signs an unsigned APK with apksigner. No Gradle runs here, so no build plugin sees the key.
# Usage: sign-apk.sh <unsigned.apk> <signed.apk> <key-alias> <expected-package>
# Reads the key from KEYSTORE_BASE64 and its password from SIGNING_PASSWORD.
set -euo pipefail
unsigned="${1:?unsigned apk}"
signed="${2:?output apk}"
alias="${3:?key alias}"
expected_package="${4:?expected package}"

: "${KEYSTORE_BASE64:?signing key secret is missing}"
: "${SIGNING_PASSWORD:?signing password secret is missing}"

build_tools="$(ls -d "${ANDROID_HOME:?ANDROID_HOME not set}"/build-tools/* | sort -V | tail -n 1)"
badging="$("$build_tools/aapt2" dump badging "$unsigned")"

# Only sign the app we expect, and never one that asks for internet access.
package="$(sed -n "s/^package: name='\([^']*\)'.*/\1/p" <<< "$badging")"
if [ "$package" != "$expected_package" ]; then
  echo "::error::Refusing to sign: package is '$package', expected '$expected_package'"
  exit 1
fi
if grep -q "uses-permission: name='android.permission.INTERNET'" <<< "$badging"; then
  echo "::error::Refusing to sign: the APK asks for internet access"
  exit 1
fi

key_file="$(mktemp)"
trap 'rm -f "$key_file"' EXIT
base64 -d <<< "$KEYSTORE_BASE64" > "$key_file"
"$build_tools/apksigner" sign --ks "$key_file" --ks-key-alias "$alias" \
  --ks-pass env:SIGNING_PASSWORD --key-pass env:SIGNING_PASSWORD \
  --out "$signed" "$unsigned"
"$build_tools/apksigner" verify --print-certs "$signed" | grep -E "Signer #1 certificate (DN|SHA-256)"
