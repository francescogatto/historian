package net.yslibrary.historian.internal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.util.Log.e

/**
 * SQLiteOpenHelper for Historian
 */

class DbOpenHelper(val context: Context, name: String) : SQLiteOpenHelper(context, name, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(LogTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var oldVersion = oldVersion
        if (oldVersion == 1) {
            db.execSQL(LogTable.DROP_TABLE)
            db.execSQL(LogTable.CREATE_TABLE)
            oldVersion++
        }
    }



    fun executeTransaction(transaction: Transaction) {
        try {
            writableDatabase.beginTransaction()
            transaction.call(writableDatabase)
            writableDatabase.setTransactionSuccessful()
        } catch (e: Exception ){
            Log.e("ERROR","Error while inserting", e)
        } finally {
            writableDatabase.endTransaction()
        }
    }

    interface Transaction {
        fun call(db: SQLiteDatabase)
    }

    companion object {
        private val DB_VERSION = 2
    }
}
