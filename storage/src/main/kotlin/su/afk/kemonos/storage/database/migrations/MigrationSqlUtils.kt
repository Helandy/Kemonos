package su.afk.kemonos.storage.database.migrations

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase

internal fun SupportSQLiteDatabase.hasTable(tableName: String): Boolean {
    val sqlQuery = SimpleSQLiteQuery(
        "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1",
        arrayOf(tableName),
    )
    return query(sqlQuery).use { cursor -> cursor.moveToFirst() }
}

internal fun SupportSQLiteDatabase.hasColumn(
    tableName: String,
    columnName: String,
): Boolean {
    if (!hasTable(tableName)) return false

    return query("PRAGMA table_info(`$tableName`)").use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        if (nameIndex < 0) return@use false

        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex) == columnName) {
                return@use true
            }
        }
        false
    }
}

internal fun SupportSQLiteDatabase.addColumnIfMissing(
    tableName: String,
    columnName: String,
    definitionSql: String,
) {
    if (!hasTable(tableName) || hasColumn(tableName, columnName)) return
    execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnName` $definitionSql")
}
