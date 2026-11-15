package com.example.phonontts

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

/**
 * Plays the PCM the task returns: 24 kHz, mono, `ENCODING_PCM_FLOAT`.
 *
 * `MODE_STATIC` with the whole clip in one buffer is right for an MVP that synthesizes
 * first and plays second — the sample rate comes from the result rather than a constant,
 * because the task reports what it produced instead of the app assuming 24000.
 */
class AudioPlayer {

    private var audioTrack: AudioTrack? = null

    /** Called on the audio thread when playback finishes on its own (not after [stop]). */
    @Volatile
    var onComplete: (() -> Unit)? = null

    fun play(samples: FloatArray, sampleRateHz: Int) {
        stop()
        if (samples.isEmpty()) return

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRateHz)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(samples.size * Float.SIZE_BYTES)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
        // The end-of-clip hook is what returns the screen to «آماده» without a timer.
        track.setNotificationMarkerPosition(samples.size)
        track.setPlaybackPositionUpdateListener(
            object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(track: AudioTrack?) {
                    onComplete?.invoke()
                }

                override fun onPeriodicNotification(track: AudioTrack?) = Unit
            },
        )
        track.play()
        audioTrack = track
    }

    fun stop() {
        audioTrack?.let { track ->
            runCatching { track.pause() }
            runCatching { track.flush() }
            runCatching { track.stop() }
            runCatching { track.release() }
        }
        audioTrack = null
    }
}
