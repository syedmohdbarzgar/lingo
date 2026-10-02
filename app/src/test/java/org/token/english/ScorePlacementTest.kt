package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Test
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.usecase.ScorePlacementUseCase

class ScorePlacementTest {

    private val scorer = ScorePlacementUseCase()

    private fun band(vararg correct: Boolean): List<Boolean> = correct.toList()

    private fun allBands(vararg bands: List<Boolean>): List<Boolean> = bands.flatMap { it }

    private val perfect = band(true, true, true, true, true)
    private val fourOfFive = band(true, true, true, true, false)
    private val threeOfFive = band(true, true, true, false, false)

    @Test
    fun `empty results fall back to A1`() {
        assertEquals(LearningLevel.A1, scorer(emptyList()))
    }

    @Test
    fun `perfect score reaches C2`() {
        val results = allBands(perfect, perfect, perfect, perfect, perfect, perfect)
        assertEquals(LearningLevel.C2, scorer(results))
    }

    @Test
    fun `four of five passes a band`() {
        val results = allBands(fourOfFive, fourOfFive)
        assertEquals(LearningLevel.A2, scorer(results))
    }

    @Test
    fun `three of five fails a band so the walk stops there`() {
        val results = allBands(
            perfect, // A1 passed
            threeOfFive, // A2 fails (3/5 < 2/3)
            perfect, // B1 — never reached
        )
        assertEquals(LearningLevel.A1, scorer(results))
    }

    @Test
    fun `failure stops the walk so higher bands are never credited`() {
        val results = allBands(
            perfect, // A1
            perfect, // A2
            threeOfFive, // B1 fails
            perfect, // B2 — ignored
            perfect, // C1 — ignored
            perfect, // C2 — ignored
        )
        assertEquals(LearningLevel.A2, scorer(results))
    }

    @Test
    fun `one wrong per band keeps climbing while the band holds`() {
        val results = allBands(
            fourOfFive, // A1 4/5, cum 4/5
            fourOfFive, // A2 4/5, cum 8/10
            fourOfFive, // B1 4/5, cum 12/15
            threeOfFive, // B2 3/5 fails → stop
        )
        assertEquals(LearningLevel.B1, scorer(results))
    }

    @Test
    fun `adaptive early stop leaves a partial band that never inflates the level`() {
        // PlacementViewModel stops mid-band once the band is unwinnable; the
        // scorer sees a short final chunk and must not credit it.
        val results = allBands(
            perfect, // A1
            band(true, false, false), // A2 died after three answers (2 wrong → unwinnable)
        )
        assertEquals(LearningLevel.A1, scorer(results))
    }

    @Test
    fun `default band size matches the 30-question bundle`() {
        assertEquals(5, ScorePlacementUseCase.DEFAULT_BAND_SIZE)
    }
}
