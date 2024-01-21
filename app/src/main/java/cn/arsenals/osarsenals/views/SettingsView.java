package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class SettingsView extends RelativeLayout {
    private static final String TAG = "SettingsView";

    private View view;

    public SettingsView(Context context) {
        super(context);
        initView();
    }

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.settings_view, this);
    }
}
