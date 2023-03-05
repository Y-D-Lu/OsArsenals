package cn.arsneals.osarsenals.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import cn.arsneals.osarsenals.utils.Alog

class PercentageRectView : View {
    private val TAG = "PercentageRectView"

    private val mRectBackgroundPaint = Paint()
    private val mRectPaint = Paint()

    private val mRectList: ArrayList<Rect> = ArrayList()
    private val mBackgroundRectList: ArrayList<Rect> = ArrayList()

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        Alog.debug(TAG, "initView")
        initPaint()
    }

    private fun initPaint() {
        mRectBackgroundPaint.isAntiAlias = true
        mRectBackgroundPaint.color = 0x4D000000
        mRectPaint.isAntiAlias = true
        mRectPaint.color = 0x4D00FFFF
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?:let {
            Alog.warn(TAG, "onDraw canvas is null, return!")
            return
        }

        if (mRectList.isEmpty()) {
            Alog.warn(TAG, "onDraw mRectList is empty, return!")
            return
        }

        for (rect in mBackgroundRectList) {
            canvas.drawRect(rect, mRectBackgroundPaint)
        }
        for (rect in mRectList) {
            canvas.drawRect(rect, mRectPaint)
        }
    }

    fun updatePercentage(list: ArrayList<Int>): Boolean {
        if (mRectList.size != list.size) {
            mRectList.clear()
            val rectHeight = height
            val rectWidth = (width.toDouble() / list.size).toInt()
            for (i in 0 until list.size) {
                mRectList.add(Rect(rectWidth * i, 0, rectWidth * (i + 1), rectHeight))
            }
        }
        if (mBackgroundRectList.size != list.size) {
            mBackgroundRectList.clear()
            val rectHeight = height
            val rectWidth = (width.toDouble() / list.size).toInt()
            for (i in 0 until list.size) {
                mBackgroundRectList.add(Rect(rectWidth * i, 0, rectWidth * (i + 1), rectHeight))
            }
        }
        for (i in 0 until list.size) {
            mRectList[i].top = height - (height * list[i] / 100)
        }
        return true
    }
}
