package cn.arsenals.osarsenals.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Gravity;
import android.view.WindowManager;

import cn.arsenals.osarsenals.OsApplication;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.views.MonitorView;

public class OverviewViewManager {
    private static final String TAG = "OverviewViewManager";

    private static class SingletonHolder {
        private static final OverviewViewManager sInstance = new OverviewViewManager();
    }

    public static OverviewViewManager getInstance() {
        return OverviewViewManager.SingletonHolder.sInstance;
    }

    private HandlerThread handlerThread = new HandlerThread("OverviewViewManagerHandlerThread");
    private Handler handler = null;

    private WindowManager windowManager = (WindowManager) OsApplication.application.getSystemService(Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    private MonitorView view = new MonitorView(OsApplication.application);
    private Point touchStartPoint = new Point();

    public void init() {
        Alog.info(TAG, "OverviewViewManager init");

        layoutParams.type = 2018;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        // mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.format = PixelFormat.RGBA_8888;
    }

    public void uninit() {
        Alog.info(TAG, "OverviewViewManager uninit");
    }

    public void addView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                layoutParams.x = 0;
                layoutParams.y = 0;
                windowManager.addView(view, layoutParams);
            }
        });
    }

    public void removeView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                windowManager.removeView(view);
            }
        });
    }
}
