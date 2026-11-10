package org.token.english.core.common

/**
 * Where the offline curriculum bundle stands for this process: still being written
 * into Room ([LOADING]), written ([READY]), or the write failed ([FAILED]).
 *
 * A failure here is **not** a network problem — the bundle ships inside the APK, so
 * the screen must say so and offer a retry instead of blaming the connection
 * (AGENTS §6, docs/connectivity.md).
 */
enum class ContentStatus { LOADING, READY, FAILED }

/**
 * Why the write failed, when it did — the screen cannot offer the right way out
 * without it.
 *
 * A retry is the right answer for an asset that could not be read or a parse that
 * threw: the same work may well succeed. It is the wrong answer for a database Room
 * cannot open at all (a half-restored file, a path that is not a file, a disk that
 * refuses the write) — that retry is honest and terminal, so the failure state has
 * to name it and offer the one thing that can work: rebuilding the database.
 */
enum class ContentFailure {
    /** Room cannot open `english.db` at all. Only a rebuild can get past this. */
    DATABASE_UNREADABLE,

    /** Anything else — a missing or malformed asset, a write that may work next time. */
    OTHER,
}
