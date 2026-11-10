#!/usr/bin/env bash
#
# Release-readiness check ON A PHONE (Phase 4). The unit suite can prove the
# content gate, the migrations and every engine, but three things only hardware
# can answer: does the app still work with no network, how long does a first
# launch take on this device, and does a cold start stay under a second once the
# bundle is seeded.
#
# Like install_debug.sh it is a NO-OP when nothing is attached — the everyday
# loop is tests.
#
# What it does, in order:
#   1. installs the chosen flavour's debug APK,
#   2. CLEARS the app's data (fresh install: no content, no learner state),
#   3. turns airplane mode ON, cold-starts the app and measures it — that is the
#      first launch, which also seeds the 2888-exercise bundle into Room. Home
#      must appear without a crash and without any "connect to the internet"
#      state, because nothing in the learning path touches the network,
#   4. cold-starts a second time (content already seeded) for the steady-state
#      launch time,
#   5. restores airplane mode to whatever it was, and prints both times plus the
#      seeder's own log line ("seeded v64 in … ms").
#
# Usage:
#   scripts/verify_device.sh [bazaar|myket|googlePlay]   # default: bazaar
#   scripts/verify_device.sh --list-devices
#   scripts/verify_device.sh bazaar --require-device      # CI: no device is a failure
#
# Exit codes: 0 = checked (or nothing attached), 1 = the check failed.
#
# `--require-device` (or `EXPECT_DEVICE=1`) exists for the `device` CI job: the
# no-op-on-a-laptop behaviour is right for a human, and a false green in CI when
# the emulator never booted is exactly the "suite that proves nothing" this
# project refuses to keep.
set -uo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

package="org.token.english"
launcher="$package/.SplashActivity"

# Windows/Git Bash ships a 0-byte `adb` placeholder next to the real `adb.exe`.
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

FLAVOR=""
REQUIRE_DEVICE="${EXPECT_DEVICE:-0}"
for arg in "$@"; do
  case "$arg" in
    --require-device) REQUIRE_DEVICE=1 ;;
    bazaar|myket|googlePlay) FLAVOR="$arg" ;;
    *) echo "unknown argument: '$arg' (expected a flavour, --require-device or --list-devices)" >&2; exit 2 ;;
  esac
done
FLAVOR="${FLAVOR:-bazaar}"

if ! ADB="$(find_adb)"; then
  echo "Android platform-tools not found — nothing to verify on hardware."
  [ "$REQUIRE_DEVICE" = "1" ] && { echo "FAILED: --require-device was set."; exit 1; }
  exit 0
fi

connected="$("$ADB" devices | awk 'NR > 1 && $2 == "device" { print $1 }')"
unauthorized="$("$ADB" devices | awk 'NR > 1 && $2 == "unauthorized" { print $1 }')"

if [ -z "$connected" ]; then
  if [ -n "$unauthorized" ]; then
    echo "Device connected but not authorised: $(echo "$unauthorized" | tr '\n' ' ')"
    echo "Accept the 'Allow USB debugging?' prompt on the phone, then re-run."
  elif [ "$REQUIRE_DEVICE" = "1" ]; then
    # CI reached here with --require-device, so the emulator that should have
    # booted did not: report that, instead of the laptop's "nothing to do".
    echo "FAILED: --require-device was set but no device is attached (the emulator never came up)."
    exit 1
  else
    echo "No device attached — the hardware check did not run (this is not a failure)."
    echo "Plug in a phone with USB debugging enabled, then run: scripts/verify_device.sh $FLAVOR"
  fi
  exit 0
fi

device="$(echo "$connected" | head -n1)"
echo "Device: $device"
echo "Model:  $("$ADB" shell getprop ro.product.model | tr -d '\r')"
echo "SDK:    $("$ADB" shell getprop ro.build.version.sdk | tr -d '\r')"

# --- 0. install ---------------------------------------------------------------

echo
echo "Installing ${FLAVOR}Debug..."
if ! ./gradlew ":app:install${FLAVOR^}Debug" >/tmp/verify_device_install.log 2>&1; then
  echo "install failed — see /tmp/verify_device_install.log"
  tail -5 /tmp/verify_device_install.log
  exit 1
fi
echo "Installed."

# --- 1. airplane mode ---------------------------------------------------------

previous_airplane="$("$ADB" shell settings get global airplane_mode_on | tr -d '\r')"
[ -n "$previous_airplane" ] || previous_airplane=0

set_airplane() {
  local want="$1" # 1 = on, 0 = off
  if [ "$want" = "1" ]; then
    "$ADB" shell cmd connectivity airplane-mode enable >/dev/null 2>&1 ||
      "$ADB" shell settings put global airplane_mode_on 1 >/dev/null 2>&1
  else
    "$ADB" shell cmd connectivity airplane-mode disable >/dev/null 2>&1 ||
      "$ADB" shell settings put global airplane_mode_on 0 >/dev/null 2>&1
  fi
}

restore_airplane() { set_airplane "$previous_airplane"; }
trap restore_airplane EXIT

set_airplane 1
sleep 3
echo "Airplane mode: on (state before this run: $previous_airplane)"

failures=0
fail() { failures=$((failures + 1)); echo "  [FAIL] $*"; }
ok()   { echo "  [ OK ] $*"; }

# --- 2. first launch: fresh install, no network -------------------------------

echo
echo "Clearing app data (this is a fresh install: content and learner state go away)..."
"$ADB" shell pm clear "$package" >/dev/null 2>&1

"$ADB" logcat -c >/dev/null 2>&1
started="$("$ADB" shell am start -W -S -n "$launcher" 2>&1 | tr -d '\r')"
first_total="$(echo "$started" | sed -n 's/^TotalTime: //p' | head -n1)"
[ -n "$first_total" ] || first_total="?"
echo "First launch (cold, seeding): ${first_total}ms"

# The splash waits for seeding, then hands off to MainActivity (exported=false, so
# this dumpsys check is the only way to confirm it arrived).
sleep 12
resumed="$("$ADB" shell dumpsys activity activities 2>/dev/null \
  | grep -m1 -E 'mResumedActivity|topResumedActivity' | tr -d '\r')"
if echo "$resumed" | grep -q MainActivity; then
  ok "reached MainActivity with no network"
else
  fail "did not reach MainActivity offline — see: adb shell dumpsys activity activities"
fi

seed_line="$("$ADB" logcat -d -s ContentSeeder:I 2>/dev/null | tr -d '\r' | grep 'seeded v' | tail -n1)"
if [ -n "$seed_line" ]; then
  ok "seed: $seed_line"
else
  fail "the seeder logged no completion line (a seed that never finished leaves Home empty)"
fi

crashes="$("$ADB" logcat -d -s AndroidRuntime:E 2>/dev/null | tr -d '\r' | grep -A3 "$package" || true)"
if [ -z "$crashes" ]; then
  ok "no crash in logcat"
else
  fail "crash in logcat:"
  echo "$crashes" | head -20
fi

# --- 3. second launch: content already seeded --------------------------------

echo
started="$("$ADB" shell am start -W -S -n "$launcher" 2>&1 | tr -d '\r')"
second_total="$(echo "$started" | sed -n 's/^TotalTime: //p' | head -n1)"
[ -n "$second_total" ] || second_total="?"
echo "Second launch (cold, seeded): ${second_total}ms"

# --- 4. summary ---------------------------------------------------------------

echo
echo "Summary — ${FLAVOR}Debug on $device (airplane mode on for both launches)"
echo "  first launch  : ${first_total}ms  (includes the one-time content seed)"
echo "  later launch  : ${second_total}ms  (room already seeded)"
[ -n "${seed_line:-}" ] && echo "  seeder        : ${seed_line#*ContentSeeder: }"
echo
echo "A weak phone is the interesting case: the splash holds for at most 8s while"
echo "seeding (SplashActivity.MAX_WAIT_MS). A seed line above ~6s on the target"
echo "device means the bundle is close to that ceiling and the splash is hiding it."

if [ "$failures" -gt 0 ]; then
  echo "FAILED: $failures check(s) above."
  exit 1
fi
echo "Passed."
