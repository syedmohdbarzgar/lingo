#!/usr/bin/env bash
# Fetches Phonon 31M (INT8, ~87 MB) into the app's assets.
#
# The bundle is a build input, not source: it is gitignored (see .gitignore) and must
# never be committed to the normal git history. Requires `huggingface_hub` (pip).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ASSETS="$ROOT/app/src/main/assets"
FILE="phonon_int8.litertlm"
REPO="litert-community/phonon-31m-en"

mkdir -p "$ASSETS"

if ! command -v hf >/dev/null 2>&1; then
  echo "huggingface_hub CLI (hf) not found. Install it with: pip install -U huggingface_hub" >&2
  exit 1
fi

echo "Downloading $REPO/$FILE from Hugging Face…"
hf download "$REPO" "$FILE" --local-dir "$ASSETS"

SIZE_BYTES=$(wc -c < "$ASSETS/$FILE" | tr -d ' ')
echo "Downloaded $ASSETS/$FILE ($SIZE_BYTES bytes)"

# ~87 MB. A much smaller file is an error page or a partial download.
if [ "$SIZE_BYTES" -lt 80000000 ]; then
  echo "Model looks too small — expected about 87 MB. Delete it and retry." >&2
  exit 1
fi
