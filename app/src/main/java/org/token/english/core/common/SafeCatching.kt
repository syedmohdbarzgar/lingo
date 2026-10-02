package org.token.english.core.common

import kotlinx.coroutines.CancellationException

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
