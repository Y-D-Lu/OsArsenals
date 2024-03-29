package cn.arsenals.osarsenals.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import cn.arsenals.osarsenals.utils.Alog

class PercentageRectView : View {
    companion object {
        private const val TAG = "PercentageRectView"
    }

    private val rectBackgroundPaint = Paint()
    private val rectPaint = Paint()

    private val rectList: ArrayList<Rect> = ArrayList()
    private val backgroundRectList: ArrayList<Rect> = ArrayList()

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
        rectBackgroundPaint.isAntiAlias = true
        rectBackgroundPaint.color = 0x4D000000
        rectPaint.isAntiAlias = true
        rectPaint.color = 0x4D00FFFF
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

        if (rectList.isEmpty()) {
            Alog.warn(TAG, "onDraw mRectList is empty, return!")
            return
        }

        for (rect in backgroundRectList) {
            canvas.drawRect(rect, rectBackgroundPaint)
        }
        for (rect in rectList) {
            canvas.drawRect(rect, rectPaint)
        }
    }

    fun updatePercentage(list: ArrayList<Int>): Boolean {
        if (rectList.size != list.size) {
            rectList.clear()
            val rectHeight = height
            val rectWidth = (width.toDouble() / list.size).toInt()
            for (i in 0 until list.size) {
                rectList.add(Rect(rectWidth * i, 0, rectWidth * (i + 1), rectHeight))
            }
        }
        if (backgroundRectList.size != list.size) {
            backgroundRectList.clear()
            val rectHeight = height
            val rectWidth = (width.toDouble() / list.size).toInt()
            for (i in 0 until list.size) {
                backgroundRectList.add(Rect(rectWidth * i, 0, rectWidth * (i + 1), rectHeight))
            }
        }
        for (i in 0 until list.size) {
            rectList[i].top = height - (height * list[i] / 100)
        }
        return true
    }
}
