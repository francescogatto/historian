package net.francescogatto.catlog.sample

import java.io.Closeable

/**
 * Created by yshrsmz on 17/02/14.
 */

object Util {

    fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (t: Throwable) {
            // no-op
        }

    }
}// no-op
