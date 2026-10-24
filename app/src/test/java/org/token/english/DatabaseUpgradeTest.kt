package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.local.AppDatabase
import org.token.english.data.local.DATABASE_VERSION
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Room upgrade with real learner data (checklist v8, priority 1).
 *
 * `RoomMigrationTest` proves the migration SQL matches the exported schemas — a
 * structural check. This one *executes* the whole chain against a real SQLite
 * engine: it builds the oldest schema, fills it with learner rows (progress,
 * review schedule, mastery, study time), upgrades it to the shipped version, and
 * asserts every value is still there and the added columns carry their documented
 * defaults.
 *
 * That is the failure an upgrading install actually suffers (AGENTS.md §10): a
 * forgotten migration crashes at open time, a destructive one silently wipes the
 * learner's progress — and neither is visible to a fresh install.
 *
 * SQLite here is the host JVM driver (`org.xerial:sqlite-jdbc`, test-only), not
 * Android's; the statements are plain SQL and both engines accept them. A v1
 * install is reconstructed by undoing the additive `1->2` step, because schema
 * export only began at v2 (the test fails loudly if a shipped step is not purely
 * additive, which would make that reconstruction a lie).
 */
class DatabaseUpgradeTest {

    private val schemaDir = listOf(
        File("schemas/org.token.english.data.local.AppDatabase"),
        File("app/schemas/org.token.english.data.local.AppDatabase"),
    ).firstOrNull { it.isDirectory } ?: error("exported Room schemas not found")

    private fun schema(version: Int): JSONObject =
        JSONObject(File(schemaDir, "$version.json").readText()).getJSONObject("database")

    private data class Column(val name: String, val affinity: String)

    private fun entities(version: Int): Map<String, List<Column>> {
        val list = schema(version).getJSONArray("entities")
        return (0 until list.length()).associate { i ->
            val entity = list.getJSONObject(i)
            val fields = entity.getJSONArray("fields")
            entity.getString("tableName") to (0 until fields.length()).map { f ->
                val field = fields.getJSONObject(f)
                Column(
                    name = field.getString("columnName"),
                    affinity = field.getString("affinity"),
                )
            }
        }
    }

    /**
     * The statements Room itself would run to create this version from nothing.
     *
     * Room's exported `createSql` keeps the `${TABLE_NAME}` placeholder (only
     * room-testing substitutes it), and SQLite happily accepts `` `\${TABLE_NAME}` ``
     * as a quoted identifier — so an un-substituted run creates one table of that
     * name and every later statement fails with "no such table".
     */
    private fun createSql(version: Int): List<String> {
        val db = schema(version)
        val entities = db.getJSONArray("entities")
        val creates = (0 until entities.length()).map { i ->
            val entity = entities.getJSONObject(i)
            substituteTableName(entity.getString("createSql"), entity.getString("tableName"))
        }
        val indices = (0 until entities.length()).flatMap { i ->
            val entity = entities.getJSONObject(i)
            val list = entity.optJSONArray("indices") ?: return@flatMap emptyList<String>()
            (0 until list.length()).map { k ->
                substituteTableName(list.getJSONObject(k).getString("createSql"), entity.getString("tableName"))
            }
        }
        val setup = db.optJSONArray("setupQueries")
        val extra = if (setup == null) emptyList() else (0 until setup.length()).map { setup.getString(it) }
        return creates + indices + extra
    }

    private fun substituteTableName(sql: String, tableName: String): String =
        sql.replace("\${TABLE_NAME}", tableName).replace("\$TABLE_NAME", tableName)

    private val addColumn = Regex("""ALTER TABLE (\w+) ADD COLUMN (\w+) .*""")

    /**
     * The v1 shape: v2 minus the columns the `1->2` step added. Every statement of
     * that step must be an ADD COLUMN for this to hold, which is asserted first.
     */
    private fun setUpOldestVersion(db: Connection) {
        val backToV1 = AppDatabase.migrationStatements.getValue("1->2")
        backToV1.forEach { statement ->
            assertTrue(
                "a v1 install cannot be reconstructed because `1->2` is not purely additive: $statement",
                addColumn.matches(statement.trim()),
            )
        }
        createSql(2).forEach { execSql(db, it) }
        backToV1.mapNotNull { addColumn.matchEntire(it.trim()) }.forEach { match ->
            execSql(db, "ALTER TABLE ${match.groupValues[1]} DROP COLUMN ${match.groupValues[2]}")
        }
    }

    private fun execSql(db: Connection, sql: String) {
        try {
            db.createStatement().use { it.execute(sql) }
        } catch (e: SQLException) {
            throw AssertionError("statement failed: $sql", e)
        }
    }

    private fun query(db: Connection, sql: String, read: (ResultSet) -> Any?): Any? =
        try {
            db.createStatement().use { statement -> statement.executeQuery(sql).use(read) }
        } catch (e: SQLException) {
            throw AssertionError("query failed: $sql", e)
        }

    /** Table names that exist in the open database. */
    @Suppress("UNCHECKED_CAST")
    private fun tablesIn(db: Connection): List<String> =
        query(db, "SELECT name FROM sqlite_master WHERE type='table'") { rs ->
            val names = mutableListOf<String>()
            while (rs.next()) names += rs.getString(1)
            names
        } as? List<String> ?: emptyList()

    /** One generated value per column, deterministic and type-appropriate. */
    private fun valueFor(table: String, column: String, affinity: String): Any = when (affinity) {
        "INTEGER" -> 7L
        "REAL" -> 1.25
        else -> "v:$table:$column"
    }

    private fun literal(value: Any): String = when (value) {
        is Long -> value.toString()
        is Double -> value.toString()
        else -> "'" + value.toString().replace("'", "''") + "'"
    }

    private fun read(rs: ResultSet, affinity: String): Any? {
        val raw = rs.getObject(1) ?: return null
        return when (affinity) {
            "INTEGER" -> (raw as Number).toLong()
            "REAL" -> (raw as Number).toDouble()
            else -> raw.toString()
        }
    }

    private fun count(db: Connection, table: String): Int = query(db, "SELECT COUNT(*) FROM $table") { rs ->
        if (rs.next()) rs.getInt(1) else 0
    } as? Int ?: 0

    @Test
    fun `learner data survives every upgrade step from the oldest schema to the shipped one`() {
        val tables = entities(2)
        val expected = mutableMapOf<String, Map<String, Any>>()
        DriverManager.getConnection("jdbc:sqlite::memory:").use { db ->
            setUpOldestVersion(db)

            // The reconstructed v1 install really has the learner tables — if this
            // fails, everything after it would be asserting nothing.
            val v1Tables = tables.keys
            val created = tablesIn(db)
            assertTrue(
                "the oldest schema was not created: missing ${v1Tables - created.toSet()}",
                created.containsAll(v1Tables),
            )

            // A v1 install the learner has actually used: one row per table, every
            // column filled (nullable ones too, so a wipe shows up as NULL).
            val droppedByStep = AppDatabase.migrationStatements.getValue("1->2")
                .mapNotNull { addColumn.matchEntire(it.trim())?.groupValues }
                .groupBy({ it[1] }, { it[2] })
            tables.forEach { (table, columns) ->
                val live = columns.filterNot { it.name in droppedByStep[table].orEmpty() }
                val values: Map<String, Any> = live.associate { it.name to valueFor(table, it.name, it.affinity) }
                expected[table] = values
                val names = live.joinToString(", ") { it.name }
                val literals = live.joinToString(", ") { literal(values.getValue(it.name)) }
                execSql(db, "INSERT INTO $table ($names) VALUES ($literals)")
            }

            // Every shipped step, in order — the same statements Room runs.
            for (version in 1 until DATABASE_VERSION) {
                AppDatabase.migrationStatements.getValue("$version->${version + 1}")
                    .forEach { execSql(db, it) }
            }

            // 1. No value moved, and no row disappeared.
            expected.forEach { (table, columns) ->
                val affinityOf = tables.getValue(table).associate { it.name to it.affinity }
                columns.forEach { (column, value) ->
                    val actual = query(db, "SELECT $column FROM $table") { rs ->
                        if (rs.next()) read(rs, affinityOf.getValue(column)) else null
                    }
                    assertEquals("$table.$column changed during the upgrade", value, actual)
                }
                assertEquals("rows were lost from $table", 1, count(db, table))
            }

            // 2. Columns added by a later step give existing rows their documented value.
            val introducedAt = query(db, "SELECT introducedAt FROM review_item") { rs ->
                rs.next(); rs.getLong(1)
            }
            assertEquals("an existing review card is not a 'new' card", 0L, introducedAt)
            val grammarTip = query(db, "SELECT grammarTipFa FROM lesson") { rs ->
                if (rs.next()) rs.getString(1) else null
            }
            assertNull("a pre-existing lesson has no authored tip yet", grammarTip)
            val explanation = query(db, "SELECT explanationFa FROM vocabulary") { rs ->
                if (rs.next()) rs.getString(1) else null
            }
            assertNull("a pre-existing word has no usage note yet", explanation)

            // 3. Tables introduced by later steps exist and arrive empty — the
            //    seeder fills content, the learner fills state (v6 adds the
            //    cross-session dimension rows, which arrive with no evidence).
            val after = entities(DATABASE_VERSION)
            val added = after.keys - tables.keys
            assertEquals(
                "the graph and cross-session steps introduce exactly the known tables",
                setOf("knowledge_item", "knowledge_state", "dimension_mastery"),
                added,
            )
            added.forEach { table ->
                assertEquals("$table must arrive empty", 0, count(db, table))
            }

            // 4. The upgraded database still has a usable column set: selecting every
            //    column of the shipped schema cannot fail, and the pre-existing tables
            //    still hold their row. (`added` tables are empty by design — step 3.)
            after.forEach { (table, columns) ->
                query(db, "SELECT ${columns.joinToString(", ") { it.name }} FROM $table") { rs ->
                    if (table !in added) assertTrue("$table lost its only row", rs.next())
                }
            }
        }
    }
}
