package org.token.english.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.token.english.R
import org.token.english.core.designsystem.AppSpacing
import org.token.english.domain.model.LearningLevel

/**
 * The bundled portrait for a CEFR band (one character per level, authored as
 * square artwork under `res/drawable-nodpi`). The artwork carries its own level
 * badge and English tagline, so it is decoration for a level, never the only
 * place the level is named — [LevelHeroCard] always prints the level in Persian
 * and in the badge as well.
 */
@DrawableRes
fun levelArtRes(level: LearningLevel): Int = when (level) {
    LearningLevel.A1 -> R.drawable.level_a1
    LearningLevel.A2 -> R.drawable.level_a2
    LearningLevel.B1 -> R.drawable.level_b1
    LearningLevel.B2 -> R.drawable.level_b2
    LearningLevel.C1 -> R.drawable.level_c1
    LearningLevel.C2 -> R.drawable.level_c2
}

/** Persian name of the band, as the published curriculum plan words it. */
fun levelNameFa(level: LearningLevel): String = when (level) {
    LearningLevel.A1 -> "مبتدی"
    LearningLevel.A2 -> "مقدماتی رو به متوسط"
    LearningLevel.B1 -> "متوسط"
    LearningLevel.B2 -> "متوسط رو به پیشرفته"
    LearningLevel.C1 -> "پیشرفته"
    LearningLevel.C2 -> "تسلط پیشرفته"
}

/** The one-sentence goal of the band — what the learner can do when it is done. */
fun levelGoalFa(level: LearningLevel): String = when (level) {
    LearningLevel.A1 -> "واژگان و عبارت‌های پایه برای ارتباطات روزمره."
    LearningLevel.A2 -> "مدیریت موقعیت‌های متداول زندگی و مکالمات ساده."
    LearningLevel.B1 -> "ارتباط مستقل در موقعیت‌های آشنا و بیان تجربه‌ها و نظرات."
    LearningLevel.B2 ->
        "بیان دقیق‌تر نظرات، درک متون پیچیده‌تر و تعامل مؤثر در کار و اجتماع."
    LearningLevel.C1 -> "استفادهٔ دقیق، منعطف و حرفه‌ای از زبان در موضوعات پیچیده."
    LearningLevel.C2 -> "درک و تولید زبان بسیار دقیق، ظریف و متناسب با موقعیت."
}

/**
 * "Your level" — the band's portrait beside its badge, name and goal.
 *
 * Shared by Home and the Progress screen so the learner's level is introduced
 * once, in one shape, in both places: the artwork is what makes the level feel
 * like a place on a path rather than a label in a settings row. [extra] is the
 * screen's own line under the goal (Home passes the level's lesson count).
 */
@Composable
fun LevelHeroCard(
    level: LearningLevel,
    modifier: Modifier = Modifier,
    extra: String? = null,
) {
    AppCard(modifier) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Image(
                painter = painterResource(levelArtRes(level)),
                // Decoration: the level is named in text right next to it.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialTheme.shapes.medium),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Text(
                    text = "سطح شما",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    CefrBadge(level)
                    Text(
                        text = levelNameFa(level),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                Text(
                    text = levelGoalFa(level),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (extra != null) {
                    Text(
                        text = extra,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
