package org.token.english.core.audio

/**
 * Speaks text with the learner's sound setting applied (checklist B-6).
 *
 * The container owns the mute decision — a ViewModel only asks to speak and never
 * reaches for the player or the settings itself. [onDone] always fires when the
 * request ends; [onError] fires in addition when no sound was produced. Muting is
 * not an error (checklist B-3).
 *
 * A normal interface (not a `fun interface`) so the optional callbacks can keep
 * their defaults; tests implement it with a one-line `object : Speaker`.
 */
interface Speaker {
    fun speak(text: String, onDone: (() -> Unit)? = null, onError: (() -> Unit)? = null)

    /**
     * Stops whatever is playing, if anything. Defaulted so a test double only has
     * to implement [speak]: the only production caller is the screen that must not
     * leave the device talking after the learner has left it.
     */
    fun stop() = Unit
}
