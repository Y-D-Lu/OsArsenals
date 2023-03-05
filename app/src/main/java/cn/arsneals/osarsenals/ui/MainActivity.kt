package cn.arsneals.osarsenals.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import cn.arsneals.osarsenals.R
import cn.arsneals.osarsenals.jni.ArsenalsJni
import cn.arsneals.osarsenals.manager.DeviceStatusManager
import cn.arsneals.osarsenals.utils.Alog
import cn.arsneals.osarsenals.utils.DeviceStatusUtil
import cn.arsneals.osarsenals.views.FunctionView
import cn.arsneals.osarsenals.views.OverviewView
import cn.arsneals.osarsenals.views.SettingsView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "OverviewView"
    private lateinit var mMainRelativelayout: RelativeLayout
    private lateinit var mOverviewView: OverviewView
    private lateinit var mFunctionView: FunctionView
    private lateinit var mSettingsView: SettingsView
    private lateinit var mFunctionBtn: Button
    private lateinit var mOverviewBtn: Button
    private lateinit var mSettingsBtn: Button
    private lateinit var mCurrentView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMainRelativelayout = findViewById(R.id.mainRelativelayout)
        mFunctionBtn = findViewById(R.id.mainFunctionBtn)
        mFunctionBtn.setOnClickListener(this)
        mOverviewBtn = findViewById(R.id.mainOverviewBtn)
        mOverviewBtn.setOnClickListener(this)
        mSettingsBtn = findViewById(R.id.mainSettingsBtn)
        mSettingsBtn.setOnClickListener(this)

        mOverviewView = OverviewView(this)
        mFunctionView = FunctionView(this)
        mSettingsView = SettingsView(this)
        mMainRelativelayout.addView(mOverviewView)
        mCurrentView = mOverviewView

        Alog.verbose(TAG, "onCreate : " + ArsenalsJni().stringFromJNI("Hello world"))
    }

    override fun onResume() {
        super.onResume()

        DeviceStatusManager.getInstance().init()
    }

    override fun onPause() {
        super.onPause()

        DeviceStatusManager.getInstance().uninit()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when(view) {
            mFunctionBtn->{
                Alog.info(TAG, "onClick on functionBtn currentView $mCurrentView")
                if (mCurrentView != mFunctionView) {
                    mMainRelativelayout.removeView(mCurrentView)
                    mMainRelativelayout.addView(mFunctionView)
                    mCurrentView = mFunctionView
                }
            }
            mOverviewBtn->{
                Alog.info(TAG, "onClick on overviewBtn currentView $mCurrentView")
                if (mCurrentView != mOverviewView) {
                    mMainRelativelayout.removeView(mCurrentView)
                    mMainRelativelayout.addView(mOverviewView)
                    mCurrentView = mOverviewView
                }
            }
            mSettingsBtn->{
                Alog.info(TAG, "onClick on settingsBtn currentView $mCurrentView")
                if (mCurrentView != mSettingsView) {
                    mMainRelativelayout.removeView(mCurrentView)
                    mMainRelativelayout.addView(mSettingsView)
                    mCurrentView = mSettingsView
                }
            }
            else->{
                Alog.warn(TAG, "onClick on unknown view $view")
            }
        }
    }
}