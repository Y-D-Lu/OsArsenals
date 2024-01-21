package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class OverviewView extends RelativeLayout {
    private static final String TAG = "OverviewView";

    private View view;
    private View monitorView;

    public OverviewView(Context context) {
        super(context);
        initView();
    }

    public OverviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OverviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public OverviewView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.overview_view, this);
        monitorView = findViewById(R.id.overview_monitorview);
    }
}
