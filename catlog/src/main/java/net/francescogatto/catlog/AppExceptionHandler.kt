package net.francescogatto.catlog

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

class AppExceptionHandler(val systemHandler: Thread.UncaughtExceptionHandler,
                          val crashlyticsHandler: Thread.UncaughtExceptionHandler,
                          application: Application) : Thread.UncaughtExceptionHandler {

    private val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    override fun uncaughtException(t: Thread?, e: Throwable) {
        pref.edit().putBoolean("error", true).commit()
        Log.e("AppExceptionHandler", "", e)
        systemHandler.uncaughtException(t, e)
    }

}