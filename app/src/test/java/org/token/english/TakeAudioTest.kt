package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.audio.TakeRecorder
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The take's audio (v80): one capture the learner hears **and** the recogniser grades.
 *
 * The WAV container is the part a device cannot tell you about — a header with the
 * wrong sizes plays as silence or not at all, and "it sounded fine on my phone" is
 * not a check. So the file is written here, on the JVM, and read back byte by byte.
 */
class TakeAudioTest {

    private fun tempFile(): File =
        File.createTempFile("take", ".wav").apply { delete() }

    private fun le32(bytes: ByteArray, at: Int): Int =
        ByteBuffer.wrap(bytes, at, 4).order(ByteOrder.LITTLE_ENDIAN).int

    private fun le16(bytes: ByteArray, at: Int): Int =
        ByteBuffer.wrap(bytes, at, 2).order(ByteOrder.LITTLE_ENDIAN).short.toInt() and 0xFFFF

    private fun text(bytes: ByteArray, at: Int, length: Int): String =
        String(bytes, at, length, Charsets.US_ASCII)

    @Test
    fun `the header is a mono 16-bit PCM wave at the recogniser's own rate`() {
        val header = TakeRecorder.header(dataBytes = 320)

        assertEquals("RIFF", text(header, 0, 4))
        assertEquals("WAVE", text(header, 8, 4))
        assertEquals("fmt ", text(header, 12, 4))
        assertEquals("data", text(header, 36, 4))
        assertEquals("a 44-byte canonical header", TakeRecorder.HEADER_BYTES, header.size)
        assertEquals("uncompressed PCM", 1, le16(header, 20))
        assertEquals("mono", 1, le16(header, 22))
        assertEquals("16 kHz — no resampling, the recogniser's rate", 16_000, le32(header, 24))
        assertEquals("byte rate follows the sample rate", 32_000, le32(header, 28))
        assertEquals("one 2-byte sample per frame", 2, le16(header, 32))
        assertEquals(16, le16(header, 34))
        assertEquals("data size is the frame bytes", 320, le32(header, 40))
        assertEquals("and RIFF's own size excludes its first 8 bytes", 36 + 320, le32(header, 4))
    }

    @Test
    fun `a finished take is a playable file whose sizes match what was appended`() {
        val file = tempFile()
        val take = TakeRecorder(file)

        // Two buffers, as the capture loop reads them (a 100 ms buffer is 1600 samples).
        take.append(ByteArray(3_200) { 1 })
        take.append(ByteArray(1_600) { 2 })
        val finished = take.finish()

        assertNotNull("the take is playable", finished)
        assertTrue(file.exists())
        assertEquals("44-byte header + every appended byte", 44L + 4_800L, file.length())

        val bytes = file.readBytes()
        assertEquals("the data chunk is patched to the real size", 4_800, le32(bytes, 40))
        assertEquals("and so is RIFF's", 36 + 4_800, le32(bytes, 4))
        assertEquals("the first buffer's first sample landed", 1, bytes[44].toInt())
        assertEquals("and the last one as well", 2, bytes[44 + 4_800 - 1].toInt())
        file.delete()
    }

    @Test
    fun `a take that captured nothing leaves no file behind`() {
        // An empty WAV plays as a failure, which reads to the learner as a broken
        // button — so the screen offers no playback at all instead.
        val file = tempFile()
        val take = TakeRecorder(file)
        take.append(ByteArray(0))

        assertNull("nothing to play", take.finish())
        assertFalse("and nothing left on disk", file.exists())
    }

    @Test
    fun `discarding a take deletes it, and a discarded take cannot be finished later`() {
        val file = tempFile()
        val take = TakeRecorder(file)
        take.append(ByteArray(400))

        take.discard()

        assertFalse(file.exists())
        assertFalse("an abandoned take is closed", take.isOpen)
        assertNull("and cannot be handed to the player", take.finish())
    }

    @Test
    fun `a new take starts from an empty file, not on top of the previous one`() {
        // Two takes in a row on the same path (the cache file is fixed): the second
        // must not contain the first, or the learner hears words from two sentences.
        val file = tempFile()
        TakeRecorder(file).apply {
            append(ByteArray(1_000) { 9 })
            finish()
        }
        val second = TakeRecorder(file)
        second.append(ByteArray(200) { 7 })
        second.finish()

        val bytes = file.readBytes()
        assertEquals("the file is the second take alone", 44L + 200L, file.length())
        assertEquals(200, le32(bytes, 40))
        assertEquals(7, bytes[44].toInt())
        file.delete()
    }

    @Test
    fun `a take whose directory does not exist yet still writes`() {
        // The cache dir always exists on a device, but the recorder must not be the
        // thing that fails if it does not: the verdict has to survive a dead path.
        val dir = File(System.getProperty("java.io.tmpdir"), "lingo-take-" + System.nanoTime())
        val file = File(dir, "speech_take.wav")
        val take = TakeRecorder(file)
        take.append(ByteArray(100))

        val finished = take.finish()

        assertNotNull(finished)
        assertTrue(file.exists())
        dir.deleteRecursively()
    }
}
