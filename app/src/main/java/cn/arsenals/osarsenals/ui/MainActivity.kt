package cn.arsenals.osarsenals.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import cn.arsenals.osarsenals.R
import cn.arsenals.osarsenals.jni.ArsenalsJni
import cn.arsenals.osarsenals.manager.OverviewViewManager
import cn.arsenals.osarsenals.utils.Alog
import cn.arsenals.osarsenals.views.FunctionView
import cn.arsenals.osarsenals.views.OverviewView
import cn.arsenals.osarsenals.views.SettingsView
import java.util.Arrays


class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "OverviewView"
    }

    private val requestPermissionsList = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.QUERY_ALL_PACKAGES,
        )
    } else if (VERSION.SDK_INT >= VERSION_CODES.R) {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.QUERY_ALL_PACKAGES,
        )
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    private lateinit var mainRelativelayout: RelativeLayout
    private lateinit var overviewView: OverviewView
    private lateinit var functionView: FunctionView
    private lateinit var settingsView: SettingsView
    private lateinit var functionBtn: Button
    private lateinit var overviewBtn: Button
    private lateinit var settingsBtn: Button
    private lateinit var currentView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainRelativelayout = findViewById(R.id.mainRelativelayout)
        functionBtn = findViewById(R.id.mainFunctionBtn)
        functionBtn.setOnClickListener(this)
        overviewBtn = findViewById(R.id.mainOverviewBtn)
        overviewBtn.setOnClickListener(this)
        settingsBtn = findViewById(R.id.mainSettingsBtn)
        settingsBtn.setOnClickListener(this)

        overviewView = OverviewView(this)
        functionView = FunctionView(this)
        settingsView = SettingsView(this)
        mainRelativelayout.addView(overviewView)
        currentView = overviewView

        Alog.verbose(TAG, "onCreate : " + ArsenalsJni().stringFromJNI("Hello world"))
    }

    override fun onResume() {
        super.onResume()

        checkPermissionForOsArsenals()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when(view) {
            functionBtn->{
                Alog.info(TAG, "onClick on functionBtn currentView $currentView")
                if (currentView != functionView) {
                    mainRelativelayout.removeView(currentView)
                    mainRelativelayout.addView(functionView)
                    currentView = functionView
                }
            }
            overviewBtn->{
                Alog.info(TAG, "onClick on overviewBtn currentView $currentView")
                if (currentView != overviewView) {
                    mainRelativelayout.removeView(currentView)
                    mainRelativelayout.addView(overviewView)
                    currentView = overviewView
                }
            }
            settingsBtn->{
                Alog.info(TAG, "onClick on settingsBtn currentView $currentView")
                if (currentView != settingsView) {
                    mainRelativelayout.removeView(currentView)
                    mainRelativelayout.addView(settingsView)
                    currentView = settingsView
                }
            }
            else->{
                Alog.warn(TAG, "onClick on unknown view $view")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_pin -> {
                OverviewViewManager.getInstance().addView()
                true
            }
            R.id.main_menu_record -> {
                OverviewViewManager.getInstance().removeView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkPermissionForOsArsenals() {
        if (!Settings.System.canWrite(this@MainActivity)) {
            Alog.warn(TAG, "do not have permission WRITE_SETTINGS, start settings to request")
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        if (VERSION.SDK_INT > VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Alog.warn(TAG, "do not have permission MANAGE_EXTERNAL_STORAGE, start settings to request")
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:$packageName"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return
            }
        }
        if (!Settings.canDrawOverlays(this@MainActivity)) {
            Alog.warn(TAG, "do not have permission SYSTEM_ALERT_WINDOW, start settings to request")
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        for (permission in requestPermissionsList) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Alog.info(TAG, "checkPermissionForOsArsenals $permission granted")
            } else {
                Alog.info(TAG, "checkPermissionForOsArsenals $permission not granted")
                if (shouldShowRequestPermissionRationale(permission)) {
                    Alog.warn(TAG, "checkPermissionForOsArsenals $permission denied!")
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } else {
                    Alog.info(TAG, "checkPermissionForOsArsenals request $permission")
                    requestPermissions(requestPermissionsList, 0x1)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0x1 -> {
                Alog.info(TAG, "onRequestPermissionsResult 0x1 permissions ${Arrays.toString(permissions)} grantResults ${Arrays.toString(grantResults)}")
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    for (result in grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Alog.warn(TAG, "onRequestPermissionsResult failed!")
                            checkPermissionForOsArsenals()
                            return
                        }
                    }
                    Alog.info(TAG, "onRequestPermissionsResult 0x1 all granted")
                }
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}