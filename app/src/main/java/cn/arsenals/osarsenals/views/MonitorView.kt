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
    companion object {
        private const val TAG = "RelativeLayout"
    }

    private val eachCpuUtilizationList = ArrayList<Int>()

    private val deviceInfoCb = object : DeviceStatusManager.IDeviceInfoCb{
        override fun onDeviceInfoRefresh(info: DeviceStatusInfo) {
            Alog.verbose(TAG, "onDeviceInfoRefresh info $info")
            post {
                if (eachCpuUtilizationList.size != info.cpuUtilizationList.size - 1) {
                    eachCpuUtilizationList.clear()
                    for (i in 1 until info.cpuUtilizationList.size) {
                        eachCpuUtilizationList.add(info.cpuUtilizationList[i])
                    }
                } else {
                    for (i in 0 until eachCpuUtilizationList.size) {
                        eachCpuUtilizationList[i] = info.cpuUtilizationList[i + 1]
                    }
                }
                if (cpuInfoRectView.updatePercentage(eachCpuUtilizationList)) {
                    cpuInfoRectView.invalidate()
                }
                if (gpuInfoCircleView.updatePercentage(info.gpuBusy)) {
                    gpuInfoCircleView.invalidate()
                }
                if (batteryInfoCircleView.updatePercentage(info.batteryCapacity)) {
                    batteryInfoCircleView.invalidate()
                }
                cpuInfoTextView.text = "${info.cpuTemp}℃"
                gpuInfoInnerTextView.text = "${info.gpuBusy}%"
                gpuInfoTextView.text = "${info.gpuFreq}MHz"
                batteryInfoInnerTextView.text = "${info.batteryCapacity}%"
                batteryInfoTextView.text = "${info.batteryTemp}℃"

                var cpuDetailText = ""
                for (i in 0 until info.totalCpuCount) {
                    cpuDetailText += "#$i "
                    if (!info.cpuOnlineList[i]) {
                        cpuDetailText += "OFFLINE"
                    } else {
                        cpuDetailText += "${info.cpuFreqList[i].roundToInt()}MHz ${info.cpuUtilizationList[i + 1]}%"
                    }
                    if (i != info.totalCpuCount - 1) {
                        cpuDetailText += "\n"
                    }
                }
                cpuDetailTextView.text = cpuDetailText
                fpsTextView.text = "FPS\n${info.fps}"
                ramTextView.text = "RAM\n${info.ramUtilization}%"
                currentTextView.text = "CUR\n${info.batteryCurrent}mA"
                powerTextView.text = "PWR\n${String.format("%.2f", info.batteryPower)}W"
            }
        }
    }

    private lateinit var view: View
    private lateinit var cpuInfoRectView: PercentageRectView
    private lateinit var cpuInfoTextView: TextView
    private lateinit var gpuInfoCircleView: PercentageCircleView
    private lateinit var gpuInfoInnerTextView: TextView
    private lateinit var gpuInfoTextView: TextView
    private lateinit var batteryInfoCircleView: PercentageCircleView
    private lateinit var batteryInfoInnerTextView: TextView
    private lateinit var batteryInfoTextView: TextView
    private lateinit var cpuDetailTextView: TextView
    private lateinit var fpsTextView: TextView
    private lateinit var ramTextView: TextView
    private lateinit var currentTextView: TextView
    private lateinit var powerTextView: TextView

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
        view = inflater.inflate(R.layout.monitor_view, this)

        cpuInfoRectView = findViewById(R.id.monitor_cpu_info_rect)
        cpuInfoTextView = findViewById(R.id.monitor_cpu_info_text)
        gpuInfoCircleView = findViewById(R.id.monitor_gpu_info_circle)
        gpuInfoInnerTextView = findViewById(R.id.monitor_gpu_info_inner_text)
        gpuInfoTextView = findViewById(R.id.monitor_gpu_info_text)
        batteryInfoCircleView = findViewById(R.id.monitor_battery_info_circle)
        batteryInfoInnerTextView = findViewById(R.id.monitor_battery_info_inner)
        batteryInfoTextView = findViewById(R.id.monitor_battery_info_text)

        cpuDetailTextView = findViewById(R.id.monitor_cpu_detail_text)
        fpsTextView = findViewById(R.id.monitor_fps_text)
        ramTextView = findViewById(R.id.monitor_ram_text)
        currentTextView = findViewById(R.id.monitor_cur_text)
        powerTextView = findViewById(R.id.monitor_pwr_text)

        DeviceStatusManager.getInstance().registerDeviceInfoCb(deviceInfoCb)
    }

    private fun deinitView() {
        DeviceStatusManager.getInstance().unregisterDeviceInfoCb(deviceInfoCb)
    }

    private fun updateViewInfo() {

    }
}
