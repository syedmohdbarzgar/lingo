#!/usr/bin/env bash
# Fetches the offline Vosk model used by conversation practice and unpacks it into
# app/src/main/assets/vosk/ so the app can copy it to internal storage on first use.
#
# WHY A SCRIPT AND NOT A COMMITTED ASSET: the small English model is ~40 MB. Committing
# it would permanently bloat the repo and every clone; the release APK still ships it
# (assets are packaged), it just arrives via this step instead of git.
#
# WHICH MODEL: `vosk-model-small-en-us-0.15`. The practice targets are ENGLISH sentences
# ("Go straight and turn left at the traffic light."), so recognition must run an English
# model — a Persian model (vosk-model-small-fa-*) cannot transcribe English speech and
# every comparison would fail. If you ever add Persian targets, add the fa model beside
# this one and pick per target, don't swap this directory.
#
# Usage:  bash scripts/fetch_vosk_model.sh
# Re-run safe: existing unpacked model is left alone unless --force.
set -euo pipefail

MODEL_NAME="vosk-model-small-en-us-0.15"
ASSETS_DIR="app/src/main/assets/vosk"
DEST="$ASSETS_DIR/$MODEL_NAME"
URL="https://alphacephei.com/vosk/models/${MODEL_NAME}.zip"

cd "$(dirname "$0")/.."

if [ -d "$DEST" ] && [ "${1:-}" != "--force" ]; then
  echo "Model already present at $DEST — nothing to do (use --force to re-fetch)."
  exit 0
fi

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

echo "Downloading $URL ..."
if command -v curl >/dev/null 2>&1; then
  curl -fL --retry 3 -o "$TMP/model.zip" "$URL"
else
  wget -O "$TMP/model.zip" "$URL"
fi

echo "Unpacking ..."
unzip -q "$TMP/model.zip" -d "$TMP"

rm -rf "$DEST"
mkdir -p "$ASSETS_DIR"
mv "$TMP/$MODEL_NAME" "$DEST"

# The app checks for this marker (and writes one if a partial copy was interrupted),
# so the bundled model must carry it too.
[ -f "$DEST/uuid" ] || uuidgen > "$DEST/uuid" 2>/dev/null || echo "vosk-model" > "$DEST/uuid"

echo "Done: $DEST"
echo "Model files: $(find "$DEST" -type f | wc -l)"
