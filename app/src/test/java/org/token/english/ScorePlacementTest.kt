package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Test
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.usecase.ScorePlacementUseCase

class ScorePlacementTest {

    private val scorer = ScorePlacementUseCase()

    private fun band(vararg correct: Boolean): List<Boolean> = correct.toList()

    private fun allBands(vararg bands: List<Boolean>): List<Boolean> = bands.flatMap { it }

    private val perfect = band(true, true, true, true, true, true)
    private val fiveOfSix = band(true, true, true, true, true, false)
    private val fourOfSix = band(true, true, true, true, false, false)
    private val threeOfSix = band(true, true, true, false, false, false)

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
    fun `four of six passes a band`() {
        // 4/6 is exactly the 2/3 band bar.
        val results = allBands(fourOfSix, fourOfSix)
        assertEquals(LearningLevel.A2, scorer(results))
    }

    @Test
    fun `three of six fails a band so the walk stops there`() {
        val results = allBands(
            perfect, // A1 passed
            threeOfSix, // A2 fails (3/6 < 2/3)
            perfect, // B1 — never reached
        )
        assertEquals(LearningLevel.A1, scorer(results))
    }

    @Test
    fun `failure stops the walk so higher bands are never credited`() {
        val results = allBands(
            perfect, // A1
            perfect, // A2
            threeOfSix, // B1 fails
            perfect, // B2 — ignored
            perfect, // C1 — ignored
            perfect, // C2 — ignored
        )
        assertEquals(LearningLevel.A2, scorer(results))
    }

    @Test
    fun `one wrong per band keeps climbing while the band holds`() {
        val results = allBands(
            fiveOfSix, // A1 5/6, cum 5/6
            fiveOfSix, // A2 5/6, cum 10/12
            fiveOfSix, // B1 5/6, cum 15/18
            threeOfSix, // B2 3/6 fails → stop
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
    fun `default band size matches the 36-question bundle`() {
        assertEquals(6, ScorePlacementUseCase.DEFAULT_BAND_SIZE)
    }
}
