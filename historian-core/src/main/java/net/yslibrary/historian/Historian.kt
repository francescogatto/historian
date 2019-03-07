package net.yslibrary.historian

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.annotation.CheckResult
import android.util.Log
import net.yslibrary.historian.internal.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Historian
 */

class Historian private constructor(internal val context: Context,
                                    internal val directory: File,
                                    internal val dbName: String,
                                    internal val size: Int,
                                    internal val logLevel: Int,
                                    internal val debug: Boolean,
                                    callbacks: Callbacks?) {

    internal val dbOpenHelper: DbOpenHelper
    internal val logWriterDB: LogWriter
    internal val logWriterFile: LogWriter
    internal val callbacks: Callbacks

    private val executorService: ExecutorService
    private var initialized = false

    val database: SQLiteDatabase
        get() {
            checkInitialized()
            return dbOpenHelper.readableDatabase
        }

    init {
        this.callbacks = callbacks ?: DefaultCallbacks(debug)

        createDirIfNeeded(directory)
        try {
            dbOpenHelper = DbOpenHelper(context, directory.canonicalPath + File.separator + dbName)
        } catch (e: IOException) {
            throw HistorianFileException("Could not resolve the canonical path to the Historian DB file: " + directory.absolutePath, e)
        }

        if (debug)
            Log.d(TAG, String.format(Locale.ENGLISH, "backing database file will be created at: %s", dbOpenHelper.databaseName))

        executorService = Executors.newSingleThreadExecutor()
        logWriterDB = LogWriterDB(dbOpenHelper, size.toLong())
        logWriterFile = LogWriterFile(dbOpenHelper.context, size)

    }

    /**
     * initialize
     */
    fun initialize() {
        if (initialized) return

        dbOpenHelper.writableDatabase

        initialized = true
    }

    fun log(priority: Int, tag: String, message: String?) {
        checkInitialized()

        if (priority < logLevel) return
        if (message == null || message.length == 0) return

        executorService.execute(
                LogWritingTask(
                        callbacks,
                        logWriterDB,
                        LogEntity.create(priority, tag, message, System.currentTimeMillis())
                )
        )
    }

    fun log(priority: Int, tag: String, message: String?, t: Throwable) {
        checkInitialized()

        if (priority < logLevel) return
        if (message == null || message.length == 0) return

        executorService.execute(
                LogWritingTask(
                        callbacks,
                        logWriterDB,
                        LogEntity.create(priority, tag, message, System.currentTimeMillis(), t)
                )
        )
    }

    /**
     * Terminate Historian
     * This method will perform;
     * - close underlying [net.yslibrary.historian.internal.DbOpenHelper]
     *
     *
     * After calling this method, all calls to this instance of [net.yslibrary.historian.Historian]
     * can produce exception or undefined behavior.
     */
    fun terminate() {
        checkInitialized()
        dbOpenHelper.close()
    }

    /**
     * delete cache
     */
    fun delete() {
        checkInitialized()
        logWriterDB.delete()
    }

    /**
     * Get absolute path of database file
     *
     * @return absolute path of database file
     */
    fun dbPath(): String {
        checkInitialized()
        try {
            return directory.canonicalPath + File.separator + dbName
        } catch (e: IOException) {
            throw HistorianFileException("Could not resolve the canonical path to the Historian DB file: " + directory.absolutePath, e)
        }

    }

    /**
     * Get database file name
     *
     * @return database file name
     */
    fun dbName(): String {
        return dbName
    }

    private fun createDirIfNeeded(file: File) {
        if (!file.exists()) file.mkdir()
    }

    /**
     * throw if [Historian.initialize] is not called.
     */
    private fun checkInitialized() {
        if (!initialized) throw IllegalStateException("Historian#initialize is not called")
    }


    interface Callbacks {
        fun onSuccess()

        fun onFailure(throwable: Throwable)
    }

    /**
     * Builder class for [net.yslibrary.historian.Historian]
     */
    class Builder internal constructor(context: Context) {

        private val context: Context
        private var directory: File? = null
        private var name = DB_NAME
        private var size = SIZE
        private var logLevel = LOG_LEVEL
        private var debug = false
        private var callbacks: Callbacks? = null

        init {
            this.context = context.applicationContext
            directory = context.filesDir
        }

        /**
         * Specify a directory where Historian's Database file is stored.
         *
         * @param directory directory to save SQLite database file.
         * @return Builder
         */
        @CheckResult
        fun directory(directory: File): Builder {
            this.directory = directory
            return this
        }

        /**
         * Specify a name of the Historian's Database file
         *
         *
         * Default is [Historian.DB_NAME]
         *
         * @param name file name of the backing SQLite database file
         * @return Builder
         */
        @CheckResult
        fun name(name: String): Builder {
            this.name = name
            return this
        }

        /**
         * Specify the max row number of the SQLite database
         *
         *
         * Default is 500.
         *
         * @param size max row number
         * @return Builder
         */
        @CheckResult
        fun size(size: Int): Builder {
            if (size < 0) throw IllegalArgumentException("size should be 0 or greater")
            this.size = size
            return this
        }

        /**
         * Specify minimum log level to save. The value should be any one of
         * [android.util.Log.VERBOSE],
         * [android.util.Log.DEBUG],
         * [android.util.Log.INFO],
         * [android.util.Log.WARN],
         * [android.util.Log.ERROR] or
         * [android.util.Log.ASSERT].
         *
         *
         * Default is [android.util.Log.INFO]
         *
         * @param logLevel log level
         * @return Builder
         */
        @CheckResult
        fun logLevel(logLevel: Int): Builder {
            this.logLevel = logLevel
            return this
        }

        /**
         * Enable/disable Historian's debug logs(not saved to SQLite).
         *
         *
         * Default is false.
         *
         * @param debug true: output logs. false: no debug logs
         * @return Builder
         */
        @CheckResult
        fun debug(debug: Boolean): Builder {
            this.debug = debug
            return this
        }

        /**
         * Specify callbacks. This callbacks are called each time Historian save a log.
         * This callbacks are called on background thread.
         *
         *
         * Default is [DefaultCallbacks]
         *
         * @param callbacks callbacks to execute.
         * @return Builder
         */
        @CheckResult
        fun callbacks(callbacks: Callbacks): Builder {
            this.callbacks = callbacks
            return this
        }

        /**
         * Build Historian. You need to call this method to use [Historian]
         *
         * @return [Historian]
         */
        @CheckResult
        fun build(): Historian {
            return Historian(context, directory!!, name, size, logLevel, debug, callbacks)
        }
    }

    internal class DefaultCallbacks(private val debug: Boolean) : Callbacks {

        override fun onSuccess() {
            // no-op
        }

        override fun onFailure(throwable: Throwable) {
            if (debug) Log.e(TAG, "Something happened while trying to save a log", throwable)
        }
    }

    companion object {

        internal val DB_NAME = "log.db"
        internal val SIZE = 500
        internal val LOG_LEVEL = Log.INFO

        private val TAG = "Historian"

        /**
         * Get Builder
         *
         * @param context Context
         * @return [Builder]
         */
        @CheckResult
        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }
}
