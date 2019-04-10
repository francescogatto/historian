package net.francescogatto.catlog.internal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import timber.log.Timber

/**
 * SQLiteOpenHelper for CatLog
 */

class DbOpenHelper(val context: Context, name: String) : SQLiteOpenHelper(context, name, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(LogTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL(LogTable.DROP_TABLE)
            db.execSQL(LogTable.CREATE_TABLE)
        }
    }


    fun executeTransaction(transaction: Transaction) {
        with(writableDatabase) {
            try {
                beginTransaction()
                transaction.call(this)
                setTransactionSuccessful()
            } catch (e: Exception) {
                Timber.e(e, "Error while inserting")
            } finally {
                endTransaction()
            }
        }
    }

    interface Transaction {
        fun call(db: SQLiteDatabase)
    }

    companion object {
        private const val DB_VERSION = 2
    }
}
