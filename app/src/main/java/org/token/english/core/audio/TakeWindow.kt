package org.token.english.core.audio

/**
 * Which take still owns the screen — the one rule the mic service needs, on its own.
 *
 * A take is an asynchronous thing: the model loads, the loop reads buffers, and the
 * end arrives on a different thread than the one that asked for it. So "may this take
 * still speak?" is asked at several points, and it has two answers that are easy to
 * confuse:
 *
 *  - **the learner ended it** (`ACTION_STOP`, «پایان ضبط», the notification's action):
 *    the mic closes and the take **still reports** — its transcript is graded and its
 *    WAV is handed to the player. That is a normal end.
 *  - **the screen abandoned it** (`SpeechBus.reset()`, which bumps `SpeechBus.epoch`):
 *    nobody wants the take any more. It must not publish a word, a path or an error,
 *    and its half-written WAV is thrown away rather than finished — otherwise a take
 *    from a lesson the learner already left lands in the next item's state, whose
 *    «پخش صدای خودت» button then plays a sentence that belongs to another exercise.
 *
 * Both are one question here, [mayPublish]. [mayOpenMic] is the narrower one asked
 * before touching `AudioRecord`, and it is false after *either* answer, so a stopped
 * take cannot open the mic again.
 *
 * Pure and thread-safe on purpose: the service holds one of these, and the rules are
 * pinned by tests instead of by reading a 700-line Service.
 */
class TakeWindow(
    /** The newest take id the service has issued. */
    private val newest: () -> Int,
    /** The screen the take belongs to; a change means it was abandoned. */
    private val screenEpoch: () -> Int = { SpeechBus.epoch },
) {

    @Volatile
    private var active = 0

    @Volatile
    private var beginEpoch = -1

    /** Whether a take is currently allowed to hold the microphone. */
    val hasActiveTake: Boolean get() = active != 0

    /** [take] opens the mic: from here it is the one this window tracks. */
    fun begin(take: Int) {
        active = take
        beginEpoch = screenEpoch()
    }

    /** No take may open the mic any more — the learner stopped it, or it ended. */
    fun end() {
        active = 0
    }

    /** Whether [take] may still open (or keep) the microphone. */
    fun mayOpenMic(take: Int): Boolean = take != 0 && take == active

    /**
     * Whether the screen that asked for [take] has been abandoned since — the one
     * reason a take must also stop *recording*, not only stop publishing.
     */
    fun isAbandoned(take: Int): Boolean = take != 0 && beginEpoch != screenEpoch()

    /**
     * Whether [take] may still write to the screen: it must be the newest take *and*
     * the screen it began on must not have been abandoned since.
     */
    fun mayPublish(take: Int): Boolean =
        take != 0 && take == newest() && !isAbandoned(take)
}
