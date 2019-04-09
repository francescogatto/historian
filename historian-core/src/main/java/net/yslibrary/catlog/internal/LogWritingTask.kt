package net.yslibrary.catlog.internal

import net.yslibrary.catlog.CatLog

/**
 * Runnable implementation writing logs and executing callbacks
 */

class LogWritingTask(private val callbacks: CatLog.Callbacks,
                     private val logWriter: LogWriter,
                     private val log: LogEntity) : Runnable {

    override fun run() {
        try {
            logWriter.log(log)
            callbacks.onSuccess()
        } catch (throwable: Throwable) {
            callbacks.onFailure(throwable)
        }

    }
}
