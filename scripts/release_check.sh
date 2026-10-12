#!/usr/bin/env bash
#
# Release pre-flight. Answers, in a second, the questions that make a release
# build fail — or worse, ship something unverifiable — *before* you pay for the
# multi-minute assemble (R8 + packaging) that would otherwise die at the very end.
#
# The Gradle gates in app/build.gradle.kts already fail closed; this script is the
# calm, early version of them, and it also covers the signing key they do not.
#
# What it checks (AGENTS.md §4a):
#   1. keystore.properties exists and carries the signing key
#      (storeFile / storePassword / keyAlias / keyPassword).
#   2. bazaarRsaKey is set — required for a Cafe Bazaar release (its own gate).
#   3. myketPublicKey is set — optional, warned only (Myket verifies what it can).
#   4. CompanionApp.EXPECTED_SIGNING_SHA256 is not empty — the Zaribar digest.
#      Empty means the free-access grant trusts the package id alone (N-1), and a
#      release must not ship that.
#
# Usage:
#   scripts/release_check.sh [bazaar|myket|googlePlay]   # default: check all
#   scripts/release_check.sh --print-template            # show keystore.properties
#
# Exit codes: 0 = ready to assemble a release, 1 = something is missing,
#             2 = usage error.
set -uo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

keystore_file="keystore.properties"
companion_source="app/src/main/java/org/token/english/core/billing/CompanionApp.kt"

problems=0
warnings=0

fail() { problems=$((problems + 1)); echo "  [MISSING] $*"; }
warn() { warnings=$((warnings + 1)); echo "  [WARN]    $*"; }
ok()   { echo "  [ OK ]    $*"; }

print_template() {
  cat <<'EOF'
# keystore.properties — NEVER commit this file; it holds secrets.
# Add it to your local working copy only (it is already git-ignored).
#
# Paths may be absolute or relative to the project root.
storeFile=/absolute/path/to/release.jks
storePassword=REPLACE_ME
keyAlias=REPLACE_ME
keyPassword=REPLACE_ME
#
# Cafe Bazaar purchase-verification key (developers.cafebazaar.ir → app → RSA key).
# Required for a `bazaar` release; debug builds fall back to SecurityCheck.Disable.
bazaarRsaKey=REPLACE_ME
#
# Myket public key (developer.myket.ir). Optional but recommended.
myketPublicKey=REPLACE_ME
EOF
}

# --- argument handling -------------------------------------------------------

FLAVOR=""
case "${1:-}" in
  --print-template) print_template; exit 0 ;;
  ""|bazaar|myket|googlePlay) FLAVOR="${1:-}" ;;
  -h|--help)
    grep '^#' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//' | sed '/^$/q'
    exit 0
    ;;
  *) echo "unknown argument: '$1' (expected bazaar, myket, googlePlay or --print-template)" >&2; exit 2 ;;
esac

printf 'Release pre-flight%s\n\n' "${FLAVOR:+ for '$FLAVOR'}"

# --- 1. signing key ----------------------------------------------------------

echo "Signing key ($keystore_file):"
if [ ! -f "$keystore_file" ]; then
  fail "$keystore_file does not exist — a release APK cannot be signed."
  echo "          Create it with:  scripts/release_check.sh --print-template"
else
  # Read a property value, stripping CR (the file is edited on Windows too).
  prop() { sed -n "s/^$1=//p" "$keystore_file" | head -n1 | tr -d '\r'; }
  have_all=1
  for key in storeFile storePassword keyAlias keyPassword; do
    if [ -z "$(prop "$key")" ]; then
      fail "$keystore_file has no '$key'."
      have_all=0
    fi
  done
  [ "$have_all" -eq 1 ] && ok "all four signing properties are present."
fi

# --- 2/3. marketplace keys ---------------------------------------------------

echo
echo "Marketplace keys:"
rsa=""
myket_key=""
if [ -f "$keystore_file" ]; then
  rsa="$(sed -n 's/^bazaarRsaKey=//p' "$keystore_file" | head -n1 | tr -d '\r')"
  myket_key="$(sed -n 's/^myketPublicKey=//p' "$keystore_file" | head -n1 | tr -d '\r')"
fi

if [ -n "$rsa" ]; then
  ok "bazaarRsaKey is set (Poolakey signature verification enabled)."
else
  if [ "$FLAVOR" = "bazaar" ]; then
    fail "bazaarRsaKey is empty — a Cafe Bazaar release refuses to assemble without it."
  else
    warn "bazaarRsaKey is empty — required only for a Cafe Bazaar release."
  fi
fi

if [ -n "$myket_key" ]; then
  ok "myketPublicKey is set."
else
  warn "myketPublicKey is empty — optional; Myket can still verify via its own flow."
fi

# --- 4. companion signing digest ---------------------------------------------

echo
echo "Companion signing digest (N-1):"
digest=""
if [ -f "$companion_source" ]; then
  digest="$(sed -n 's/.*val EXPECTED_SIGNING_SHA256[^=]*=[[:space:]]*\(.*\)/\1/p' "$companion_source" \
    | head -n1 | tr -d '\r' | sed 's/[[:space:]]*$//')"
fi

if [ -z "$digest" ] || [ "${digest#emptySet}" != "$digest" ]; then
  fail "CompanionApp.EXPECTED_SIGNING_SHA256 is empty — no release may ship a free"
  echo "          subscription that trusts the package id 'org.token.zaribar' alone."
  echo "          Get the Zaribar release certificate digest and paste it into"
  echo "          $companion_source :"
  echo
  echo "              keytool -printcert -jarfile zaribar.apk"
  echo
  echo "          Copy the 'SHA256:' line (e.g. AA:BB:CC:…) into the set:"
  echo "              val EXPECTED_SIGNING_SHA256: Set<String> = setOf(\"AA:BB:CC:…\")"
else
  ok "EXPECTED_SIGNING_SHA256 is configured ($digest)."
fi

# --- summary -----------------------------------------------------------------

echo
if [ "$problems" -gt 0 ]; then
  echo "NOT ready: $problems blocking issue(s), $warnings warning(s)."
  echo "A release build would fail closed (that is by design)."
  exit 1
fi

if [ "$warnings" -gt 0 ]; then
  echo "Ready to assemble a release ($warnings warning(s) above are non-blocking)."
else
  echo "Ready to assemble a release."
fi
exit 0
