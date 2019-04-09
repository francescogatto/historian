package net.yslibrary.catlog

import android.database.Cursor
import android.os.Build
import android.util.Log
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * Created by yshrsmz on 2017/01/22.
 */
@RunWith(ConfiguredRobolectricTestRunner::class)
class CatLogTest {

    private var catLog: CatLog? = null

    @Before
    fun setup() {
        val context = RuntimeEnvironment.application

        catLog = CatLog.builder(context).build()
    }

    @Test(expected = IllegalStateException::class)
    fun initialize_not_called() {
        catLog!!.log(Log.DEBUG, TAG, "this is debug1")
    }

    @Test
    fun log_queue_under_logLevel() {
        catLog!!.initialize()

        catLog!!.log(Log.VERBOSE, TAG, "this is verbose")
        catLog!!.log(Log.DEBUG, TAG, "this is debug1")
        catLog!!.log(Log.DEBUG, TAG, "this is debug2")

        val result = getAllLogs(catLog!!)

        assertEquals(0, result.count.toLong())
    }

    @Test
    @Throws(InterruptedException::class)
    fun log_queue_over_logLevel() {
        catLog!!.initialize()

        catLog!!.log(Log.INFO, TAG, "this is info1")
        catLog!!.log(Log.DEBUG, TAG, "this is debug1")
        catLog!!.log(Log.INFO, TAG, "this is info2")
        catLog!!.log(Log.WARN, TAG, "this is warn1")
        catLog!!.log(Log.ERROR, TAG, "this is error1")

        Thread.sleep(500)

        val cursor = getAllLogs(catLog!!)

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
        catLog!!.initialize()

        val es = Executors.newSingleThreadExecutor()
        val future = es.submit {
            var i = 0
            val len = 10
            while (i < len) {
                catLog!!.log(Log.INFO, TAG, "this log is from background thread - $i")
                i++
            }
        }

        future.get()

        Thread.sleep(200)

        val cursor = getAllLogs(catLog!!)

        assertEquals(10, cursor.count.toLong())
    }

    @Test
    @Throws(InterruptedException::class)
    fun multipleWriteInMultipleThreads() {
        val nThreads = 10
        catLog!!.initialize()

        for (i in 0 until nThreads) {
            val writer = Runnable {
                try {
                    Thread.sleep((Math.random() * 200.0).toInt().toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                catLog!!.log(Log.INFO, TAG, "this is test: " + System.currentTimeMillis())
            }

            val thread = Thread(writer)
            thread.run()
        }

        Thread.sleep(1000)

        val cursor = getAllLogs(catLog!!)

        assertEquals(cursor.count.toLong(), 10)
    }

    @Test
    @Throws(InterruptedException::class)
    fun nullTag() {
        catLog!!.initialize()

        catLog!!.log(Log.INFO, null!!, "this tag should be null")

        Thread.sleep(1000)

        val cursor = getAllLogs(catLog!!)

        cursor.moveToFirst()
        assertEquals("", Cursors.getString(cursor, "tag"))
    }

    private fun getAllLogs(catLog: CatLog): Cursor {
        val db = catLog.dbOpenHelper.readableDatabase
        return db.query("log", arrayOf("id", "tag", "priority", "message", "created_at"), null, null, null, null, "created_at ASC")
    }

    companion object {

        internal val TAG = "test_tag"
    }
}
