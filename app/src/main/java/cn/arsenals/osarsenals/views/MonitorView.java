package cn.arsenals.osarsenals.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.manager.DeviceStatusManager;
import cn.arsenals.osarsenals.model.DeviceStatusInfo;
import cn.arsenals.osarsenals.utils.Alog;

public class MonitorView extends RelativeLayout {
    private static final String TAG = "MonitorView";

    private ArrayList<Integer> eachCpuUtilizationList = new ArrayList<Integer>();

    private DeviceStatusManager.IDeviceInfoCb deviceInfoCb = new DeviceStatusManager.IDeviceInfoCb() {
        @Override
        public void onDeviceInfoRefresh(DeviceStatusInfo info) {
            Alog.verbose(TAG, "onDeviceInfoRefresh info $info");

            if (eachCpuUtilizationList.size() != info.cpuUtilizationList.size() - 1) {
                eachCpuUtilizationList.clear();
                for (int i = 1; i < info.cpuUtilizationList.size(); i++){
                    eachCpuUtilizationList.add(info.cpuUtilizationList.get(i));
                }
            } else {
                for (int i = 0; i < eachCpuUtilizationList.size(); i++){
                    eachCpuUtilizationList.set(i, info.cpuUtilizationList.get(i + 1));
                }
            }
            if (cpuInfoRectView.updatePercentage(eachCpuUtilizationList)) {
                cpuInfoRectView.invalidate();
            }
            if (gpuInfoCircleView.updatePercentage(info.gpuBusy)) {
                gpuInfoCircleView.invalidate();
            }
            if (batteryInfoCircleView.updatePercentage(info.batteryCapacity)) {
                batteryInfoCircleView.invalidate();
            }
            cpuInfoTextView.setText("${info.cpuTemp}℃");
            gpuInfoInnerTextView.setText("${info.gpuBusy}%");
            gpuInfoTextView.setText("${info.gpuFreq}MHz");
            batteryInfoInnerTextView.setText("${info.batteryCapacity}%");
            batteryInfoTextView.setText("${info.batteryTemp}℃");

            String cpuDetailText = "";
            for (int i = 0; i < info.totalCpuCount; i++) {
                cpuDetailText += "#$i ";
                if (!info.cpuOnlineList.get(i)) {
                    cpuDetailText += "OFFLINE";
                } else {
                    cpuDetailText += "${info.cpuFreqList[i].roundToInt()}MHz ${info.cpuUtilizationList[i + 1]}%";
                }
                if (i != info.totalCpuCount - 1) {
                    cpuDetailText += "\n";
                }
            }
            cpuDetailTextView.setText(cpuDetailText);
            fpsTextView.setText("FPS\n${info.fps}");
            ramTextView.setText("RAM\n${info.ramUtilization}%");
            currentTextView.setText("CUR\n${info.batteryCurrent}mA");
            powerTextView.setText("PWR\n" + String.format(" % .2f " + ", info.batteryPower)}W"));
        }
    };

    private View view;
    private PercentageRectView cpuInfoRectView;
    private TextView cpuInfoTextView;
    private PercentageCircleView gpuInfoCircleView;
    private TextView gpuInfoInnerTextView;
    private TextView gpuInfoTextView;
    private PercentageCircleView batteryInfoCircleView;
    private TextView batteryInfoInnerTextView;
    private TextView batteryInfoTextView;
    private TextView cpuDetailTextView;
    private TextView fpsTextView;
    private TextView ramTextView;
    private TextView currentTextView;
    private TextView powerTextView;

    public MonitorView(Context context) {
        super(context);
        initView();
    }

    public MonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MonitorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public MonitorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        Alog.debug(TAG, "initView");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.monitor_view, this);

        cpuInfoRectView = findViewById(R.id.monitor_cpu_info_rect);
        cpuInfoTextView = findViewById(R.id.monitor_cpu_info_text);
        gpuInfoCircleView = findViewById(R.id.monitor_gpu_info_circle);
        gpuInfoInnerTextView = findViewById(R.id.monitor_gpu_info_inner_text);
        gpuInfoTextView = findViewById(R.id.monitor_gpu_info_text);
        batteryInfoCircleView = findViewById(R.id.monitor_battery_info_circle);
        batteryInfoInnerTextView = findViewById(R.id.monitor_battery_info_inner);
        batteryInfoTextView = findViewById(R.id.monitor_battery_info_text);

        cpuDetailTextView = findViewById(R.id.monitor_cpu_detail_text);
        fpsTextView = findViewById(R.id.monitor_fps_text);
        ramTextView = findViewById(R.id.monitor_ram_text);
        currentTextView = findViewById(R.id.monitor_cur_text);
        powerTextView = findViewById(R.id.monitor_pwr_text);

        DeviceStatusManager.getInstance().registerDeviceInfoCb(deviceInfoCb);
    }

    private void deinitView() {
        DeviceStatusManager.getInstance().unregisterDeviceInfoCb(deviceInfoCb);
    }

    private void updateViewInfo() {

    }
}
