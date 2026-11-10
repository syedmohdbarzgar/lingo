package org.token.english

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.token.english.core.common.ContentFailure
import org.token.english.core.common.ContentStatus
import org.token.english.di.AppContainer

/**
 * The content recovery path that no screen test reaches (checklist P1): the shard of
 * the app that runs before any screen exists.
 *
 * `ContentSeederTest` covers the seeder's own failure modes on a fake — a missing
 * asset, a malformed one, a crash between the bundle write and its stamp. This one
 * covers the outermost one: **the database file itself cannot be opened**. On a real
 * install that happens after a bad restore, a full disk or a killed write, and the
 * old shape of the code would have thrown inside a background coroutine — a crash at
 * launch on every start, before the first screen.
 *
 * What must hold now: the write's failure is a *state* ([ContentStatus.FAILED]) the
 * UI can render with a retry, and it is never left in [ContentStatus.LOADING], where
 * every screen would wait forever on a bundle that will never arrive. The failure is
 * also *classified* ([ContentFailure.DATABASE_UNREADABLE]): this is the one case a
 * retry cannot fix, so it is the only one that may offer the rebuild on Home.
 *
 * And the rebuild has to actually work. `repairUnreadableDatabase()` moves the file
 * aside — it never deletes it — so the next start builds a fresh database; the second
 * container below is that start, and it must end in [ContentStatus.READY]. If a future
 * change ever makes the repair delete instead of move, or restart without moving, one
 * of these two assertions fails.
 *
 * One practical note for whoever extends this: Robolectric does **not** reproduce a
 * corrupt SQLite *file*. A garbage `english.db` written here was opened as a fresh, empty
 * database and the seed then succeeded (in ~21 s, the whole bundle). What can be made to
 * fail is the path itself — a directory where the file should be — which is why the test
 * uses that and asserts the assets are readable first, so a `FAILED` cannot come from the
 * asset read instead.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ContentRecoveryTest {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `a database that cannot be opened ends the seed in FAILED, not a crash or a spinner`() =
        runBlocking {
            val context: Context = RuntimeEnvironment.getApplication()

            // The bundle has to be readable, or the failure asserted below would be the
            // asset read and this test would pass while proving nothing about Room.
            val bundleReadable = context.assets.open("content/lessons.json")
                .use { it.readBytes().isNotEmpty() }
            assertTrue("the bundled assets must be readable here", bundleReadable)

            // A path SQLite cannot open as a database file: the first query throws.
            val file = context.getDatabasePath("english.db")
            file.delete()
            file.parentFile?.mkdirs()
            assertTrue("the unusable path must really be there", file.mkdirs() || file.isDirectory)

            val container = AppContainer(context, scope)

            assertFalse("the seed reports its failure instead of throwing", container.seedContent())
            assertEquals(
                "and the screen gets a state to render — never a load that never ends",
                ContentStatus.FAILED,
                container.contentStatus.value,
            )
            assertEquals(
                "the database itself is the reason, which is what earns the rebuild on Home",
                ContentFailure.DATABASE_UNREADABLE,
                container.contentFailure.value,
            )

            // Pressing retry again is the same answer, and still does not hang or crash:
            // nothing in the app replaces a learner's file behind their back.
            assertFalse(container.seedContent())
            assertEquals(ContentStatus.FAILED, container.contentStatus.value)
            assertEquals(
                "a retry that fails again is still classified the same way",
                ContentFailure.DATABASE_UNREADABLE,
                container.contentFailure.value,
            )

            // The button can be pressed as often as the learner likes: every attempt
            // must still end in a state, never in LOADING, and never in a different
            // classification (checklist P1: "run the retry several times").
            repeat(3) {
                assertFalse(container.seedContent())
                assertEquals(ContentStatus.FAILED, container.contentStatus.value)
                assertEquals(ContentFailure.DATABASE_UNREADABLE, container.contentFailure.value)
            }

            // The repair the learner confirms on Home: the unusable path is moved into
            // the backup-excluded folder, never deleted…
            assertTrue("the repair moves the file aside", container.repairUnreadableDatabase())
            assertFalse("the database path is free again", file.exists())
            val aside = context.noBackupFilesDir.resolve(AppContainer.CORRUPT_DATABASE_DIR)
            assertTrue(
                "the damaged file is kept, so support can still read it",
                aside.listFiles().orEmpty().any { it.name.startsWith(AppContainer.DATABASE_NAME) },
            )

            // …and the next start (the container the restart builds) seeds normally,
            // which is what makes the rebuild a way out rather than a second dead end.
            val restarted = AppContainer(context, scope)
            assertTrue("the fresh database accepts the bundle", restarted.seedContent())
            assertEquals(ContentStatus.READY, restarted.contentStatus.value)
        }
}
