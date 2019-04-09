package net.yslibrary.historian.tree

import net.yslibrary.historian.Historian
import net.yslibrary.historian.HistorianTree
import timber.log.Timber


class HistorianTree private constructor(private val historian: Historian) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        var tag = tag
        if (tag == null) tag = ""
        historian.log(priority, tag, message)
    }

    companion object {

        fun with(historian: Historian): HistorianTree {
            return net.yslibrary.historian.HistorianTree(historian)
        }
    }
}