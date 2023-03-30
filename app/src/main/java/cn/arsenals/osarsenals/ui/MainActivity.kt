package cn.arsenals.osarsenals.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import cn.arsenals.osarsenals.R
import cn.arsenals.osarsenals.jni.ArsenalsJni
import cn.arsenals.osarsenals.manager.OverviewViewManager
import cn.arsenals.osarsenals.utils.Alog
import cn.arsenals.osarsenals.views.FunctionView
import cn.arsenals.osarsenals.views.OverviewView
import cn.arsenals.osarsenals.views.SettingsView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "OverviewView"
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
}