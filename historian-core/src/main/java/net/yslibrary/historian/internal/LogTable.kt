package net.yslibrary.historian.internal

/**
 * Table definition of Log
 */

internal object LogTable {

    val NAME = "log"

    val CREATE_TABLE = StringBuilder()
            .append("CREATE TABLE ").append(NAME)
            .append(" (")
            .append("id INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append("priority TEXT NOT NULL, ")
            .append("tag TEXT NOT NULL, ")
            .append("message TEXT NOT NULL, ")
            .append("created_at INTEGER NOT NULL")
            .append(");")
            .toString()

    val DROP_TABLE = StringBuilder()
            .append("DROP TABLE ").append(NAME)
            .append(";")
            .toString()

    val INSERT = StringBuilder()
            .append("INSERT INTO ").append(NAME)
            .append("(priority, tag, message, created_at) ")
            .append("VALUES(?, ?, ?, ?);")
            .toString()

    val DELETE_OLDER = StringBuilder()
            .append("DELETE FROM ").append(NAME)
            .append(" where id NOT IN (")
            .append("SELECT id FROM log ORDER BY created_at DESC LIMIT ?")
            .append(");")
            .toString()
}// no-op
