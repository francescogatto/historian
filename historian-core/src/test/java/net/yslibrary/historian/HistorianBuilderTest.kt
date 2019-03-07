package net.yslibrary.historian

import android.content.Context
import android.util.Log

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue

/**
 * Created by yshrsmz on 2017/01/22.
 */
@RunWith(ConfiguredRobolectricTestRunner::class)
class HistorianBuilderTest {

    private var context: Context? = null

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun build_with_defaults() {
        val historian = Historian.builder(context!!).build()

        assertNotNull(historian.context)
        assertNotNull(historian.dbOpenHelper)
        assertNotNull(historian.logWriterDB)

        //    String path = context.getFilesDir() + File.separator + Historian.DB_NAME;
        //    assertEquals(historian.dbOpenHelper.getDatabaseName(), path);

        assertEquals(Historian.LOG_LEVEL, historian.logLevel)
        assertEquals(context!!.filesDir, historian.directory)
        assertEquals(Historian.DB_NAME, historian.dbName)
        assertEquals(Historian.SIZE, historian.size)
        assertFalse(historian.debug)
        assertThat(historian.callbacks, instanceOf<Any>(Historian.DefaultCallbacks::class.java))
    }

    @Test
    fun build_with_custom_params() {
        val historian = Historian.builder(context!!)
                .name("test.db")
                .directory(context!!.getExternalFilesDir(null)!!)
                .logLevel(Log.DEBUG)
                .size(1000)
                .debug(true)
                .callbacks(TestCallbacks())
                .build()

        assertNotNull(historian.context)
        assertNotNull(historian.dbOpenHelper)
        assertNotNull(historian.logWriterDB)

        //    String path = context.getExternalFilesDir(null) + File.separator + "test.db";
        //    assertEquals(path, historian.dbOpenHelper.getDatabaseName());

        assertEquals(Log.DEBUG, historian.logLevel)
        assertEquals(context!!.getExternalFilesDir(null), historian.directory)
        assertEquals("test.db", historian.dbName)
        assertEquals(1000, historian.size)
        assertTrue(historian.debug)
        assertThat(historian.callbacks, instanceOf<Any>(TestCallbacks::class.java))
    }

    internal inner class TestCallbacks : Historian.Callbacks {

        override fun onSuccess() {

        }

        override fun onFailure(throwable: Throwable) {

        }
    }
}
