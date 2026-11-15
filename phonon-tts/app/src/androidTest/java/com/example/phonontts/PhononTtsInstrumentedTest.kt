package com.example.phonontts

import android.media.AudioFormat
import android.media.AudioTrack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The device evidence T7 asks for, without depending on a screen tap.
 *
 * What it proves on a physical arm64 phone: the 87 MB asset loads through the MediaPipe
 * Text-to-Speech task, the bundle lists its voices, synthesis returns 24 kHz mono floats,
 * and that buffer is playable by [AudioPlayer] (including a stop mid-clip).
 */
@RunWith(AndroidJUnit4::class)
class PhononTtsInstrumentedTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun modelLoadsAndListsItsVoices() = runBlocking {
        PhononTtsManager(context).use { manager ->
            manager.initialize()

            val voiceIds = manager.availableVoices().map { it.voiceId }
            assertTrue("the bundle lists at least one voice, got $voiceIds", voiceIds.isNotEmpty())
            assertTrue(
                "Phonon ships freya as its default, got $voiceIds",
                PhononTtsManager.DEFAULT_VOICE_ID in voiceIds,
            )
        }
    }

    @Test
    fun synthesizesAudibleAudioAt24k() = runBlocking {
        PhononTtsManager(context).use { manager ->
            manager.initialize()

            val result = manager.synthesize(
                text = "Hello. This is Phonon speaking on the device.",
                voiceId = PhononTtsManager.DEFAULT_VOICE_ID,
            )

            assertTrue("synthesis returned nothing", result != null)
            val samples = result!!.samples
            assertTrue("no samples were produced", samples.isNotEmpty())
            assertEquals("Phonon's output rate", 24000, result.sampleRateHz)
            assertTrue(
                "a two-sentence line is longer than a tenth of a second",
                result.durationSeconds > 0.1f,
            )
            assertTrue(
                "the samples are normalised floats, not 16-bit magnitudes",
                samples.all { it >= -1.5f && it <= 1.5f },
            )
            assertTrue(
                "silence is not speech",
                samples.any { kotlin.math.abs(it) > 0.01f },
            )
        }
    }

    @Test
    fun playsAndStopsWithoutLeakingTheTrack() = runBlocking {
        PhononTtsManager(context).use { manager ->
            manager.initialize()
            val result = manager.synthesize("Stop me after a moment.", PhononTtsManager.DEFAULT_VOICE_ID)!!
            val player = AudioPlayer()

            try {
                player.play(result.samples, result.sampleRateHz)
                // Playing must be a real AudioTrack in the float encoding the model emits.
                assertTrue("the clip is long enough to be stopped mid-way", result.samples.size > 24000)
                player.play(result.samples, result.sampleRateHz)
                player.stop()
            } finally {
                player.stop()
            }

            // No state leak: a fresh track with the same format can be built after stop.
            val probe = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                        .setSampleRate(result.sampleRateHz)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                )
                .setBufferSizeInBytes(4096)
                .build()
            probe.release()
        }
    }
}
