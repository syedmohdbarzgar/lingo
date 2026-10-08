package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.local.AppDatabase
import org.token.english.data.local.DATABASE_VERSION
import java.io.File

/**
 * Room migration safety (checklist P0).
 *
 * Room only validates a migration when it actually runs, on an *upgrading* install —
 * a missing or wrong step crashes the app at open time (AGENTS.md §10), and that
 * path is invisible to a fresh install. These tests execute in the JVM: they read
 * the exported schemas in `app/schemas` and check that
 *
 *  1. every version step the schemas describe has a migration, with no gaps, and
 *  2. each step's SQL actually produces the schema delta of that step — every new
 *     table is created, every new column is added, and nothing is added twice.
 *
 * A device-level `MigrationTestHelper` run would additionally exercise SQLite
 * itself; this catches the failure class that ships silently.
 */
class RoomMigrationTest {

    private val schemaDir = listOf(
        File("schemas/org.token.english.data.local.AppDatabase"),
        File("app/schemas/org.token.english.data.local.AppDatabase"),
    ).firstOrNull { it.isDirectory }
        ?: error("exported Room schemas not found")

    private fun schemaFile(version: Int): File =
        schemaDir.listFiles().orEmpty()
            .firstOrNull { it.name == "$version.json" }
            ?: error("no exported schema for version $version in ${schemaDir.path}")

    private fun schema(version: Int): JSONObject =
        JSONObject(schemaFile(version).readText())

    private fun availableVersions(): List<Int> =
        schemaDir.listFiles().orEmpty()
            .mapNotNull { it.name.removeSuffix(".json").toIntOrNull() }
            .sorted()

    /** tableName → its columns, from the exported schema of [version]. */
    private fun tables(version: Int): Map<String, Set<String>> {
        val entities = schema(version).getJSONObject("database").getJSONArray("entities")
        return (0 until entities.length()).associate { i ->
            val entity = entities.getJSONObject(i)
            val fields = entity.getJSONArray("fields")
            entity.getString("tableName") to (0 until fields.length())
                .map { fields.getJSONObject(it).getString("columnName") }
                .toSet()
        }
    }

    private val addColumn = Regex("""ALTER TABLE (\w+) ADD COLUMN (\w+).*""")
    private val createTable = Regex("""CREATE TABLE IF NOT EXISTS (\w+).*""")

    private data class AddedColumn(val table: String, val column: String)

    private fun addedColumns(statements: List<String>): List<AddedColumn> =
        statements.mapNotNull { statement ->
            addColumn.matchEntire(statement.trim())?.let {
                AddedColumn(it.groupValues[1], it.groupValues[2])
            }
        }

    private fun createdTables(statements: List<String>): Set<String> =
        statements.mapNotNull { createTable.matchEntire(it.trim())?.groupValues?.get(1) }.toSet()

    @Test
    fun `every exported schema step has a migration`() {
        val versions = availableVersions()
        assertTrue("no exported schemas found", versions.isNotEmpty())

        for (version in versions.dropLast(1)) {
            val key = "$version->${version + 1}"
            assertTrue(
                "schema $version -> ${version + 1} exists but no migration covers it",
                AppDatabase.migrationStatements.containsKey(key),
            )
        }
    }

    @Test
    fun `the shipped migrations form one gap-free chain to the declared version`() {
        // Schemas are exported from whichever version first enabled exportSchema, so
        // this checks the chain itself rather than the exported file set: consecutive
        // steps from 1 to DATABASE_VERSION with nothing missing in between.
        val froms = AppDatabase.migrationStatements.keys
            .map { it.substringBefore("->").toInt() }
            .sorted()
        assertEquals(
            "every version must have a migration into the next one, with no gaps",
            (1 until DATABASE_VERSION).toList(),
            froms,
        )
        AppDatabase.migrationStatements.keys.forEach { key ->
            val from = key.substringBefore("->").toInt()
            assertEquals("a migration step must move exactly one version", key, "$from->${from + 1}")
        }
        assertEquals(
            "ALL_MIGRATIONS must expose every recorded step",
            AppDatabase.migrationStatements.size,
            AppDatabase.ALL_MIGRATIONS.size,
        )
    }

    @Test
    fun `each migration produces the schema delta of its step`() {
        val versions = availableVersions()
        for (version in versions.dropLast(1)) {
            val statements = AppDatabase.migrationStatements.getValue("$version->${version + 1}")
            assertTrue("migration $version->${version + 1} has no statements", statements.isNotEmpty())

            val before = tables(version)
            val after = tables(version + 1)
            val created = createdTables(statements)
            val added = addedColumns(statements)

            // 1. New tables must be created by the step.
            val newTables = after.keys - before.keys
            assertEquals(
                "tables introduced by $version->${version + 1} must be created: $newTables",
                newTables,
                created.intersect(newTables),
            )

            // 2. New columns on a surviving table must be added by the step.
            for (table in before.keys intersect after.keys) {
                val gained = after.getValue(table) - before.getValue(table)
                val covered = added.filter { it.table == table }.map { it.column }.toSet()
                assertTrue(
                    "columns added to \"$table\" in $version->${version + 1} are not all in the " +
                        "migration: missing ${gained - covered}",
                    gained.all { it in covered },
                )
                // 3. Nothing is silently dropped: a removed column needs a DROP (none shipped yet).
                val removed = before.getValue(table) - after.getValue(table)
                assertTrue(
                    "columns removed from \"$table\" without a DROP statement: $removed",
                    removed.isEmpty(),
                )
            }

            // 4. A migration must not add a column that already exists (it would crash
            //    with "duplicate column name" on a real upgrade).
            for (column in added) {
                assertTrue(
                    "migration $version->${version + 1} adds \"${column.column}\" to " +
                        "\"${column.table}\", which already has it",
                    column.column !in before[column.table].orEmpty(),
                )
            }

            // 5. No statement may touch a table that does not exist in either schema.
            for (column in added) {
                assertTrue(
                    "migration $version->${version + 1} alters unknown table \"${column.table}\"",
                    column.table in before || column.table in after,
                )
            }
        }
    }

    @Test
    fun `the declared database version matches the newest exported schema`() {
        val newest = availableVersions().max()
        assertEquals(
            "the newest exported schema must describe itself with its own version",
            newest,
            schema(newest).getJSONObject("database").getInt("version"),
        )
        assertEquals(
            "@Database(version = ...) must equal the newest exported schema — bump it with a schema change",
            newest,
            DATABASE_VERSION,
        )
        val highestTarget = AppDatabase.migrationStatements.keys
            .map { it.substringAfter("->").toInt() }
            .max()
        assertEquals(
            "the migrations must reach the declared version",
            DATABASE_VERSION,
            highestTarget,
        )
    }
}
