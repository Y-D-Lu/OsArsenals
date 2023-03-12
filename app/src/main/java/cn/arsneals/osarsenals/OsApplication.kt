package cn.arsneals.osarsenals

import android.app.Application
import android.content.res.Configuration
import cn.arsneals.osarsenals.manager.DeviceStatusManager
import cn.arsneals.osarsenals.manager.OverviewViewManager
import cn.arsneals.osarsenals.utils.Alog

class OsApplication: Application() {
    val TAG = "OsApplication"
    companion object {
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
