package cn.arsenals.osarsenals.manager

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import cn.arsenals.osarsenals.OsApplication
import cn.arsenals.osarsenals.utils.Alog
import cn.arsenals.osarsenals.views.MonitorView

class OverviewViewManager {
    companion object {
        private const val TAG = "OverviewViewManager"

        fun getInstance() = Instance.instance
    }

    object Instance {
        val instance = OverviewViewManager()
    }

    private val handlerThread = HandlerThread("OverviewViewManagerHandlerThread")
    private var handler: Handler

    private val windowManager = OsApplication.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutParams = WindowManager.LayoutParams()
    private val view: MonitorView = MonitorView(OsApplication.application)
    private val touchStartPoint = Point()

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartPoint.x = event.x.toInt()
                    touchStartPoint.y = event.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = event.rawX.toInt() - touchStartPoint.x
                    layoutParams.y = event.rawY.toInt() - touchStartPoint.y
                    windowManager.updateViewLayout(view, layoutParams)
                }
                else -> {
                }
            }
            true
        }
    }

    fun init() {
        Alog.info(TAG, "OverviewViewManager init")

        layoutParams.type = 2018
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        // mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.format = PixelFormat.RGBA_8888
    }

    fun uninit() {
        Alog.info(TAG, "OverviewViewManager uninit")
    }

    fun addView() {
        handler.post {
            layoutParams.x = 0
            layoutParams.y = 0
            windowManager.addView(view, layoutParams)
        }
    }

    fun removeView() {
        handler.post {
            windowManager.removeView(view)
        }
    }
}