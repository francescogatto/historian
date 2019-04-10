package net.francescogatto.catlog

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.CheckResult
import net.francescogatto.catlog.internal.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CatLog
 */

class CatLog private constructor(internal val context: Context,
                                 private val directory: File,
                                 private val name: String,
                                 private val size: Int,
                                 private val logLevel: Int,
                                 private val debug: Boolean,
                                 private val typeOfPersistence: Type,
                                 callbacks: Callbacks?) {

    private val dbOpenHelper: DbOpenHelper
    private lateinit var logWriterDB: LogWriter
    private lateinit var logWriterFile: LogWriter
    private val callbacks: Callbacks


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
            dbOpenHelper = DbOpenHelper(context, directory.canonicalPath + File.separator + name + ".db")
        } catch (e: IOException) {
            throw CatLogFileException("Could not resolve the canonical path to the CatLog file: " + directory.absolutePath, e)
        }

        if (debug) Timber.tag(TAG).d(String.format(Locale.ENGLISH, "backing database file will be created at: %s", dbOpenHelper.databaseName))

        executorService = Executors.newSingleThreadExecutor()
        if (isDBType) logWriterDB = LogWriterDB(dbOpenHelper, size.toLong())
        if (isFileType) logWriterFile = LogWriterFile(dbOpenHelper.context, size, File(directory.canonicalPath + File.separator + name + ".txt"))

    }

    private val isFileType get() = typeOfPersistence == Type.FILE
    @Deprecated("TO deprecate")
    private val isDBType get() = typeOfPersistence == Type.DATABASE

    /**
     * initialize
     */
    fun initialize() {
        if (initialized) return
        if (isDBType) dbOpenHelper.writableDatabase
        initialized = true
    }

    /**
     * initialize
     */
    fun initializeAndSendLogAfterException(application: Application) {
        if (initialized) return

        if (PreferenceManager.getDefaultSharedPreferences(application).getBoolean("error", false)) {
            PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean("error", false).apply()
        }
        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), Thread.getDefaultUncaughtExceptionHandler(), application))

        if (isDBType) dbOpenHelper.writableDatabase
        initialized = true
    }

    fun log(priority: Int, tag: String, message: String?) {
        checkInitialized()
        if (priority < logLevel) return
        if (message.isNullOrEmpty()) return
        when {
            isFileType -> executorService.execute(LogWritingTask(callbacks, logWriterFile, LogEntity.create(priority, tag, message, System.currentTimeMillis())))
            else -> executorService.execute(LogWritingTask(callbacks, logWriterDB, LogEntity.create(priority, tag, message, System.currentTimeMillis())))
        }
    }

    fun log(priority: Int, tag: String, message: String?, t: Throwable) {
        checkInitialized()
        if (priority < logLevel) return
        if (message.isNullOrEmpty()) return
        when {
            isFileType -> executorService.execute(LogWritingTask(callbacks, logWriterFile, LogEntity.create(priority, tag, message, System.currentTimeMillis(), t)))
            else -> executorService.execute(LogWritingTask(callbacks, logWriterDB, LogEntity.create(priority, tag, message, System.currentTimeMillis(), t)))
        }
    }

    /**
     * Terminate CatLog
     * This method will perform;
     * - close underlying [net.francescogatto.catlog.internal.DbOpenHelper]
     *
     *
     * After calling this method, all calls to this instance of [net.francescogatto.catlog.CatLog]
     * can produce exception or undefined behavior.
     */
    fun terminate() {
        checkInitialized()
        if (isDBType) dbOpenHelper.close()
    }

    /**
     * delete cache
     */
    fun delete() {
        checkInitialized()
        if (isDBType) logWriterDB.delete()
        if (isFileType) logWriterFile.delete()
    }

    /**
     * Get absolute path of database file
     *
     * @return absolute path of database file
     */
    fun path(): String {
        checkInitialized()
        try {
            return directory.canonicalPath + File.separator + name
        } catch (e: IOException) {
            throw CatLogFileException("Could not resolve the canonical path to the CatLog DB file: " + directory.absolutePath, e)
        }

    }

    /**
     * Get database file name
     *
     * @return database file name
     */
    fun name(): String {
        return name
    }

    private fun createDirIfNeeded(file: File) {
        if (!file.exists()) file.mkdir()
    }

    /**
     * throw if [CatLog.initialize] is not called.
     */
    private fun checkInitialized() {
        if (!initialized) throw IllegalStateException("CatLog#initialize is not called")
    }


    interface Callbacks {
        fun onSuccess()

        fun onFailure(throwable: Throwable)
    }

    /**
     * Builder class for [net.francescogatto.catlog.CatLog]
     */
    class Builder internal constructor(context: Context) {

        private val context: Context = context.applicationContext
        private var directory = context.filesDir
        private var typeOfPersistence: Type = Type.FILE
        private var name = NAME
        private var size = SIZE
        private var logLevel = LOG_LEVEL
        private var debug = false
        private var callbacks: Callbacks? = null

        /**
         * Specify how to save the log
         *
         * @param type the CatLog type
         * @return Builder
         */
        @CheckResult
        fun typeOfPersistence(type: Type): Builder {
            this.typeOfPersistence = type
            return this
        }

        /**
         * Specify a directory where CatLog's Database file is stored.
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
         * Specify a name of the CatLog's Database file
         *
         *
         * Default is [CatLog.DB_NAME]
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
         * Specify the max lines of log
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
         * Enable/disable CatLog's debug logs(not saved to SQLite).
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
         * Specify callbacks. This callbacks are called each time CatLog save a log.
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
         * Build CatLog. You need to call this method to use [CatLog]
         *
         * @return [CatLog]
         */
        @CheckResult
        fun build(): CatLog {
            return CatLog(context, directory, name, size, logLevel, debug, typeOfPersistence, callbacks)
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

        internal val NAME = "log"
        internal val SIZE = 500
        internal val LOG_LEVEL = Log.INFO

        private val TAG = "CatLog"

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

    enum class Type {
        FILE, DATABASE
    }
}
