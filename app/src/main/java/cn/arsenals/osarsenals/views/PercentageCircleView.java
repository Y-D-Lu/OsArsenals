package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.arsenals.osarsenals.utils.Alog;

public class PercentageCircleView extends View {
    private static final String TAG = "PercentageCircelView";

    private RectF mCircleRectF = new RectF();
    private Paint mCircleBackgroundPaint = new Paint();
    private Paint mCirclePaint = new Paint();

    private int mPercentage = 0;
    private double mRadius = 0.0;

    public PercentageCircleView(Context context) {
        super(context);
        initView();
    }

    public PercentageCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PercentageCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public PercentageCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        initPaint();
    }

    private void initPaint() {
        mCircleBackgroundPaint.setAntiAlias(true);
        mCircleBackgroundPaint.setStyle(Paint.Style.STROKE);
        mCircleBackgroundPaint.setColor(0x4D000000);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(0x4D00FFFF);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = w < h ? w / 2.0 : h / 2.0;

        mCircleRectF.left = (float) (mRadius - (mRadius * 0.66));
        mCircleRectF.top = (float) (mRadius - (mRadius * 0.66));
        mCircleRectF.right = (float) (mRadius + (mRadius * 0.66));
        mCircleRectF.bottom = (float) (mRadius + (mRadius * 0.66));

        mCircleBackgroundPaint.setStrokeWidth((float) (mRadius * 0.25));
        mCirclePaint.setStrokeWidth((float) (mRadius * 0.25));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas == null) {
            Alog.warn(TAG, "onDraw canvas is null, return!");
            return;
        }

        canvas.drawArc(mCircleRectF, 0.0f, 360.0f, false, mCircleBackgroundPaint);

        canvas.drawArc(mCircleRectF, -90.0f,
                (float) (360.0 * mPercentage / 100.0), false, mCirclePaint);
    }

    boolean updatePercentage(int percentage) {
        if (mPercentage == percentage) {
            return false;
        }
        mPercentage = percentage;
        return true;
    }
}
