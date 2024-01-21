package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.ui.OnHookActivity;
import cn.arsenals.osarsenals.utils.Alog;

public class FunctionView extends RelativeLayout {
    private static final String TAG = "FunctionView";

    private View view;
    private Button onHookBtn;

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
    }
}
