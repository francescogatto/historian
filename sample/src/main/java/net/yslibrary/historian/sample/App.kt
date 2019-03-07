package net.yslibrary.historian.sample

import android.app.Application
import android.content.Context

import com.facebook.stetho.Stetho

import net.yslibrary.historian.Historian
import net.yslibrary.historian.HistorianInspectorModulesProvider
import net.yslibrary.historian.tree.HistorianDebugTree
import net.yslibrary.historian.tree.HistorianTree

import timber.log.Timber

/**
 * Created by yshrsmz on 17/01/20.
 */

class App : Application() {

    lateinit var historian: Historian

    override fun onCreate() {
        super.onCreate()

        historian = Historian.builder(this).build()
        historian.initialize()

        Timber.plant(HistorianDebugTree())
        Timber.plant(HistorianTree.with(historian))

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(HistorianInspectorModulesProvider(this, historian))
                .build())
    }

    override fun onTerminate() {
        super.onTerminate()
        historian.terminate()
    }

    companion object {

        operator fun get(context: Context): App {
            return context.applicationContext as App
        }

        fun getHistorian(context: Context): Historian {
            return get(context).historian
        }
    }
}
