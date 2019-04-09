package net.yslibrary.catlog

import android.database.Cursor

/**
 * Utility methods for [android.database.Cursor]
 */

object Cursors {

    fun getString(cursor: Cursor, column: String): String {
        return cursor.getString(cursor.getColumnIndex(column))
    }
}// no-op
