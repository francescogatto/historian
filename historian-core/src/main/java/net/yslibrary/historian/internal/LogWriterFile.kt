package net.yslibrary.historian.internal

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Class for log writing operation
 */

class LogWriterFile(val context: Context, private val size: Int) : LogWriter {

    override fun log(log: LogEntity) {
        if (log.tag == null) {
            appendLog(formatMsg("", log.message + log.stackException))
        } else {
            appendLog(formatMsg(log.tag!!, log.message + log.stackException))
        }

    }

    fun appendLog(text: String) {
        val logFile = File(context.cacheDir.toString() + "/log.txt")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Clear logs in SQLite.
     */
    override fun delete() {

    }

    companion object {
        private val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
        private val MSG_FORMAT = "%s: %s - %s"  // timestamp, tag, message
        private val sBufferedWriter: BufferedWriter? = null

        private fun formatMsg(tag: String, msg: String): String {
            return String.format(MSG_FORMAT, currentTimeStamp, tag, msg)
        }

        private val currentTimeStamp: String?
            get() {
                var currentTimeStamp: String? = null

                try {
                    val dateFormat = SimpleDateFormat(TIMESTAMP_FORMAT,
                            java.util.Locale.getDefault())
                    currentTimeStamp = dateFormat.format(Date())
                } catch (e: Exception) {
                    Log.e("FileLog", Log.getStackTraceString(e))
                }

                return currentTimeStamp
            }
    }

}
