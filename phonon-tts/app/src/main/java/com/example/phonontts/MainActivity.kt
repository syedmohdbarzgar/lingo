package com.example.phonontts

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.phonontts.databinding.ActivityMainBinding
import com.google.mediapipe.tasks.audio.texttospeech.VoiceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * One screen: type English, pick a voice, synthesize, play, stop.
 *
 * Offline end to end — the model is an asset and the audio never leaves the device, so
 * there is no permission to ask for and no failure that can be blamed on a network.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val ttsManager by lazy { PhononTtsManager(this) }
    private val audioPlayer = AudioPlayer()

    private var voices: List<VoiceInfo> = emptyList()
    private var synthesizeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Playback ended on its own: the screen returns to «آماده» and the play button
        // becomes usable again (a clip that finished must not leave it disabled).
        audioPlayer.onComplete = {
            runOnUiThread {
                setBusy(false)
                binding.stopButton.isEnabled = false
                setStatus(R.string.status_ready)
            }
        }

        binding.inputText.doAfterTextChanged {
            binding.playButton.isEnabled = !it.isNullOrBlank()
        }
        binding.playButton.isEnabled = false

        binding.playButton.setOnClickListener { playTypedText() }
        binding.stopButton.setOnClickListener { stopPlayback() }

        loadModel()
    }

    /**
     * Loading the bundle does not block the first frame: creating the task is fast
     * (the model is memory-mapped), but it still happens off the main thread so a slow
     * device shows «در حال بارگذاری مدل…» instead of a frozen launch.
     */
    private fun loadModel() {
        setStatus(R.string.status_loading_model)
        lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) {
                try {
                    ttsManager.initialize()
                    Result.success(ttsManager.availableVoices())
                } catch (error: Exception) {
                    Result.failure<List<VoiceInfo>>(error)
                }
            }
            result.onSuccess { available ->
                voices = available
                bindVoices(available)
                if (available.isEmpty()) {
                    setStatus(R.string.status_empty_audio)
                } else {
                    setStatus(R.string.status_ready)
                }
            }.onFailure { error ->
                if (error is PhononTtsManager.ModelMissingException) {
                    setStatus(R.string.error_model_missing)
                } else {
                    setStatus(getString(R.string.error_generic, error.message ?: "init"))
                }
            }
            // The EditText restores its text without a change event, so the enabled state
            // is recomputed from the field rather than trusted to the listener.
            setBusy(false)
            binding.stopButton.isEnabled = false
        }
    }

    /** The spinner lists what the bundle actually ships — never a hardcoded voice list. */
    private fun bindVoices(available: List<VoiceInfo>) {
        val labels = available.map { voice ->
            if (voice.displayName.isNullOrBlank()) voice.voiceId else "${voice.voiceId} — ${voice.displayName}"
        }
        binding.voiceSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            labels,
        )
        binding.voiceSpinner.isEnabled = available.isNotEmpty()
    }

    /** The voice id of the current selection, or null when the bundle had no voices. */
    private fun selectedVoiceId(): String? =
        voices.getOrNull(binding.voiceSpinner.selectedItemPosition)?.voiceId

    private fun playTypedText() {
        val text = binding.inputText.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) {
            setStatus(R.string.status_empty_text)
            return
        }
        // A second tap replaces the first sentence: one voice at a time.
        stopPlayback()
        setStatus(R.string.status_synthesizing)
        setBusy(true)

        synthesizeJob = lifecycleScope.launch {
            val result = runCatching {
                ttsManager.synthesize(text = text, voiceId = selectedVoiceId())
            }
            result.onSuccess { audio ->
                if (audio == null || audio.samples.isEmpty()) {
                    setStatus(R.string.status_empty_audio)
                    setBusy(false)
                    return@onSuccess
                }
                setStatus(R.string.status_playing)
                binding.stopButton.isEnabled = true
                binding.playButton.isEnabled = false
                audioPlayer.play(audio.samples, audio.sampleRateHz)
            }.onFailure { error ->
                setStatus(getString(R.string.error_generic, error.message ?: "synthesize"))
                setBusy(false)
            }
        }
    }

    private fun stopPlayback() {
        synthesizeJob?.cancel()
        synthesizeJob = null
        ttsManager.cancel()
        audioPlayer.stop()
        setBusy(false)
        binding.stopButton.isEnabled = false
        setStatus(R.string.status_stopped)
    }

    /** Disables the input that would start a second synthesis while one is running. */
    private fun setBusy(busy: Boolean) {
        binding.playButton.isEnabled = !busy && !binding.inputText.text.isNullOrBlank()
        binding.voiceSpinner.isEnabled = !busy && voices.isNotEmpty()
    }

    private fun setStatus(resId: Int) {
        binding.statusText.text = getString(resId)
    }

    private fun setStatus(text: String) {
        binding.statusText.text = text
    }

    override fun onStop() {
        // A clip must not keep speaking from a screen the learner left.
        synthesizeJob?.cancel()
        ttsManager.cancel()
        audioPlayer.stop()
        super.onStop()
    }

    override fun onDestroy() {
        audioPlayer.stop()
        ttsManager.close()
        super.onDestroy()
    }
}
