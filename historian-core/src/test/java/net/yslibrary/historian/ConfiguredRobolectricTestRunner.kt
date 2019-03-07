package net.yslibrary.historian

import android.app.Application

import org.junit.runners.model.InitializationError
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import java.lang.reflect.Method

/**
 * Created by yshrsmz on 2017/01/22.
 */

class ConfiguredRobolectricTestRunner @Throws(InitializationError::class)
constructor(klass: Class<*>) : RobolectricTestRunner(klass) {

    override fun getConfig(method: Method): Config {
        val c = super.getConfig(method)

        val sdkLevel = if (c.sdk().size == 0) SDK else c.sdk()
        val constants = if (c.constants() == Void::class.java) BuildConfig::class.java else c.constants()
        val application = if (c.constants() == Application::class.java) TestApp::class.java else c.application()

        return Config.Builder(c)
                .setSdk(*sdkLevel)
                .setConstants(constants)
                .setApplication(application)
                .build()
    }

    companion object {
        private val SDK = intArrayOf(23)
    }
}
