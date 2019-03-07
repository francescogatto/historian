package net.yslibrary.historian.tree

import net.yslibrary.historian.Historian

import timber.log.Timber


class HistorianTree private constructor(private val historian: Historian) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        var tag = tag
        if (tag == null)
            tag = ""
        historian.log(priority, tag, message)
    }

    companion object {

        fun with(historian: Historian): HistorianTree {
            return HistorianTree(historian)
        }

        private val STACK_TRACE_LEVELS_UP = 5


        /**
         * Get the current line number. Note, this will only work as called from
         * this class as it has to go a predetermined number of steps up the stack
         * trace. In this case 5.
         *
         * @return int - Current line number.
         * @author kvarela
         */
        private val lineNumber: Int
            get() = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].lineNumber

        /**
         * Get the current class name. Note, this will only work as called from this
         * class as it has to go a predetermined number of steps up the stack trace.
         * In this case 5.
         *
         * @return String - Current line number.
         * @author kvarela
         */
        private// kvarela: Removing ".java" and returning class name
        val className: String
            get() {
                val fileName = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].fileName
                return fileName.substring(0, fileName.length - 5)
            }

        /**
         * Get the current method name. Note, this will only work as called from
         * this class as it has to go a predetermined number of steps up the stack
         * trace. In this case 5.
         *
         * @return String - Current line number.
         * @author kvarela
         */
        private val methodName: String
            get() = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].methodName

        /**
         * Returns the class name, method name, and line number from the currently
         * executing log call in the form .()-
         *
         * @return String - String representing class name, method name, and line
         * number.
         * @author kvarela
         */
        private val classNameMethodNameAndLineNumber: String
            get() = "[$className.$methodName()-$lineNumber]: "
    }
}