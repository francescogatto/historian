package net.francescogatto.catlog

import timber.log.Timber


class CatLogDebugTree(private val catLog: CatLog) : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format("(%s:%s) #%s ",
                element.fileName,
                element.lineNumber,
                element.methodName)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (tag == null)
            catLog.log(priority, "", message)
        else
            catLog.log(priority, tag, message)
    }

    companion object {

        fun with(catLog: CatLog): CatLogTree {
            return CatLogTree(catLog)
        }
    }
}
