#!/usr/bin/env node
/**
 * The lint budget: the only warnings this project accepts, each with a reason.
 *
 * `./gradlew :app:lintBazaarDebug` already fails on *errors*. Warnings are the
 * slippery half — a deliberate deprecation and six "a newer version exists" nags
 * look identical to a real finding once they pile up, and after a while nobody
 * reads the report. So the accepted set is written down here, with the decision
 * behind each one, and this script fails when the report disagrees with it in
 * either direction:
 *
 *   - a rule that is not in the budget (a new finding nobody decided about),
 *   - more of a rule than the budget allows (an old finding growing),
 *   - fewer of a rule than the budget allows (a stale entry — the budget must
 *     shrink when something is fixed, or it silently becomes a licence to add it
 *     back).
 *
 * Usage: node scripts/lint_budget.mjs [report.sarif]
 *        (default: app/build/reports/lint-results-bazaarDebug.sarif)
 *
 * Run after the lint task. CI does exactly that, in the `lint` job.
 */

import { readFileSync, existsSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const reportPath = process.argv[2] ?? join(root, "app/build/reports/lint-results-bazaarDebug.sarif");

/**
 * rule → { max, reason }. An entry is an accepted warning, not a silenced one:
 * the reason says why it is not worth a change now and what would change it.
 */
const budget = {
  CustomSplashScreen: {
    max: 1,
    reason:
      "SplashActivity draws its own launch screen instead of using the platform SplashScreen API. " +
      "Deliberate (the fade hand-off is the design), and its two open halves — the black gap and the " +
      "deprecated overridePendingTransition — are tracked in docs/debt.md, where the fix belongs.",
  },
  AndroidGradlePluginVersion: {
    max: 2,
    reason:
      "Toolchain freshness (Gradle, AGP). Upgraded in a deliberate toolchain pass, never as a side " +
      "effect: AGP 9 carries built-in Kotlin, so the Gradle, AGP and Kotlin versions move together.",
  },
  NewerVersionAvailable: {
    max: 3,
    reason:
      "jna (pinned by vosk-android's @aar requirement) and the two Kotlin plugins (coupled to AGP 9's " +
      "built-in Kotlin). Same rule as above: no silent bumps in a feature commit.",
  },
  UseTomlInstead: {
    max: 1,
    reason:
      "FALSE POSITIVE, do not 'fix' it. app/build.gradle.kts declares jna as " +
      "\"net.java.dev.jna:jna:${libs.versions.jna.get()}@aar\" — the version DOES come from the catalog, " +
      "and the coordinate is written out only because a catalog entry cannot carry the @aar packaging. " +
      "Moving it to libs.versions.toml resolves to the jar and the native Vosk loader fails at runtime.",
  },
};

if (!existsSync(reportPath)) {
  console.error(`lint budget: no report at ${reportPath}`);
  console.error("  run `./gradlew :app:lintBazaarDebug` first (CI runs this after the lint task)");
  process.exit(2);
}

const sarif = JSON.parse(readFileSync(reportPath, "utf8"));
const results = (sarif.runs ?? []).flatMap((run) => run.results ?? []);

/** rule → {count, errorCount} */
const seen = new Map();
for (const result of results) {
  const rule = result.ruleId ?? "(unnamed rule)";
  const entry = seen.get(rule) ?? { count: 0, errors: 0 };
  entry.count += 1;
  if (result.level === "error") entry.errors += 1;
  seen.set(rule, entry);
}

const problems = [];

for (const [rule, entry] of [...seen].sort(([a], [b]) => a.localeCompare(b))) {
  const allowed = budget[rule];
  if (!allowed) {
    problems.push(
      `unbudgeted warning: ${rule} ×${entry.count} — fix it, or add it to scripts/lint_budget.mjs ` +
        "with the reason it is accepted",
    );
    continue;
  }
  if (entry.errors > 0) {
    problems.push(`${rule} reported ${entry.errors} error(s); the budget only covers warnings`);
  }
  if (entry.count > allowed.max) {
    problems.push(`${rule}: ${entry.count} findings, budget is ${allowed.max}`);
  } else if (entry.count < allowed.max) {
    problems.push(
      `${rule}: only ${entry.count} findings but the budget allows ${allowed.max} — lower or remove ` +
        "the entry, or the slack becomes permission to regress",
    );
  }
}

for (const rule of Object.keys(budget)) {
  if (!seen.has(rule)) {
    problems.push(
      `${rule} no longer appears in the report — remove it from scripts/lint_budget.mjs ` +
        "(a budget that lists a fixed warning loses its meaning)",
    );
  }
}

const total = results.length;
if (problems.length > 0) {
  console.error(`lint budget: ${total} warning(s), ${problems.length} problem(s)\n`);
  for (const problem of problems) console.error(`  ✗ ${problem}`);
  console.error("\nSee docs/debt.md — every accepted warning has a decision recorded there.");
  process.exit(1);
}

console.log(`lint budget: ${total} warnings, all accepted`);
for (const [rule, entry] of [...seen].sort(([a], [b]) => a.localeCompare(b))) {
  console.log(`  ✓ ${rule} ×${entry.count}`);
}
