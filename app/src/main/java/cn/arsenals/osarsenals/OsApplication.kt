package cn.arsenals.osarsenals

import android.app.Application
import android.content.res.Configuration
import cn.arsenals.osarsenals.manager.DeviceStatusManager
import cn.arsenals.osarsenals.manager.OverviewViewManager
import cn.arsenals.osarsenals.utils.Alog

class OsApplication: Application() {
    companion object {
        private const val TAG = "OsApplication"

        lateinit var application: Application
    }

    init {
        application = this
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()

        Alog.info(TAG, "Application onCreate")
        OverviewViewManager.getInstance().init()
        DeviceStatusManager.getInstance().init()
    }
}
