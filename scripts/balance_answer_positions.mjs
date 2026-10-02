#!/usr/bin/env node
/**
 * Balances correct-answer positions in the bundled content JSON files.
 *
 * For every "multiple_choice" entry the correct option is moved to
 * position `counter % optionCount`, so answers cycle 0,1,2,3,0,1,… across the
 * file instead of clustering at index 0. The script is idempotent — running it
 * again produces no change — and edits the file line-by-line so the authored
 * formatting (inline or multi-line option arrays) is preserved.
 *
 * Usage: node scripts/balance_answer_positions.mjs [contentFile.json ...]
 *        (defaults to exercises.json + placement.json under app/src/main/assets)
 */

import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const defaultFiles = [
  join(root, "app/src/main/assets/content/exercises.json"),
  join(root, "app/src/main/assets/content/placement.json"),
];
const files = process.argv.length > 2 ? process.argv.slice(2) : defaultFiles;

function balance(path) {
  const lines = readFileSync(path, "utf8").split("\n");
  let isMc = false;
  let pending = null; // { options, startLine, endLine, inline }
  let counter = 0;
  let moved = 0;
  const distribution = {};

  const finishEntry = (correctLine, correctMatch) => {
    const { options, startLine, endLine, inline } = pending;
    const current = Number(correctMatch[2]);
    if (current < 0 || current >= options.length) {
      throw new Error(`${path}: correctIndex ${current} out of bounds (line ${correctLine + 1})`);
    }
    const target = counter % options.length;
    if (current !== target) {
      [options[current], options[target]] = [options[target], options[current]];
      if (inline) {
        const prefix = lines[startLine].match(/^(\s*"options":\s*)/)[1];
        const suffix = lines[startLine].endsWith(",") ? "," : "";
        lines[startLine] =
          prefix + "[" + options.map((o) => JSON.stringify(o)).join(", ") + "]" + suffix;
      } else {
        const itemIndent = lines[startLine + 1].match(/^\s*/)[0];
        const itemLines = options.map(
          (o, j) => itemIndent + JSON.stringify(o) + (j < options.length - 1 ? "," : ""),
        );
        lines.splice(startLine + 1, endLine - startLine - 1, ...itemLines);
      }
      lines[correctLine] = correctMatch[1] + String(target) + correctMatch[3];
      moved++;
    }
    distribution[target] = (distribution[target] ?? 0) + 1;
    counter++;
    pending = null;
  };

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];

    const typeMatch = line.match(/^\s*"type":\s*"([^"]+)",?\s*$/);
    if (typeMatch) {
      isMc = typeMatch[1] === "multiple_choice";
      pending = null;
      continue;
    }
    if (!isMc) continue;

    if (pending && !pending.inline && pending.endLine < 0) {
      // Collecting a multi-line options array until its closing bracket.
      if (/^\s*\],?\s*$/.test(line)) {
        pending.endLine = i;
        const inner = lines
          .slice(pending.startLine + 1, i)
          .map((l) => l.trim().replace(/,$/, ""))
          .join(", ");
        pending.options = JSON.parse("[" + inner + "]");
      }
      continue;
    }

    const inlineMatch = line.match(/^(\s*"options":\s*)(\[.*\])(,?)\s*$/);
    if (inlineMatch) {
      pending = {
        inline: true,
        startLine: i,
        endLine: i,
        options: JSON.parse(inlineMatch[2]),
      };
      continue;
    }
    if (/^\s*"options":\s*\[\s*$/.test(line)) {
      pending = { inline: false, startLine: i, endLine: -1, options: [] };
      continue;
    }

    const correctMatch = line.match(/^(\s*"correctIndex":\s*)(\d+)(,?)\s*$/);
    if (correctMatch && pending) {
      if (pending.endLine < 0) throw new Error(`${path}: unterminated options array at line ${i + 1}`);
      finishEntry(i, correctMatch);
    }
  }

  if (counter > 0) writeFileSync(path, lines.join("\n"), "utf8");

  console.log(
    `${path.replace(/\\/g, "/").split("/").slice(-2).join("/")}: ${counter} MC questions, ` +
      `${moved} answers moved, distribution ${JSON.stringify(distribution)}`,
  );
}

for (const f of files) balance(f);
