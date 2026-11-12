package org.token.english.core.audio

import java.io.File
import java.io.RandomAccessFile

/**
 * The learner's take, written to disk **while** the recogniser hears it.
 *
 * Speaking practice used to be either/or: a recorder kept the audio but nobody
 * judged it, and the recogniser judged it but kept only text — which is why the
 * lesson's `speaking` step lost its "hear yourself" playback when it was graded.
 * The capture loop already holds the PCM it feeds Vosk, so the same buffers are
 * appended here; the learner gets a verdict **and** their own voice, from one take.
 *
 * The format is deliberately the dumbest one that plays everywhere: mono 16-bit
 * linear PCM in a WAV container, at the recogniser's own 16 kHz rate. Nothing is
 * encoded, so a take is exactly bytes-per-second × seconds on disk, and the
 * header can be written twice — zeros first, real sizes when the take ends — which
 * is what makes it writeable while the recording is still running.
 *
 * Failure is never fatal to the take: a device that cannot open the file still
 * gets its transcript and its verdict, only without playback ([finish] returns
 * null). A take that captured nothing returns null too, and leaves no empty file
 * behind — an empty WAV would make the player fail instead of the button being
 * absent.
 */
class TakeRecorder(
    private val file: File,
    private val sampleRate: Int = SAMPLE_RATE,
) {

    private var out: RandomAccessFile? = null
    private var dataBytes = 0

    init {
        try {
            file.parentFile?.mkdirs()
            out = RandomAccessFile(file, "rw").apply {
                setLength(0)
                write(header(0, sampleRate))
            }
        } catch (t: Throwable) {
            // Playback is an addition to the take, never a precondition for it.
            out = null
        }
    }

    /** True while the file is open and can still take audio. */
    val isOpen: Boolean get() = out != null

    /** Appends one captured buffer. A short read is the caller's own length, not a fixed size. */
    fun append(pcm: ByteArray, length: Int = pcm.size) {
        val stream = out ?: return
        if (length <= 0) return
        try {
            stream.write(pcm, 0, length)
            dataBytes += length
        } catch (t: Throwable) {
            // A full disk mid-take must not take the take down with it.
            discard()
        }
    }

    /**
     * Closes the file and patches the WAV sizes, returning it — or `null` when no
     * audio was captured (or the file could not be written), in which case nothing
     * is left on disk.
     */
    fun finish(): File? {
        val stream = out ?: return null
        out = null
        return try {
            if (dataBytes <= 0) {
                stream.close()
                file.delete()
                null
            } else {
                stream.seek(0)
                stream.write(header(dataBytes, sampleRate))
                stream.close()
                file
            }
        } catch (t: Throwable) {
            closeQuietly(stream)
            file.delete()
            null
        }
    }

    /** Throws the take away: the screen left, or a newer take replaced it. */
    fun discard() {
        val stream = out ?: return
        out = null
        closeQuietly(stream)
        file.delete()
    }

    private fun closeQuietly(stream: RandomAccessFile) {
        try {
            stream.close()
        } catch (t: Throwable) {
            // Already closed.
        }
    }

    companion object {
        /** The recogniser's own rate — resampling here would buy nothing. */
        const val SAMPLE_RATE = 16_000

        /** 16-bit PCM: one sample is two bytes, and the WAV header says so. */
        const val BYTES_PER_SAMPLE = 2

        /** "RIFF" (12) + "fmt " chunk (24) + "data" header (8). */
        const val HEADER_BYTES = 44

        /**
         * A canonical 44-byte WAV header for mono 16-bit PCM.
         *
         * Pure on purpose: the two sizes are the only thing that changes between the
         * placeholder (0) and the finished file, and a test can read them back
         * instead of trusting a byte count in a comment.
         */
        fun header(dataBytes: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
            val header = ByteArray(HEADER_BYTES)
            fun ascii(at: Int, text: String) {
                text.forEachIndexed { i, c -> header[at + i] = c.code.toByte() }
            }
            fun le32(at: Int, value: Int) {
                header[at] = value.toByte()
                header[at + 1] = (value shr 8).toByte()
                header[at + 2] = (value shr 16).toByte()
                header[at + 3] = (value shr 24).toByte()
            }
            fun le16(at: Int, value: Int) {
                header[at] = value.toByte()
                header[at + 1] = (value shr 8).toByte()
            }
            ascii(0, "RIFF")
            le32(4, HEADER_BYTES - 8 + dataBytes)
            ascii(8, "WAVE")
            ascii(12, "fmt ")
            le32(16, 16)                       // PCM fmt chunk size
            le16(20, 1)                        // format: uncompressed PCM
            le16(22, 1)                        // channels: mono
            le32(24, sampleRate)
            le32(28, sampleRate * BYTES_PER_SAMPLE) // byte rate
            le16(32, BYTES_PER_SAMPLE)         // block align
            le16(34, 16)                       // bits per sample
            ascii(36, "data")
            le32(40, dataBytes)
            return header
        }
    }
}
