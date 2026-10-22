package org.token.english

import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.AnswerChecker
import java.io.File

/**
 * Checklist v15 B-12: when a doc quotes a code constant, it must quote the value
 * the app actually uses. `docs/debt.md` described production grading as "≥ 60% of
 * one exemplar's words" while `AnswerChecker` grades with 0.5 — one of the two was
 * silently wrong. This test reads both sides, so the next drift fails the fast
 * gate instead of misleading whoever trusts the docs.
 */
class DocCodeConsistencyTest {

    /** Tests run from the module dir (`app/`) or the repo root — accept both. */
    private fun repoFile(vararg candidates: String): File =
        candidates.map(::File).firstOrNull(File::exists)
            ?: error("none of ${candidates.joinToString()} exists — run tests from the repo root or app/")

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
