#!/usr/bin/env bash
# Signs an unsigned APK with apksigner, in a job where Gradle never ran.
# Usage: sign-apk.sh <unsigned.apk> <signed.apk> <key-alias> <expected-package>
# Needs BUILD_TOOLS (a pinned Android build-tools folder), KEYSTORE_BASE64 and SIGNING_PASSWORD.
set -euo pipefail
unsigned="${1:?unsigned apk}"
signed="${2:?output apk}"
alias="${3:?key alias}"
expected_package="${4:?expected package}"

: "${BUILD_TOOLS:?pinned build-tools folder is missing}"
: "${KEYSTORE_BASE64:?signing key secret is missing}"
: "${SIGNING_PASSWORD:?signing password secret is missing}"

# Read the untrusted APK without the secrets in the environment.
badging="$(env -u KEYSTORE_BASE64 -u SIGNING_PASSWORD "$BUILD_TOOLS/aapt2" dump badging "$unsigned")"

# 1. Only sign the app we expect.
package="$(sed -n "s/^package: name='\([^']*\)'.*/\1/p" <<< "$badging")"
if [ "$package" != "$expected_package" ]; then
  echo "::error::Refusing to sign: package is '$package', expected '$expected_package'"
  exit 1
fi

# 2. Only the permissions Ember is allowed to have (Security reviewer, rule 1), in any declaration form.
allowed=(
  "android.permission.POST_NOTIFICATIONS"
  "android.permission.RECEIVE_BOOT_COMPLETED"
  "android.permission.USE_BIOMETRIC"
  "${expected_package}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
)
while IFS= read -r permission; do
  [ -z "$permission" ] && continue
  ok=false
  for a in "${allowed[@]}"; do
    if [ "$permission" = "$a" ]; then ok=true; fi
  done
  if [ "$ok" != true ]; then
    echo "::error::Refusing to sign: the APK asks for $permission, which Ember must not have"
    exit 1
  fi
done < <(sed -n "s/^uses-permission[a-z0-9-]*: name='\([^']*\)'.*/\1/p" <<< "$badging")

# 3. Sign; the decoded key lives only in a private temp file removed on exit.
key_file="$(mktemp)"
trap 'rm -f "$key_file"' EXIT
base64 -d <<< "$KEYSTORE_BASE64" > "$key_file"
"$BUILD_TOOLS/apksigner" sign --ks "$key_file" --ks-key-alias "$alias" \
  --ks-pass env:SIGNING_PASSWORD --key-pass env:SIGNING_PASSWORD \
  --out "$signed" "$unsigned"
"$BUILD_TOOLS/apksigner" verify --print-certs "$signed" | grep -E "Signer #1 certificate (DN|SHA-256)"
