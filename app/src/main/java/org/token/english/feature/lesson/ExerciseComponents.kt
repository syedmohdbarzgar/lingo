package org.token.english.feature.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.EnglishText

/**
 * Speaking practice (the `speaking` item): hear the model from the on-device TTS,
 * record your own take, hear it back and compare. Nothing is graded and nothing
 * is uploaded — there is no recogniser offline, and the take lives in the app
 * cache (AGENTS §7: no network, no cloud speech).
 *
 * Every action is a labelled button, so the states carry themselves without
 * leaning on colour alone (design.md §50).
 */
@Composable
fun SpeakingCard(
    modelSentence: String,
    promptFa: String,
    isPlayingModel: Boolean,
    isListening: Boolean,
    isPreparing: Boolean,
    heardPartial: String,
    heardText: String,
    unheard: Boolean,
    interrupted: Boolean,
    canPlayTake: Boolean,
    isPlayingTake: Boolean,
    onPlayModel: () -> Unit,
    onPlayTake: () -> Unit,
    onStopTakePlayback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        if (promptFa.isNotBlank()) {
            Text(
                text = promptFa,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        // The model sentence is English: EnglishText re-asserts LTR inside the
        // forced-RTL root and pins the paragraph direction.
        AppCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                EnglishText(
                    text = modelSentence,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                TextButton(onClick = onPlayModel, enabled = !isPlayingModel) {
                    Text(if (isPlayingModel) "در حال پخش…" else "پخش نمونه")
                }
            }
        }

        // What the device actually heard is the comparison now: the recogniser's
        // transcript is shown as it accumulates, so the learner sees the words land
        // while they are still speaking instead of waiting for a verdict.
        val live = heardPartial.trim()
        if (isListening && live.isNotEmpty()) {
            AppCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Text(
                        text = "شنیدم:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    EnglishText(
                        text = live,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }

        if (!isListening && heardText.isNotBlank()) {
            AppCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Text(
                        text = "آنچه شنیده شد:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    EnglishText(
                        text = heardText,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    // The verdict was formed on these words, and this is the voice
                    // that said them — one take, heard and judged, not two passes.
                    if (canPlayTake) {
                        TextButton(onClick = if (isPlayingTake) onStopTakePlayback else onPlayTake) {
                            Text(if (isPlayingTake) "توقف پخش" else "پخش صدای خودت")
                        }
                    }
                }
            }
        }

        Text(
            text = when {
                isListening -> "در حال شنیدن — جمله را کامل و واضح بگو، بعد «پایان پاسخ» را بزن."
                isPreparing -> "مدل گفتار در حال آماده‌سازی است…"
                unheard -> "چیزی نشنیدم. یک بار دیگر، کمی نزدیک‌تر به میکروفون بگو."
                heardText.isNotBlank() -> "جمله‌ات با نمونه سنجیده شد؛ نتیجه در پایین آمده است."
                else -> "اول نمونه را گوش کن، بعد جمله را خودت بگو تا روی همین گوشی سنجیده شود."
            },
            style = MaterialTheme.typography.bodySmall.copy(
                textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        if (interrupted) {
            Text(
                text = "این نوبت شنیده نشد — میکروفون یا مدل گفتار در دسترس نیست. " +
                    "می‌توانی دوباره تلاش کنی یا بدون سنجش ادامه بدهی؛ هیچ چیز به اینترنت فرستاده نمی‌شود.",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

