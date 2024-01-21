package cn.arsenals.osarsenals.ui;

import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class OnHookActivity extends AppCompatActivity {
    private static final String TAG = "OnHookActivity";

    boolean isFullScreen = false;
    int screenBrightness = 0;
    int screenBrightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_hook);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Alog.info(TAG, "OnHookActivity OnTouch ACTION_DOWN");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setFullScreenStatus();
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            screenBrightnessMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            Alog.info(TAG, "onResume save screenBrightness " + screenBrightness + "screenBrightnessMode " + screenBrightnessMode);

            Settings.System.putInt(
                    getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 1);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightness);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, screenBrightnessMode);
        Alog.info(TAG, "onResume restore screenBrightness " + screenBrightness + "screenBrightnessMode " + screenBrightnessMode);
    }

    private void setFullScreenStatus() {
        if (isFullScreen) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
        isFullScreen = !isFullScreen;
    }
}
