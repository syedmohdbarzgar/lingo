package org.token.english.core.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * [runCatching] that never swallows coroutine cancellation.
 *
 * A cancelled coroutine must stay cancelled — swallowing [CancellationException]
 * leaves jobs hanging and breaks structured concurrency. Use this around store
 * calls (billing) and anywhere a suspend block is wrapped in a Result.
 */
suspend fun <T> runCatchingCancellable(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}

/**
 * Reports a failure from `this` flow to [onFailure] instead of letting it end the
 * collector with nothing on screen.
 *
 * A Room read that throws (corrupt file, a migration the installed version cannot
 * walk) used to leave the screen on its spinner forever, because nothing ever set
 * `isLoading = false`. The screen now renders an error state that says the data is
 * local and offers a retry — see AGENTS §6: a local failure is never blamed on the
 * network.
 *
 * Cancellation passes straight through (B-9) — a cancelled screen is not an error.
 */
fun <T> Flow<T>.catchLoadFailure(onFailure: (Throwable) -> Unit): Flow<T> = catch { error ->
    if (error is CancellationException) throw error
    onFailure(error)
}
