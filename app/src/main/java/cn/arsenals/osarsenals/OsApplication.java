package cn.arsenals.osarsenals;

import android.app.Application;
import android.content.res.Configuration;

import cn.arsenals.osarsenals.manager.DeviceStatusManager;
import cn.arsenals.osarsenals.manager.OverviewViewManager;
import cn.arsenals.osarsenals.utils.Alog;

public class OsApplication extends Application {
    private static final String TAG = "OsApplication";

    public static Application application;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        Alog.info(TAG, "Application onCreate");
        OverviewViewManager.getInstance().init();
        DeviceStatusManager.getInstance().init();
    }
}
