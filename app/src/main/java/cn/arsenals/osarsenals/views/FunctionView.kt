package cn.arsenals.osarsenals.views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import cn.arsenals.osarsenals.R
import cn.arsenals.osarsenals.ui.OnHookActivity
import cn.arsenals.osarsenals.utils.Alog

class FunctionView : RelativeLayout {
    companion object {
        private const val TAG = "FunctionView"
    }

    private lateinit var view: View
    private lateinit var onHookBtn: Button

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
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.function_view, this)

        onHookBtn = findViewById(R.id.func_on_hook_btn)
        onHookBtn.setOnClickListener {
            Alog.info(TAG, "onHookBtn onClick")
            val intent = Intent(context, OnHookActivity::class.java)
            context.startActivity(intent)
        }
    }
}