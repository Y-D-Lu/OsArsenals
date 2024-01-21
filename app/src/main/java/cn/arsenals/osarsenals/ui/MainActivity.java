package cn.arsenals.osarsenals.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Arrays;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.jni.ArsenalsJni;
import cn.arsenals.osarsenals.manager.OverviewViewManager;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.views.FunctionView;
import cn.arsenals.osarsenals.views.OverviewView;
import cn.arsenals.osarsenals.views.SettingsView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private String[] requestPermissionsList = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.QUERY_ALL_PACKAGES
    };

    private RelativeLayout mainRelativelayout;
    private OverviewView overviewView;
    private FunctionView functionView;
    private SettingsView settingsView;
    private Button functionBtn;
    private Button overviewBtn;
    private Button settingsBtn;
    private RelativeLayout currentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainRelativelayout = findViewById(R.id.mainRelativelayout);
        functionBtn = findViewById(R.id.mainFunctionBtn);
        functionBtn.setOnClickListener(this);
        overviewBtn = findViewById(R.id.mainOverviewBtn);
        overviewBtn.setOnClickListener(this);
        settingsBtn = findViewById(R.id.mainSettingsBtn);
        settingsBtn.setOnClickListener(this);

        overviewView = new OverviewView(this);
        functionView = new FunctionView(this);
        settingsView = new SettingsView(this);
        mainRelativelayout.addView(overviewView);
        currentView = overviewView;

        Alog.verbose(TAG, "onCreate : " + ArsenalsJni.stringFromJNI("Hello world"));
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPermissionForOsArsenals();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == functionBtn) {
            Alog.info(TAG, "onClick on functionBtn currentView " + currentView);
            if (currentView != functionView) {
                mainRelativelayout.removeView(currentView);
                mainRelativelayout.addView(functionView);
                currentView = functionView;
            }
            return;
        } else if (view == overviewBtn) {
            Alog.info(TAG, "onClick on overviewBtn currentView " + currentView);
            if (currentView != overviewView) {
                mainRelativelayout.removeView(currentView);
                mainRelativelayout.addView(overviewView);
                currentView = overviewView;
            }
            return;
        } else if (view == settingsBtn) {
            Alog.info(TAG, "onClick on settingsBtn currentView " + currentView);
            if (currentView != settingsView) {
                mainRelativelayout.removeView(currentView);
                mainRelativelayout.addView(settingsView);
                currentView = settingsView;
            }
            return;
        }
        Alog.warn(TAG, "onClick on unknown view " + view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_pin) {
            OverviewViewManager.getInstance().addView();
            return true;
        } else if (item.getItemId() == R.id.main_menu_record) {
            OverviewViewManager.getInstance().removeView();
            return true;
        }
        super.onOptionsItemSelected(item);
        return false;
    }

    private void checkPermissionForOsArsenals() {
        if (!Settings.System.canWrite(MainActivity.this)) {
            Alog.warn(TAG, "do not have permission WRITE_SETTINGS, start settings to request");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Alog.warn(TAG, "do not have permission MANAGE_EXTERNAL_STORAGE, start settings to request");
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            Alog.warn(TAG, "do not have permission SYSTEM_ALERT_WINDOW, start settings to request");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        for (String permission : requestPermissionsList) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Alog.info(TAG, "checkPermissionForOsArsenals " + permission + " granted");
            } else {
                Alog.info(TAG, "checkPermissionForOsArsenals " + permission + " not granted");
                if (shouldShowRequestPermissionRationale(permission)) {
                    Alog.warn(TAG, "checkPermissionForOsArsenals " + permission + " denied!");
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Alog.info(TAG, "checkPermissionForOsArsenals request " + permission);
                    requestPermissions(requestPermissionsList, 0x1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x1) {
            Alog.info(TAG, "onRequestPermissionsResult 0x1 permissions " + Arrays.toString(permissions) + " grantResults " + Arrays.toString(grantResults));
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Alog.warn(TAG, "onRequestPermissionsResult failed!");
                        checkPermissionForOsArsenals();
                        return;
                    }
                }
                Alog.info(TAG, "onRequestPermissionsResult 0x1 all granted");
            }
            return;
        }
        // Ignore all other requests.
    }
}