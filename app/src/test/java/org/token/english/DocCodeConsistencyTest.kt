package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.local.DATABASE_VERSION
import org.token.english.domain.model.AnswerChecker
import java.io.File

/**
 * Checklist v15 B-12: when a doc quotes a code constant, it must quote the value
 * the app actually uses. `docs/debt.md` described production grading as "≥ 60% of
 * one exemplar's words" while `AnswerChecker` grades with 0.5 — one of the two was
 * silently wrong. This test reads both sides, so the next drift fails the fast
 * gate instead of misleading whoever trusts the docs.
 *
 * The same rule covers the two facts that did drift during the v65 pass: the Room
 * schema number quoted in README (v8 while the app shipped v9) and the bundle
 * counts quoted in README and the store listing (2,828 exercises when the bundle
 * held 2,888). Both are computed here from the shipped files, so a content batch or
 * a migration that moves a number cannot leave a doc agreeing with an older build.
 *
 * The last case guards the CI description itself in both directions: every job in
 * `.github/workflows/ci.yml` must appear, backticked, in AGENTS.md, README and
 * `docs/release.md`, and every job name those docs bold must still exist under
 * `jobs:`. Adding a job without documenting it and deleting one while the docs keep
 * naming it both fail the fast gate, so three documents cannot describe a pipeline
 * that no longer exists.
 *
 * The bundle is read through `File`, which Gradle cannot see as a test input — after a content edit
 * run the suite with `--rerun`, exactly like the content tests. The docs and `ci.yml` are inputs of
 * `:app:docGuard` (this class alone, on the unit-test classpath), so a doc edit re-runs seconds of
 * work and `check` plus every unit-test run cover the guard without `--rerun`.
 */
class DocCodeConsistencyTest {

    /** Tests run from the module dir (`app/`) or the repo root — accept both. */
    private fun repoFile(vararg candidates: String): File =
        candidates.map(::File).firstOrNull(File::exists)
            ?: error("none of ${candidates.joinToString()} exists — run tests from the repo root or app/")

    /** One array out of a bundle file, e.g. `lessons.json` → its `lessons` key. */
    private fun bundleArray(file: String, key: String): Int {
        val text = repoFile(
            "../app/src/main/assets/content/$file.json",
            "app/src/main/assets/content/$file.json",
        ).readText()
        return JSONObject(text).getJSONArray(key).length()
    }

    /** 192 → ۱۹۲, so the Persian listing can be checked against the same number. */
    private fun persianDigits(number: Int): String = number.toString()
        .map { char -> if (char in '0'..'9') ('۰'.code + (char - '0')).toChar() else char }
        .joinToString("")

    @Test
    fun `the bundle counts quoted in the docs match the shipped bundle`() {
        val lessons = bundleArray("lessons", "lessons")
        val words = bundleArray("vocabulary", "vocabulary")
        val exercises = bundleArray("exercises", "exercises")

        val readme = repoFile("../README.md", "README.md").readText()
        assertTrue(
            "README must quote the shipped lesson count ($lessons) — a content batch moved it",
            readme.contains("| Lessons | $lessons —"),
        )
        assertTrue(
            "README must quote the shipped exercise count ($exercises)",
            readme.contains("| Exercises | $exercises —"),
        )
        assertTrue(
            "README must quote the shipped vocabulary count ($words)",
            readme.contains("| Vocabulary entries | $words |"),
        )

        val listing = repoFile("../docs/store-listing.md", "docs/store-listing.md").readText()
        val claim = "$lessons lessons, $words vocabulary entries, $exercises exercises"
        assertTrue(
            "the store listing's claim table must quote '$claim'",
            listing.contains(claim),
        )
        val persianClaim = "${persianDigits(lessons)} درس، ${persianDigits(exercises)} تمرین و " +
            "${persianDigits(words)} واژه"
        assertTrue(
            "the Persian listing must quote '$persianClaim' — its numbers once said 2,828",
            listing.contains(persianClaim),
        )
    }

    @Test
    fun `the Room schema number quoted in the README is the shipped one`() {
        val readme = repoFile("../README.md", "README.md").readText()
        assertTrue(
            "README's storage row must say schema v$DATABASE_VERSION (it said v8 while the app " +
                "shipped v9)",
            readme.contains("Room (schema v$DATABASE_VERSION, exported)"),
        )
        assertTrue(
            "README's package tree must say AppDatabase (v$DATABASE_VERSION, …)",
            readme.contains("AppDatabase (v$DATABASE_VERSION, schemas exported)"),
        )
        assertTrue(
            "README's known-debt list must say Room is at version = $DATABASE_VERSION",
            readme.contains("Room is at `version = $DATABASE_VERSION`"),
        )
    }

    /**
     * The job ids under `jobs:` in `ci.yml`, in declaration order.
     *
     * A workflow key is two spaces in and ends in a colon; everything below it is
     * indented further, and the `on:` block is above `jobs:` so it is never read.
     * This is a line scan rather than a YAML parse on purpose — the test has no YAML
     * dependency, and the shape it needs (top-level job ids) is unambiguous here.
     */
    private fun ciJobs(): List<String> {
        val lines = repoFile(
            "../.github/workflows/ci.yml",
            ".github/workflows/ci.yml",
        ).readLines()
        val start = lines.indexOfFirst { it.trimEnd() == "jobs:" }
        require(start >= 0) { "ci.yml has no top-level jobs: block" }
        return lines.drop(start + 1)
            .mapNotNull { line ->
                Regex("^  ([A-Za-z0-9_-]+):\\s*${'$'}").find(line)?.groupValues?.get(1)
            }
    }

    /**
     * The docs' own convention for a job name is a bold, backticked token — `**`verify`**` —
     * while tasks and Gradle goals inside a sentence stay merely backticked (`lintBazaarDebug`,
     * `validateContent`). Reading exactly that token is what makes the reverse direction possible:
     * a name bolded as a job must still be a job.
     */
    private fun boldedTokens(doc: File): List<String> =
        Regex("\\*\\*`([^`]+)`\\*\\*").findAll(doc.readText()).map { it.groupValues[1] }.toList()

    @Test
    fun `the CI jobs in the docs are exactly the jobs in the workflow`() {
        val jobs = ciJobs()

        // A parser regression must not read as "nothing to compare".
        assertTrue("ci.yml must declare its jobs (parsed: $jobs)", jobs.size >= 3)
        assertTrue("ci.yml must declare the verify job (parsed: $jobs)", "verify" in jobs)

        val docs = listOf(
            repoFile("../AGENTS.md", "AGENTS.md"),
            repoFile("../README.md", "README.md"),
            repoFile("../docs/release.md", "docs/release.md"),
        )
        val missing = docs.flatMap { doc ->
            val text = doc.readText()
            jobs.filterNot { job -> text.contains("`$job`") }.map { job -> "${doc.name}: `$job`" }
        }
        assertTrue(
            "every ci.yml job must be named (backticked) in AGENTS.md, README and docs/release.md, " +
                "so adding a job without its doc update fails here — missing: $missing",
            missing.isEmpty(),
        )

        // The other direction: a job deleted (or renamed) in ci.yml must not survive in the docs.
        val stale = docs.flatMap { doc ->
            boldedTokens(doc).filterNot { it in jobs }.distinct().map { token -> "${doc.name}: `$token`" }
        }
        assertTrue(
            "every job name bolded in the docs must still exist under `jobs:` in ci.yml — deleting " +
                "a job and leaving it named here fails this test — stale: $stale",
            stale.isEmpty(),
        )
    }

    @Test
    fun `the production overlap floor stated in the docs matches the code`() {
        val percent = (AnswerChecker.PRODUCTION_MIN_EXEMPLAR_OVERLAP * 100).toInt()
        val doc = repoFile("../docs/debt.md", "docs/debt.md").readText()
        assertTrue(
            "docs/debt.md must state the overlap floor the app actually grades with " +
                "(≥ $percent% of one exemplar's words) — update the doc or the constant, not only one side",
            doc.contains("≥ $percent% of one exemplar's words"),
        )
    }
}
