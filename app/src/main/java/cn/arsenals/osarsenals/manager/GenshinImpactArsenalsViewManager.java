package cn.arsenals.osarsenals.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import cn.arsenals.osarsenals.OsApplication;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.views.GenshinImpactArsenalsView;

public class GenshinImpactArsenalsViewManager {
    private static final String TAG = "GenshinImpactArsenalsViewManager";

    private static class SingletonHolder {
        private static final GenshinImpactArsenalsViewManager sInstance = new GenshinImpactArsenalsViewManager();
    }

    public static GenshinImpactArsenalsViewManager getInstance() {
        return GenshinImpactArsenalsViewManager.SingletonHolder.sInstance;
    }

    private HandlerThread handlerThread = new HandlerThread("GenshinImpactArsenalsViewManagerHandlerThread");
    private Handler handler;

    private WindowManager windowManager = (WindowManager) OsApplication.application.getSystemService(Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    private GenshinImpactArsenalsView view = new GenshinImpactArsenalsView(OsApplication.application);
    private Point touchStartPoint = new Point();
    private Point touchStartRawPoint = new Point();
    private GenshinInpactArsenalsViewCallback viewCallback;

    public void init() {
        Alog.info(TAG, "GenshinImpactArsenalsViewManager init");

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Alog.info(TAG, "GenshinImpactArsenalsViewManager onTouch " + event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        touchStartPoint.x = (int) event.getX();
                        touchStartPoint.y = (int) event.getY();
                        touchStartRawPoint.x = (int) event.getRawX();
                        touchStartRawPoint.y = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        layoutParams.x = (int) (event.getRawX() - touchStartPoint.x);
                        layoutParams.y = (int) (event.getRawY() - touchStartPoint.y);
                        windowManager.updateViewLayout(view, layoutParams);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (viewCallback != null && (int) event.getRawX() == touchStartRawPoint.x && (int) event.getRawY() == touchStartRawPoint.y) {
                            viewCallback.onClick();
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                return true;
            }
        });

        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
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
        Alog.info(TAG, "GenshinImpactArsenalsViewManager uninit");
    }

    public void addView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                layoutParams.x = 2240;
                layoutParams.y = 0;
                windowManager.addView(view, layoutParams);
            }
        });
    }

    public void removeView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (view.isAttachedToWindow()) {
                    windowManager.removeView(view);
                }
            }
        });
    }

    public interface GenshinInpactArsenalsViewCallback {
        void onClick();
    }

    public void registerViewCallback(GenshinInpactArsenalsViewCallback callback) {
        viewCallback = callback;
    }

    public void updateTextView(String status, String extra) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.updateTextView(status, extra);
            }
        });
    }
}
