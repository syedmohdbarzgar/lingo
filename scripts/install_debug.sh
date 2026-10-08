#!/usr/bin/env bash
#
# Build one store flavour's debug APK and install it on the attached device.
#
# It is deliberately a NO-OP when no device is connected: the whole point is to
# stop paying for a build (let alone the three-flavour release ritual) just to
# look at something on hardware. Compile check → `testBazaarDebugUnitTest`
# (which typechecks main sources) or `validateContent`; hardware check → this
# script, and only when a phone is actually plugged in.
#
# Usage:
#   scripts/install_debug.sh [bazaar|myket|googlePlay]   # default: bazaar
#   scripts/install_debug.sh --list-devices
#
# Note: every flavour ships the SAME applicationId (org.token.english), so
# installing another flavour replaces the one already on the device.
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

# Windows/Git Bash ships a 0-byte `adb` placeholder next to the real `adb.exe`;
# only the .exe is executable, so prefer it there.
exe_suffix=""
case "$(uname -s)" in
  MINGW*|MSYS*|CYGWIN*) exe_suffix=".exe" ;;
esac

find_adb() {
  local dirs=()
  local sdk_dir=""
  if [ -f local.properties ]; then
    sdk_dir="$(grep -m1 '^sdk\.dir=' local.properties | cut -d= -f2- | tr -d '\r')"
  fi
  [ -n "${ANDROID_HOME:-}" ]     && dirs+=("$ANDROID_HOME")
  [ -n "${ANDROID_SDK_ROOT:-}" ] && dirs+=("$ANDROID_SDK_ROOT")
  [ -n "$sdk_dir" ]              && dirs+=("$sdk_dir")
  dirs+=("$LOCALAPPDATA/Android/Sdk" "/e/AndroidSDK" "$HOME/Android/Sdk")

  local dir posix
  for dir in "${dirs[@]}"; do
    [ -n "$dir" ] || continue
    posix="${dir//\\//}"
    # sdk.dir is authored as a Windows path (E:\AndroidSDK) — let cygpath do it.
    if [ ! -d "$posix" ] && command -v cygpath >/dev/null 2>&1; then
      posix="$(cygpath -u "$dir" 2>/dev/null || echo "$posix")"
    fi
    if [ -s "$posix/platform-tools/adb$exe_suffix" ]; then
      echo "$posix/platform-tools/adb$exe_suffix"; return 0
    fi
  done

  local on_path
  on_path="$(command -v "adb$exe_suffix" 2>/dev/null || true)"
  [ -n "$on_path" ] && { echo "$on_path"; return 0; }
  return 1
}

if [ "${1:-}" = "--list-devices" ]; then
  if adb_path="$(find_adb)"; then "$adb_path" devices; else echo "adb not found."; exit 0; fi
  exit 0
fi

FLAVOR="${1:-bazaar}"
case "$FLAVOR" in
  bazaar|myket|googlePlay) ;;
  *) echo "unknown flavour: '$FLAVOR' (expected bazaar, myket or googlePlay)" >&2; exit 2 ;;
esac

if ! ADB="$(find_adb)"; then
  echo "Android platform-tools not found."
  echo "Looked at ANDROID_HOME / ANDROID_SDK_ROOT, sdk.dir in local.properties, common install paths and PATH."
  echo "Nothing to install — skipping (this is not an error)."
  exit 0
fi

connected="$("$ADB" devices | awk 'NR > 1 && $2 == "device" { print $1 }')"
unauthorized="$("$ADB" devices | awk 'NR > 1 && $2 == "unauthorized" { print $1 }')"

if [ -z "$connected" ]; then
  if [ -n "$unauthorized" ]; then
    echo "Device connected but not authorised: $(echo "$unauthorized" | tr '\n' ' ')"
    echo "Accept the 'Allow USB debugging?' prompt on the phone, then re-run."
  else
    echo "No device attached — nothing to install (this is not an error)."
    echo "Plug in a phone with USB debugging enabled, then run: scripts/install_debug.sh $FLAVOR"
  fi
  exit 0
fi

echo "Attached device(s): $(echo "$connected" | tr '\n' ' ')"
echo "Building and installing ${FLAVOR}Debug..."

# ${FLAVOR^} upper-cases the first letter → installer task name.
./gradlew ":app:install${FLAVOR^}Debug"

# Bring the app to the front so the install is actually visible. The launcher
# activity is .SplashActivity (it waits for content seeding, then hands off to
# MainActivity, which is exported=false and cannot be started from adb shell).
"$ADB" shell am start -n "org.token.english/.SplashActivity" >/dev/null 2>&1 \
  && echo "Launched org.token.english/.SplashActivity" \
  || echo "Installed, but could not launch automatically — open the app manually."
