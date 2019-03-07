package net.yslibrary.historian.internal

import android.database.sqlite.SQLiteDatabase


/**
 * Class for log writing operation
 */

class LogWriterDB(private val dbOpenHelper: DbOpenHelper, private val size: Long) : LogWriter {


    override fun log(log: LogEntity) {

        dbOpenHelper.executeTransaction(object : DbOpenHelper.Transaction {
            override fun call(db: SQLiteDatabase) {
                // insert provided log
                val insertStatement = db.compileStatement(LogTable.INSERT)
                insertStatement.bindString(1, log.priority)
                insertStatement.bindString(2, log.tag)
                insertStatement.bindString(3, log.message)
                insertStatement.bindLong(4, log.timestamp)
                insertStatement.execute()

                // delete if row count exceeds provided size
                val deleteStatement = db.compileStatement(LogTable.DELETE_OLDER)
                deleteStatement.bindLong(1, size)
                deleteStatement.execute()
            }
        })
    }


    /**
     * Clear logs in SQLite.
     */
    override fun delete() {
        dbOpenHelper.executeTransaction(object : DbOpenHelper.Transaction {
            override fun call(db: SQLiteDatabase) {
                db.delete(LogTable.NAME, null, arrayOf())
            }
        })
    }


}
