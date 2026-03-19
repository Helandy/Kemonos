package su.afk.kemonos.storage.database.migrations

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

internal object DestructiveMigrationPrefSync {
    private const val FLAGS_TABLE = "db_migration_flags"
    private const val FLAG_DESTRUCTIVE_REBUILD = "destructive_rebuild"

    fun markDestructiveRebuild(db: SupportSQLiteDatabase, scope: String) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `$FLAGS_TABLE` (" +
                    "`scope` TEXT NOT NULL, " +
                    "`flag` TEXT NOT NULL, " +
                    "PRIMARY KEY(`scope`, `flag`))"
        )
        db.execSQL(
            "INSERT OR REPLACE INTO `$FLAGS_TABLE` (`scope`, `flag`) " +
                    "VALUES ('$scope', '$FLAG_DESTRUCTIVE_REBUILD')"
        )
    }

    fun createCleanupCallback(
        scope: String,
        prefs: SharedPreferences,
        keysToClearOnDestructiveRebuild: List<String>,
        keysToClearWhenTableEmpty: Map<String, List<String>> = emptyMap(),
    ): RoomDatabase.Callback = object : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            val keysToClear = linkedSetOf<String>()

            if (consumeDestructiveRebuild(db, scope)) {
                keysToClear += keysToClearOnDestructiveRebuild
            }

            keysToClearWhenTableEmpty.forEach { (tableName, keys) ->
                if (isTableEmpty(db, tableName)) {
                    keysToClear += keys
                }
            }

            if (keysToClear.isEmpty()) return

            prefs.edit {
                keysToClear.forEach(::remove)
            }
        }
    }

    private fun consumeDestructiveRebuild(
        db: SupportSQLiteDatabase,
        scope: String,
    ): Boolean {
        val hasFlagsTable = db.query(
            "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '$FLAGS_TABLE' LIMIT 1"
        ).use { cursor ->
            cursor.moveToFirst()
        }
        if (!hasFlagsTable) return false

        val shouldClearPrefs = db.query(
            "SELECT 1 FROM `$FLAGS_TABLE` " +
                    "WHERE `scope` = '$scope' AND `flag` = '$FLAG_DESTRUCTIVE_REBUILD' LIMIT 1"
        ).use { cursor ->
            cursor.moveToFirst()
        }
        if (!shouldClearPrefs) return false

        db.execSQL(
            "DELETE FROM `$FLAGS_TABLE` " +
                    "WHERE `scope` = '$scope' AND `flag` = '$FLAG_DESTRUCTIVE_REBUILD'"
        )
        return true
    }

    private fun isTableEmpty(
        db: SupportSQLiteDatabase,
        tableName: String,
    ): Boolean = db.query("SELECT 1 FROM `$tableName` LIMIT 1").use { cursor ->
        !cursor.moveToFirst()
    }
}
