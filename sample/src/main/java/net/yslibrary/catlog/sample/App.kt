package net.francescogatto.catlog.sample

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import net.francescogatto.catlog.CatLog
import net.francescogatto.catlog.CatLogTree
import timber.log.Timber

class App : Application() {

    lateinit var catLog: CatLog

    override fun onCreate() {
        super.onCreate()

        Timber.plant(DebugTree())
        Timber.plant(CatLogTree.with(CatLog.builder(this).build().apply { initialize() }))

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                // .enableWebKitInspector(HistorianInspectorModulesProvider(this, catLog))
                .build())
    }

    override fun onTerminate() {
        super.onTerminate()
        catLog.terminate()
    }

    companion object {

        operator fun get(context: Context): App {
            return context.applicationContext as App
        }

        fun getHistorian(context: Context): CatLog {
            return get(context).catLog
        }
    }
}
