package cn.arsneals.osarsenals.manager

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import cn.arsneals.osarsenals.OsApplication
import cn.arsneals.osarsenals.utils.Alog
import cn.arsneals.osarsenals.views.MonitorView

class OverviewViewManager {
    private val TAG = "OverviewViewManager"

    private val mHandlerThread = HandlerThread("OverviewViewManagerHandlerThread")
    private var mHandler: Handler

    private val mWindowManager = OsApplication.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mLayoutParams = WindowManager.LayoutParams()
    private val mView: MonitorView = MonitorView(OsApplication.application)
    private val mTouchStartPoint = Point()

    companion object {
        fun getInstance() = Instance.sInstance
    }

    object Instance {
        val sInstance = OverviewViewManager()
    }

    init {
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
        mView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchStartPoint.x = event.x.toInt()
                    mTouchStartPoint.y = event.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    mLayoutParams.x = event.rawX.toInt() - mTouchStartPoint.x
                    mLayoutParams.y = event.rawY.toInt() - mTouchStartPoint.y
                    mWindowManager.updateViewLayout(mView, mLayoutParams)
                }
                else -> {
                }
            }
            true
        }
    }

    fun init() {
        Alog.info(TAG, "OverviewViewManager init")

        mLayoutParams.type = 2018
        mLayoutParams.x = 0
        mLayoutParams.y = 0
        mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        // mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams.gravity = Gravity.START or Gravity.TOP
        mLayoutParams.format = PixelFormat.RGBA_8888
    }

    fun uninit() {
        Alog.info(TAG, "OverviewViewManager uninit")
    }

    fun addView() {
        mHandler.post {
            mLayoutParams.x = 0
            mLayoutParams.y = 0
            mWindowManager.addView(mView, mLayoutParams)
        }
    }

    fun removeView() {
        mHandler.post {
            mWindowManager.removeView(mView)
        }
    }
}