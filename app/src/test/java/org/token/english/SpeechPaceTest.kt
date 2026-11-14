package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.SpeechPace
import org.token.english.domain.model.ThemeMode

/**
 * The speaking pace («سرعت صدا») — the one setting that used to be a constant.
 *
 * `TtsAudioPlayer` applied 0.75 to every English utterance and nothing could move it:
 * a learner whose listening had improved stayed at the beginner pace, and a phone whose
 * English voice reads fast could not be slowed any further. The rate now comes from the
 * settings record, so the rules worth pinning are the ones that decide **what value**
 * the app speaks at — the player applying it to the next sentence is a device check
 * (`scripts/verify_device.sh`), because the engine is not reachable from the JVM suite.
 */
class SpeechPaceTest {

    @Test
    fun `the shipped pace is the one the app already spoke at`() {
        // An upgrade must sound exactly like the build it replaced, so the default is
        // 0.75 — not the first enum constant and not the engine's native 1.0.
        assertEquals(0.75f, SpeechPace.DEFAULT.rate, 0.0001f)
        assertEquals(SpeechPace.STANDARD, SpeechPace.DEFAULT)
    }

    @Test
    fun `the three steps ascend and none is below the slurring floor`() {
        val rates = SpeechPace.entries.map { it.rate }

        assertEquals("the steps must ascend: a picker that jumps back is a bug", rates.sorted(), rates)
        assertTrue("below ~0.6 most engines slur the sentence", rates.first() >= 0.6f)
        assertEquals("the fast end is the engine's own native speed", 1.0f, rates.last(), 0.0001f)
    }

    @Test
    fun `a stored name is read and a corrupt one falls back to the default`() {
        assertEquals(SpeechPace.SLOW, SpeechPace.stored("SLOW"))
        assertEquals(SpeechPace.NATURAL, SpeechPace.stored("NATURAL"))

        // A missing value is a fresh install; an unknown one is a bad record. Neither
        // is a new speed, and a guessed fallback would silently change how the app
        // sounds — the same rule `theme_mode` and `level` follow.
        assertEquals(SpeechPace.DEFAULT, SpeechPace.stored(null))
        assertEquals(SpeechPace.DEFAULT, SpeechPace.stored("TURBO"))
    }

    @Test
    fun `a settings record written before the setting existed still parses to the shipped pace`() {
        // The field is defaulted, so the old record needs no migration and no key; what
        // it must not do is parse to something the learner never chose.
        val older = AppSettings(
            isFirstLaunch = false,
            level = LearningLevel.A1,
            themeMode = ThemeMode.SYSTEM,
            dailyGoalMinutes = 15,
            soundEnabled = true,
            contentVersion = 0,
            trialStartedAt = 0L,
            subscriptionUntil = 0L,
        )

        assertEquals(SpeechPace.STANDARD, older.speechPace)
        assertEquals("and it is the rate the app spoke at", 0.75f, older.speechPace.rate, 0.0001f)
    }
}
