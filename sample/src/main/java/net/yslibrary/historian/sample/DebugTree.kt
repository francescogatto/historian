package net.yslibrary.historian.sample

import timber.log.Timber

internal class DebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format("(%s:%s)#%s",
                element.fileName,
                element.lineNumber,
                element.methodName)
    }
}