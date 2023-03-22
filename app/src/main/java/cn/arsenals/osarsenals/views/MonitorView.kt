package cn.arsenals.osarsenals.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import cn.arsenals.osarsenals.R
import cn.arsenals.osarsenals.manager.DeviceStatusManager
import cn.arsenals.osarsenals.model.DeviceStatusInfo
import cn.arsenals.osarsenals.utils.Alog
import kotlin.math.roundToInt

class MonitorView : RelativeLayout {
    private val TAG = "RelativeLayout"
    private val mEachCpuUtilizationList = ArrayList<Int>()

    private val mDeviceInfoCb = object : DeviceStatusManager.IDeviceInfoCb{
        override fun onDeviceInfoRefresh(info: DeviceStatusInfo) {
            Alog.verbose(TAG, "onDeviceInfoRefresh info $info")
            post {
                if (mEachCpuUtilizationList.size != info.mCpuUtilizationList.size - 1) {
                    mEachCpuUtilizationList.clear()
                    for (i in 1 until info.mCpuUtilizationList.size) {
                        mEachCpuUtilizationList.add(info.mCpuUtilizationList[i])
                    }
                } else {
                    for (i in 0 until mEachCpuUtilizationList.size) {
                        mEachCpuUtilizationList[i] = info.mCpuUtilizationList[i + 1]
                    }
                }
                if (mCpuInfoRectView.updatePercentage(mEachCpuUtilizationList)) {
                    mCpuInfoRectView.invalidate()
                }
                if (mGpuInfoCircleView.updatePercentage(info.mGpuBusy)) {
                    mGpuInfoCircleView.invalidate()
                }
                if (mBatteryInfoCircleView.updatePercentage(info.mBatteryCapacity)) {
                    mBatteryInfoCircleView.invalidate()
                }
                mCpuInfoTextView.text = "${info.mCpuTemp}℃"
                mGpuInfoInnerTextView.text = "${info.mGpuBusy}%"
                mGpuInfoTextView.text = "${info.mGpuFreq}MHz"
                mBatteryInfoInnerTextView.text = "${info.mBatteryCapacity}%"
                mBatteryInfoTextView.text = "${info.mBatteryTemp}℃"

                var cpuDetailText = ""
                for (i in 0 until info.mTotalCpuCount) {
                    cpuDetailText += "#$i "
                    if (!info.mCpuOnlineList[i]) {
                        cpuDetailText += "OFFLINE"
                    } else {
                        cpuDetailText += "${info.mCpuFreqList[i].roundToInt()}MHz ${info.mCpuUtilizationList[i + 1]}%"
                    }
                    if (i != info.mTotalCpuCount - 1) {
                        cpuDetailText += "\n"
                    }
                }
                mCpuDetailTextView.text = cpuDetailText
                mFpsTextView.text = "FPS\n${info.mFps}"
                mRamTextView.text = "RAM\n${info.mRamUtilization}%"
                mCurrentTextView.text = "CUR\n${info.mBatteryCurrent}mA"
                mPowerTextView.text = "PWR\n${String.format("%.2f", info.mBatteryPower)}W"
            }
        }
    }

    private lateinit var mView: View
    private lateinit var mCpuInfoRectView: PercentageRectView
    private lateinit var mCpuInfoTextView: TextView
    private lateinit var mGpuInfoCircleView: PercentageCircleView
    private lateinit var mGpuInfoInnerTextView: TextView
    private lateinit var mGpuInfoTextView: TextView
    private lateinit var mBatteryInfoCircleView: PercentageCircleView
    private lateinit var mBatteryInfoInnerTextView: TextView
    private lateinit var mBatteryInfoTextView: TextView
    private lateinit var mCpuDetailTextView: TextView
    private lateinit var mFpsTextView: TextView
    private lateinit var mRamTextView: TextView
    private lateinit var mCurrentTextView: TextView
    private lateinit var mPowerTextView: TextView

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
        mView = inflater.inflate(R.layout.monitor_view, this)

        mCpuInfoRectView = findViewById(R.id.monitor_cpu_info_rect)
        mCpuInfoTextView = findViewById(R.id.monitor_cpu_info_text)
        mGpuInfoCircleView = findViewById(R.id.monitor_gpu_info_circle)
        mGpuInfoInnerTextView = findViewById(R.id.monitor_gpu_info_inner_text)
        mGpuInfoTextView = findViewById(R.id.monitor_gpu_info_text)
        mBatteryInfoCircleView = findViewById(R.id.monitor_battery_info_circle)
        mBatteryInfoInnerTextView = findViewById(R.id.monitor_battery_info_inner)
        mBatteryInfoTextView = findViewById(R.id.monitor_battery_info_text)

        mCpuDetailTextView = findViewById(R.id.monitor_cpu_detail_text)
        mFpsTextView = findViewById(R.id.monitor_fps_text)
        mRamTextView = findViewById(R.id.monitor_ram_text)
        mCurrentTextView = findViewById(R.id.monitor_cur_text)
        mPowerTextView = findViewById(R.id.monitor_pwr_text)

        DeviceStatusManager.getInstance().registerDeviceInfoCb(mDeviceInfoCb)
    }

    private fun deinitView() {
        DeviceStatusManager.getInstance().unregisterDeviceInfoCb(mDeviceInfoCb)
    }

    private fun updateViewInfo() {

    }
}
