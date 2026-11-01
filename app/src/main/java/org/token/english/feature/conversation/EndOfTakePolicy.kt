package org.token.english.feature.conversation

/**
 * When a take ends on its own (checklist 1.4/1.5) — pure, so the timing is a unit
 * test instead of a comment inside the capture loop.
 *
 * Three rules, in order:
 *  - **Nothing heard yet**: never end. An open mic waits for the learner to start
 *    rather than timing out the take before they speak.
 *  - **A phrase still in flight**: never end. Vosk is mid-recognition; a take that
 *    closes now loses the words it is about to settle.
 *  - **Quiet**: a take whose transcript already *is* an accepted answer closes on
 *    the short window ([COMPLETE_ANSWER_MS]) — there is nothing left to wait for,
 *    and the learner is standing there waiting for a verdict. Anything else closes
 *    on [SILENCE_MS], or [HESITANT_SILENCE_MS] for a beginner, who is more likely
 *    to be searching for a word than finished (checklist 1.5).
 *
 * The service feeds it facts and does what it says; it never scores anything itself
 * (see [SpeechBus.acceptedAnswer] for the one verdict it is allowed to ask about).
 */
object EndOfTakePolicy {

    /** Quiet time that closes a take which is already a complete accepted answer. */
    const val COMPLETE_ANSWER_MS = 1_200L

    /** Quiet time that closes any other take. Long enough for a pause for thought. */
    const val SILENCE_MS = 4_000L

    /**
     * Quiet time for an A1 turn: a beginner who has said a word or two is often
     * still assembling the sentence, and cutting them at four seconds is what makes
     * speaking practice feel like a test of speed.
     */
    const val HESITANT_SILENCE_MS = 6_000L

    /**
     * @param heardSomething the take has recognised at least one word
     * @param inFlight a phrase is still being recognised
     * @param silenceMs time since the last recognised sound
     * @param completeAnswer the transcript so far is one of the turn's accepted answers
     * @param hesitant the turn is A1 (see [DialogueTurn.level])
     */
    fun shouldEnd(
        heardSomething: Boolean,
        inFlight: Boolean,
        silenceMs: Long,
        completeAnswer: Boolean,
        hesitant: Boolean = false,
    ): Boolean {
        if (!heardSomething || inFlight) return false
        if (completeAnswer && silenceMs >= COMPLETE_ANSWER_MS) return true
        return silenceMs >= if (hesitant) HESITANT_SILENCE_MS else SILENCE_MS
    }
}
