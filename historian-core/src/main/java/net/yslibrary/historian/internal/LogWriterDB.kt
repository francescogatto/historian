package net.yslibrary.historian.internal

/**
 * Class for log writing operation
 */

class LogWriterDB(private val dbOpenHelper: DbOpenHelper, private val size: Int) : LogWriter {


  override fun log(log: LogEntity) {

    dbOpenHelper.executeTransaction { db ->
      // insert provided log
      db.compileStatement(LogTable.INSERT).also {
        it.bindString(1, log.priority)
        it.bindString(2, log.tag ?: "")
        it.bindString(3, log.message)
        it.bindLong(4, log.timestamp)
        it.execute()
      }

      // delete if row count exceeds provided size
      val deleteStatement = db.compileStatement(LogTable.DELETE_OLDER)
      with(deleteStatement) {
        bindLong(1, size.toLong())
        execute()
      }
    }

    LogWriterFile(dbOpenHelper.context, size).log(log)
  }


  /**
   * Clear logs in SQLite.
   */
  override fun delete() {
    dbOpenHelper.executeTransaction { db -> db.delete(LogTable.NAME, null, arrayOf()) }
  }


}
