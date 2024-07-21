package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.ui.GenshinImpactArsenalsActivity;
import cn.arsenals.osarsenals.ui.OnHookActivity;
import cn.arsenals.osarsenals.ui.PerformanceTestActivity;
import cn.arsenals.osarsenals.ui.StandbyActivity;
import cn.arsenals.osarsenals.utils.Alog;

public class FunctionView extends RelativeLayout {
    private static final String TAG = "FunctionView";

    private View view;
    private Button onHookBtn;
    private Button performanceTestBtn;
    private Button standbyBtn;
    private Button genshinBtn;

    public FunctionView(Context context) {
        super(context);
        initView();
    }

    public FunctionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FunctionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public FunctionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.function_view, this);

        onHookBtn = findViewById(R.id.func_on_hook_btn);
        onHookBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "onHookBtn onClick");
                Intent intent = new Intent(getContext(), OnHookActivity.class);
                getContext().startActivity(intent);
            }
        });

        performanceTestBtn = findViewById(R.id.func_performance_test_btn);
        performanceTestBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "performanceTestBtn onClick");
                Intent intent = new Intent(getContext(), PerformanceTestActivity.class);
                getContext().startActivity(intent);
            }
        });

        standbyBtn = findViewById(R.id.func_standby_btn);
        standbyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "standbyBtn onClick");
                Intent intent = new Intent(getContext(), StandbyActivity.class);
                getContext().startActivity(intent);
            }
        });

        genshinBtn = findViewById(R.id.func_genshin_btn);
        genshinBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "genshinBtn onClick");
                Intent intent = new Intent(getContext(), GenshinImpactArsenalsActivity.class);
                getContext().startActivity(intent);
            }
        });
    }
}
