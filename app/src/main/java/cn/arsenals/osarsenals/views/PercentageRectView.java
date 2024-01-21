package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class PercentageRectView extends View {
    private static final String TAG = "PercentageRectView";

    private Paint rectBackgroundPaint = new Paint();
    private Paint rectPaint = new Paint();

    private ArrayList<Rect> rectList = new ArrayList<Rect>();
    private ArrayList<Rect> backgroundRectList = new ArrayList<Rect>();

    public PercentageRectView(Context context) {
        super(context);
        initView();
    }

    public PercentageRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PercentageRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public PercentageRectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        initPaint();
    }

    private void initPaint() {
        rectBackgroundPaint.setAntiAlias(true);
        rectBackgroundPaint.setColor(0x4D000000);
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(0x4D00FFFF);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas == null) {
            Alog.warn(TAG, "onDraw canvas is null, return!");
            return;
        }

        if (rectList.isEmpty()) {
            Alog.warn(TAG, "onDraw mRectList is empty, return!");
            return;
        }

        for (Rect rect : backgroundRectList) {
            canvas.drawRect(rect, rectBackgroundPaint);
        }
        for (Rect rect : rectList) {
            canvas.drawRect(rect, rectPaint);
        }
    }

    public boolean updatePercentage(ArrayList<Integer> list) {
        if (rectList.size() != list.size()) {
            rectList.clear();
            int rectHeight = getHeight();
            int rectWidth = getWidth() / list.size();
            for (int i = 0; i < list.size(); i++) {
                rectList.add(new Rect(rectWidth * i, 0, rectWidth * (i + 1), rectHeight));
            }
        }
        if (backgroundRectList.size() != list.size()) {
            backgroundRectList.clear();
            int rectHeight = getHeight();
            int rectWidth = getWidth() / list.size();
            for (int i = 0; i < list.size(); i++) {
                backgroundRectList.add(new Rect(rectWidth * i, 0, rectWidth * (i + 1), rectHeight));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            rectList.get(i).top = getHeight() - (getHeight() * list.get(i) / 100);
        }
        return true;
    }
}
