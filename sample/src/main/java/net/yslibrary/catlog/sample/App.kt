package net.francescogatto.catlog.sample

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
import net.francescogatto.catlog.AppExceptionHandler
import net.francescogatto.catlog.CatLog
import net.francescogatto.catlog.CatLogTree
import timber.log.Timber

class App : Application() {

    lateinit var catLog: CatLog

    override fun onCreate() {
        super.onCreate()

        val catLog = CatLogTree.with(CatLog.builder(this)
                .logLevel(Log.DEBUG)
                .typeOfPersistence(CatLog.Type.FILE)
                .debug(false).build().apply {
                    initializeAndSendLogAfterException(this@App)
                    delete() //reset all'avvio dei vecchi log ??
                })

        Timber.plant(DebugTree())
        Timber.plant(catLog)

       /* // 1. Get the system handler.
        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()

        // 2. Set the default handler as a dummy (so that crashlytics fallbacks to this one, once set)
        Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do nothing */ }

        // 3. Setup crashlytics so that it becomes the default handler (and fallbacking to our dummy handler)
        //Fabric.with(this, Crashlytics())

        val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        // 4. Setup our handler, which tries to restart the app.
        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this))

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                // .enableWebKitInspector(HistorianInspectorModulesProvider(this, catLog))
                .build())
                */
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
