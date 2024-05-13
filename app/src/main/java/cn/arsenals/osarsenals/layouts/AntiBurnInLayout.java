package cn.arsenals.osarsenals.layouts;

import android.content.Context;
import android.graphics.Rect;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import cn.arsenals.osarsenals.utils.Alog;

public class AntiBurnInLayout extends RelativeLayout {
    private static final String TAG = "AntiBurnInLayout";

    private static final int ANTI_BURN_IN_INTERVAL = 10;

    private Timer timer = null;

    LayoutParams layoutParams = null;

    public AntiBurnInLayout(Context context) {
        super(context);
        initView();
    }

    public AntiBurnInLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AntiBurnInLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public AntiBurnInLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimerScheduled();
            }
        }, 0, 5 * 60 * 1000);
    }

    private void onTimerScheduled() {
        if (!isAttachedToWindow()) {
            Alog.warn(TAG, "onTimerScheduled not isAttachedToWindow, return!");
            return;
        }
        if (layoutParams == null) {
            Alog.info(TAG, "onTimerScheduled layoutParams is null, try generate");
            ViewGroup.LayoutParams params = getLayoutParams();
            if (params instanceof LayoutParams) {
                layoutParams = (LayoutParams) getLayoutParams();
            }
        }
        if (layoutParams == null) {
            Alog.warn(TAG, "onTimerScheduled layoutParams is null, return!");
            return;
        }
        final int minX = -layoutParams.leftMargin;
        final int maxX = layoutParams.rightMargin;
        final int minY = -layoutParams.topMargin;
        final int maxY = layoutParams.bottomMargin;
        int targetX = (int) getTranslationX() + ANTI_BURN_IN_INTERVAL;
        int targetY = (int) getTranslationY();
        if (targetX > maxX) {
            targetX = minX;

            targetY = (int) getTranslationY() + ANTI_BURN_IN_INTERVAL;
            if (targetY > maxY) {
                targetY = minY;
            }
        }
        final int finalX = targetX;
        final int finalY = targetY;
        post(new Runnable() {
            @Override
            public void run() {
                setTranslationX(finalX);
                setTranslationY(finalY);
            }
        });
    }
}
