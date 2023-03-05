package cn.arsneals.osarsenals.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import cn.arsneals.osarsenals.utils.Alog

class PercentageCircleView : View {
    private val TAG = "PercentageCircleView"

    private val mCircleRectF: RectF = RectF()
    private val mCircleBackgroundPaint = Paint()
    private val mCirclePaint = Paint()

    private var mPercentage: Int = 0
    private var mRadius: Double = 0.0

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
        mCircleBackgroundPaint.isAntiAlias = true
        mCircleBackgroundPaint.style = Paint.Style.STROKE
        mCircleBackgroundPaint.color = 0x4D000000
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.color = 0x4D00FFFF
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = if (w < h) w / 2.0 else h / 2.0

        mCircleRectF.left = (mRadius - (mRadius * 0.66)).toFloat()
        mCircleRectF.top = (mRadius - (mRadius * 0.66)).toFloat()
        mCircleRectF.right = (mRadius + (mRadius * 0.66)).toFloat()
        mCircleRectF.bottom = (mRadius + (mRadius * 0.66)).toFloat()

        mCircleBackgroundPaint.strokeWidth = (mRadius * 0.25).toFloat()
        mCirclePaint.strokeWidth = (mRadius * 0.25).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?:let {
            Alog.warn(TAG, "onDraw canvas is null, return!")
            return
        }

        canvas.drawArc(mCircleRectF, 0.0f, 360.0f, false, mCircleBackgroundPaint)

        canvas.drawArc(mCircleRectF, -90.0f,
            (360.0 * mPercentage / 100.0).toFloat(), false, mCirclePaint)
    }

    fun updatePercentage(percentage: Int) : Boolean {
        if (mPercentage == percentage) {
            return false
        }
        mPercentage = percentage
        return true
    }
}
