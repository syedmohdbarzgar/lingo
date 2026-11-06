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
