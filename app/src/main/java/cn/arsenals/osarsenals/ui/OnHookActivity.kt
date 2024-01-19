package cn.arsenals.osarsenals.ui

import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cn.arsenals.osarsenals.R
import cn.arsenals.osarsenals.utils.Alog


class OnHookActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "OnHookActivity"
    }

    var isFullScreen = false
    var screenBrightness = 0
    var screenBrightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_hook)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.setOnTouchListener({ v, event ->
            Alog.info(TAG, "OnHookActivity OnTouch ACTION_DOWN")
            if (event.action == MotionEvent.ACTION_DOWN) {
                setFullScreenStatus()
            }
            true
        })
    }

    override fun onResume() {
        super.onResume()

        screenBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        screenBrightnessMode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
        Alog.info(TAG, "onResume save screenBrightness $screenBrightness screenBrightnessMode $screenBrightnessMode")

        Settings.System.putInt(
            contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 1)
    }

    override fun onPause() {
        super.onPause()

        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, screenBrightness)
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, screenBrightnessMode)
        Alog.info(TAG, "onPause restore screenBrightness $screenBrightness screenBrightnessMode $screenBrightnessMode")
    }

    private fun setFullScreenStatus() {
        if (isFullScreen) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            supportActionBar?.show()
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            supportActionBar?.hide()
        }
        isFullScreen = !isFullScreen
    }
}