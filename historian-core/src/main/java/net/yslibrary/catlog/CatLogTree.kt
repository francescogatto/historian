package net.yslibrary.catlog

import timber.log.Timber


class CatLogTree (private val catLog: CatLog) : Timber.Tree() {

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