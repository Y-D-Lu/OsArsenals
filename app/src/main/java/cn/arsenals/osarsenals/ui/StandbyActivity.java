package cn.arsenals.osarsenals.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class StandbyActivity extends AppCompatActivity {
    private static final String TAG = "StandbyActivity";

    private TextView standbyTextview;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standby);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        standbyTextview = findViewById(R.id.standby_textview);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimerScheduled();
            }
        }, 0, 1 * 1 * 1000);
    }

    private void onTimerScheduled() {
        LocalTime localTime = LocalTime.now();
        standbyTextview.post(new Runnable() {
            @Override
            public void run() {
                Alog.info(TAG, "onTimerScheduled " + localTime.toString());
                if (localTime.toString().substring(0, 2).equals("08")) {
                    standbyTextview.setVisibility(View.VISIBLE);
                } else {
                    standbyTextview.setVisibility(View.INVISIBLE);
                }
                standbyTextview.setText(localTime.toString().substring(0, 5));
            }
        });
    }
}