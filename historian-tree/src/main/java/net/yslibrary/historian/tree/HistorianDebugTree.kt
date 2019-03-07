package net.yslibrary.historian.tree

import timber.log.Timber

class HistorianDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format("(%s:%s) #%s ",
                element.fileName,
                element.lineNumber,
                element.methodName)
    }
}