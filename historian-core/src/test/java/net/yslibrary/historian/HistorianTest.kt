package net.yslibrary.historian

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import org.junit.Assert.assertEquals

/**
 * Created by yshrsmz on 2017/01/22.
 */
@RunWith(ConfiguredRobolectricTestRunner::class)
class HistorianTest {

    private var historian: Historian? = null

    @Before
    fun setup() {
        val context = RuntimeEnvironment.application

        historian = Historian.builder(context).build()
    }

    @Test(expected = IllegalStateException::class)
    fun initialize_not_called() {
        historian!!.log(Log.DEBUG, TAG, "this is debug1")
    }

    @Test
    fun log_queue_under_logLevel() {
        historian!!.initialize()

        historian!!.log(Log.VERBOSE, TAG, "this is verbose")
        historian!!.log(Log.DEBUG, TAG, "this is debug1")
        historian!!.log(Log.DEBUG, TAG, "this is debug2")

        val result = getAllLogs(historian!!)

        assertEquals(0, result.count.toLong())
    }

    @Test
    @Throws(InterruptedException::class)
    fun log_queue_over_logLevel() {
        historian!!.initialize()

        historian!!.log(Log.INFO, TAG, "this is info1")
        historian!!.log(Log.DEBUG, TAG, "this is debug1")
        historian!!.log(Log.INFO, TAG, "this is info2")
        historian!!.log(Log.WARN, TAG, "this is warn1")
        historian!!.log(Log.ERROR, TAG, "this is error1")

        Thread.sleep(500)

        val cursor = getAllLogs(historian!!)

        assertEquals(4, cursor.count.toLong())

        cursor.moveToFirst()
        assertEquals("INFO", Cursors.getString(cursor, "priority"))
        assertEquals(TAG, Cursors.getString(cursor, "tag"))
        assertEquals("this is info1", Cursors.getString(cursor, "message"))

        cursor.moveToNext()
        assertEquals("INFO", Cursors.getString(cursor, "priority"))
        assertEquals(TAG, Cursors.getString(cursor, "tag"))
        assertEquals("this is info2", Cursors.getString(cursor, "message"))

        cursor.moveToNext()
        assertEquals("WARN", Cursors.getString(cursor, "priority"))
        assertEquals(TAG, Cursors.getString(cursor, "tag"))
        assertEquals("this is warn1", Cursors.getString(cursor, "message"))

        cursor.moveToNext()
        assertEquals("ERROR", Cursors.getString(cursor, "priority"))
        assertEquals(TAG, Cursors.getString(cursor, "tag"))
        assertEquals("this is error1", Cursors.getString(cursor, "message"))

        cursor.close()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.M, Build.VERSION_CODES.N])
    @Throws(ExecutionException::class, InterruptedException::class)
    fun log_background() {
        historian!!.initialize()

        val es = Executors.newSingleThreadExecutor()
        val future = es.submit {
            var i = 0
            val len = 10
            while (i < len) {
                historian!!.log(Log.INFO, TAG, "this log is from background thread - $i")
                i++
            }
        }

        future.get()

        Thread.sleep(200)

        val cursor = getAllLogs(historian!!)

        assertEquals(10, cursor.count.toLong())
    }

    @Test
    @Throws(InterruptedException::class)
    fun multipleWriteInMultipleThreads() {
        val nThreads = 10
        historian!!.initialize()

        for (i in 0 until nThreads) {
            val writer = Runnable {
                try {
                    Thread.sleep((Math.random() * 200.0).toInt().toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                historian!!.log(Log.INFO, TAG, "this is test: " + System.currentTimeMillis())
            }

            val thread = Thread(writer)
            thread.run()
        }

        Thread.sleep(1000)

        val cursor = getAllLogs(historian!!)

        assertEquals(cursor.count.toLong(), 10)
    }

    @Test
    @Throws(InterruptedException::class)
    fun nullTag() {
        historian!!.initialize()

        historian!!.log(Log.INFO, null!!, "this tag should be null")

        Thread.sleep(1000)

        val cursor = getAllLogs(historian!!)

        cursor.moveToFirst()
        assertEquals("", Cursors.getString(cursor, "tag"))
    }

    private fun getAllLogs(historian: Historian): Cursor {
        val db = historian.dbOpenHelper.readableDatabase
        return db.query("log", arrayOf("id", "tag", "priority", "message", "created_at"), null, null, null, null, "created_at ASC")
    }

    companion object {

        internal val TAG = "test_tag"
    }
}
