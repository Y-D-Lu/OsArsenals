package cn.arsenals.osarsenals.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import cn.arsenals.osarsenals.R
import cn.arsenals.osarsenals.utils.Alog

class SettingsView : RelativeLayout {
    companion object {
        private const val TAG = "SettingsView"
    }

    private lateinit var view: View

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
        view = inflater.inflate(R.layout.settings_view, this)
    }
}