package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class GenshinImpactArsenalsView extends RelativeLayout {
    private static final String TAG = "GenshinImpactArsenalsView";

    private View view;
    private TextView statusTextView;
    private TextView extraTextView;

    public GenshinImpactArsenalsView(Context context) {
        super(context);
        initView();
    }

    public GenshinImpactArsenalsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GenshinImpactArsenalsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public GenshinImpactArsenalsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.genshin_impact_arsenals_view, this);

        statusTextView = findViewById(R.id.gia_view_tv_status);
        extraTextView = findViewById(R.id.gia_view_tv_extra);
    }

    public void updateTextView(String status, String extra) {
        post(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(status);
                extraTextView.setText(extra);
                statusTextView.invalidate();
                extraTextView.invalidate();
            }
        });
    }
}
