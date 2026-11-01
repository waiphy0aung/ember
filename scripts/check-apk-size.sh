#!/usr/bin/env bash
# Fails when any APK under the given folder is larger than the budget (default 5 MB).
set -euo pipefail
folder="${1:?usage: check-apk-size.sh <apk-folder> [max-bytes]}"
max_bytes="${2:-5242880}"
if ! [[ "$max_bytes" =~ ^[0-9]{1,18}$ ]]; then
  echo "::error::Budget must be a whole number of bytes (at most 18 digits), got '$max_bytes'"
  exit 1
fi
found=0
while IFS= read -r -d '' apk; do
  found=1
  size=$(stat -c %s "$apk")
  awk -v n="$(basename "$apk")" -v s="$size" -v m="$max_bytes" 'BEGIN { printf "%s: %.2f MB (budget %.2f MB)\n", n, s/1048576, m/1048576 }'
  if [ "$size" -gt "$max_bytes" ]; then
    echo "::error::$(basename "$apk") is over the size budget"
    exit 1
  fi
done < <(find "$folder" -type f -name '*.apk' -print0)
if [ "$found" -eq 0 ]; then
  echo "::error::No APK found in $folder"
  exit 1
fi
