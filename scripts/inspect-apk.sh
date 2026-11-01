#!/usr/bin/env bash
# Checks an unsigned APK before anything signs it. Runs on a machine with no keys.
# Usage: inspect-apk.sh <apk> <package> <version-code> <version-name> <target-sdk>
# Needs BUILD_TOOLS (a pinned Android build-tools folder). Prints the APK's sha256 as its last line.
set -euo pipefail
apk="${1:?apk}"
expected_package="${2:?package}"
expected_code="${3:?version code}"
expected_name="${4:?version name}"
expected_target="${5:?target sdk}"
: "${BUILD_TOOLS:?pinned build-tools folder is missing}"

badging="$("$BUILD_TOOLS/aapt2" dump badging "$apk")"
fail() { echo "::error::Refusing this APK: $1"; exit 1; }

# The exact app, version and Android target we expect.
package_line="$(grep -m1 "^package: " <<< "$badging" || true)"
[ -n "$package_line" ] || fail "no package line"
[[ "$package_line" == *"name='${expected_package}' "* ]] || fail "wrong package: $package_line"
[[ "$package_line" == *"versionCode='${expected_code}' "* ]] || fail "wrong versionCode: $package_line"
[[ "$package_line" == *"versionName='${expected_name}' "* ]] || fail "wrong versionName: $package_line"
grep -qx "targetSdkVersion:'${expected_target}'" <<< "$badging" || fail "targetSdk is not ${expected_target}"

# No permissions Android would grant implicitly, and none outside Ember's allowlist.
if grep -q "^uses-implied-permission" <<< "$badging"; then fail "it has implied permissions"; fi
allowed=(
  "android.permission.POST_NOTIFICATIONS"
  "android.permission.RECEIVE_BOOT_COMPLETED"
  "android.permission.USE_BIOMETRIC"
  "${expected_package}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
)
while IFS= read -r permission; do
  [ -z "$permission" ] && continue
  ok=false
  for a in "${allowed[@]}"; do [ "$permission" = "$a" ] && ok=true; done
  [ "$ok" = true ] || fail "it asks for $permission, which Ember must not have"
done < <(sed -n "s/^uses-permission[a-z0-9-]*: name='\([^']*\)'.*/\1/p" <<< "$badging")

sha256sum "$apk" | cut -d' ' -f1
