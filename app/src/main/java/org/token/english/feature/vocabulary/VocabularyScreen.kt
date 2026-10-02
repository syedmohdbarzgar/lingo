package org.token.english.feature.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LearningTargetStyle
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppChip
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.CefrBadge
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.InfoBanner
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.appViewModelFactory
import org.token.english.domain.model.ReviewState
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(onOpenDetail: (String) -> Unit) {
    val vm: VocabularyViewModel = viewModel(factory = appViewModelFactory { VocabularyViewModel(it) })
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("واژگان", style = MaterialTheme.typography.titleMedium) }) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md),
                placeholder = { Text("جستجو در واژه‌ها…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                state.all.isEmpty() -> AppEmptyState(
                    icon = Icons.Default.MenuBook,
                    title = "هنوز واژه‌ای ذخیره نشده",
                    subtitle = "محتوای آفلاین در حال آماده‌سازی است. کمی بعد دوباره سر بزن.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                state.filtered.isEmpty() -> AppEmptyState(
                    icon = Icons.Default.Search,
                    title = "چیزی پیدا نشد",
                    subtitle = "واژه دیگری را جستجو کن.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                else -> LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = AppSpacing.md,
                        end = AppSpacing.md,
                        bottom = AppSpacing.lg,
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    items(state.filtered, key = { it.id }) { item ->
                        AppCard(onClick = { onOpenDetail(item.id) }) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                                    EnglishText(text = item.word, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = item.translation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                CefrBadge(item.level)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyDetailScreen(vocabId: String, onBack: () -> Unit) {
    val vm: VocabularyDetailViewModel =
        viewModel(factory = appViewModelFactory { VocabularyDetailViewModel(it, vocabId) })
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("واژه", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.item == null -> AppEmptyState(
                    icon = Icons.Default.MenuBook,
                    title = "واژه پیدا نشد",
                    subtitle = "این واژه در محتوای ذخیره‌شده وجود ندارد.",
                    actionText = "بازگشت",
                    onAction = onBack,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> {
                    val item = state.item!!
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(AppSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                    ) {
                        AppCard {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            ) {
                                EnglishText(
                                    text = item.word,
                                    style = LearningTargetStyle,
                                    textAlign = TextAlign.Center,
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                                ) {
                                    CefrBadge(item.level)
                                    item.partOfSpeech?.let { AppChip(label = it) }
                                    IconButton(onClick = vm::playWord) {
                                        Icon(
                                            Icons.Default.VolumeUp,
                                            contentDescription = "پخش تلفظ",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                                item.pronunciation?.let {
                                    EnglishText(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Text(
                                    text = item.translation,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                item.definition?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        if (item.examples.isNotEmpty()) {
                            AppCard {
                                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                    SectionHeader("در جمله")
                                    item.examples.forEach { example ->
                                        EnglishText(
                                            text = example,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                }
                            }
                        }

                        if (item.collocations.isNotEmpty()) {
                            AppCard {
                                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                    SectionHeader("ترکیب‌های رایج")
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        item.collocations.take(3).forEach { AppChip(label = it) }
                                    }
                                }
                            }
                        }

                        val review = state.reviewItem
                        if (review == null) {
                            PrimaryButton(
                                text = "افزودن به صف مرور",
                                onClick = vm::addToReview,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        } else {
                            val nextText = DateFormat.getDateInstance(DateFormat.MEDIUM)
                                .format(Date(review.dueAt))
                            InfoBanner(
                                message = when (review.state) {
                                    ReviewState.NEW -> "در صف مرور — اولین مرور: $nextText"
                                    ReviewState.MASTERED -> "تسلط بلندمدت — مرور بعدی: $nextText"
                                    else -> "در حال مرور — مرور بعدی: $nextText (فاصله ${review.intervalDays} روز)"
                                },
                            )
                        }

                        state.message?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
