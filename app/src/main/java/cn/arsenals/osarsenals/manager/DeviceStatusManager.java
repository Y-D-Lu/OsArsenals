package cn.arsenals.osarsenals.manager;

import cn.arsenals.osarsenals.model.DeviceStatusInfo;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.utils.DeviceStatusUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceStatusManager {
    private static final String TAG = "DeviceStatusManager";

    private static class SingletonHolder {
        private static final DeviceStatusManager sInstance = new DeviceStatusManager();
    }

    public static DeviceStatusManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public interface IDeviceInfoCb {
        void onDeviceInfoRefresh(DeviceStatusInfo info);
    }

    private DeviceStatusInfo deviceStatusInfo = new DeviceStatusInfo();

    private ArrayList<Double> cpuFreqList = new ArrayList<Double>();
    private ArrayList<Boolean> cpuOnlineList = new ArrayList<Boolean>();
    private ArrayList<Integer> cpuUtilizationList = new ArrayList<Integer>();
    private ArrayList<Integer> cpuLastTotalTimeList = new ArrayList<Integer>();
    private ArrayList<Integer> cpuLastIdleTimeList = new ArrayList<Integer>();

    private Timer timer = null;
    private int ref = 0;

    private int totalCpuCount = DeviceStatusUtil.getTotalCpuCount();
    private double cpuTemp = 0.0;
    private int gpuFreq = 0;
    private int gpuBusy = 0;
    private double cpuUtilization = 0.0;
    private int batteryCapacity = 0;
    private int batteryTemp = 0;
    private double fps = 0.0;
    private int totalRam = 0;
    private int availableRam = 0;
    private int ramUtilization = 0;
    private int batteryCurrent = 0;
    private int batteryVoltage = 0;
    private double batteryPower = 0.0;

    private ArrayList<IDeviceInfoCb> deviceInfoCbList = new ArrayList<IDeviceInfoCb>();

    public void init() {
        Alog.info(TAG, "DeviceStatusManager init");
    }

    public void uninit() {
        Alog.info(TAG, "DeviceStatusManager uninit");
    }

    public void startTimer() {
        ref++;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onTimerScheduled();
                }
            }, 0, 1000);
        } else {
            Alog.warn(TAG, "startTimer mTimer not null, not start");
        }
    }

    public void stopTimer() {
        ref--;
        if (ref <= 0 && timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void registerDeviceInfoCb(IDeviceInfoCb cb) {
        if (deviceInfoCbList.contains(cb)) {
            Alog.warn(TAG, "registerDeviceInfoCb mDeviceInfoCbList contains " + cb + ", return!");
            return;
        }
        deviceInfoCbList.add(cb);
    }

    public void unregisterDeviceInfoCb(IDeviceInfoCb cb) {
        if (!deviceInfoCbList.contains(cb)) {
            Alog.warn(TAG, "unregisterDeviceInfoCb mDeviceInfoCbList !contains \" + cb + \", return!");
            return;
        }
        deviceInfoCbList.remove(cb);
    }

    private void onTimerScheduled() {
        totalCpuCount = DeviceStatusUtil.getTotalCpuCount();
        if (cpuFreqList.size() != totalCpuCount) {
            cpuFreqList.clear();
            for (int i = 0; i < totalCpuCount; i++) {
                cpuFreqList.add(DeviceStatusUtil.getCurrentCpuFreq(i));
            }
        } else {
            for (int i = 0; i < totalCpuCount; i++) {
                cpuFreqList.set(i, DeviceStatusUtil.getCurrentCpuFreq(i));
            }
        }

        int availableCpuCount = 0;
        if (cpuOnlineList.size() != totalCpuCount) {
            cpuOnlineList.clear();
            for (int i = 0; i < totalCpuCount; i++) {
                boolean isOnline = DeviceStatusUtil.isCpuOnline(i);
                cpuOnlineList.add(isOnline);
                if (isOnline) {
                    availableCpuCount++;
                }
            }
        } else {
            for (int i = 0; i < totalCpuCount; i++) {
                boolean isOnline = DeviceStatusUtil.isCpuOnline(i);
                cpuOnlineList.set(i, DeviceStatusUtil.isCpuOnline(i));
                if (isOnline) {
                    availableCpuCount++;
                }
            }
        }

        String cpuUtilizationStr = DeviceStatusUtil.getCpuUtilizationStr();
        String[] cpuUtilizationStrList = cpuUtilizationStr.split(",");

        int listSize = availableCpuCount + 1;
        if (cpuUtilizationStrList.length == listSize * 2) {
            try {
                if (cpuLastTotalTimeList.size() != listSize
                        || cpuLastIdleTimeList.size() != listSize || cpuUtilizationList.size() != listSize) {
                    cpuLastTotalTimeList.clear();
                    cpuLastIdleTimeList.clear();
                    cpuUtilizationList.clear();
                    for (int i = 0; i < listSize; i++) {
                        int totalTime = Integer.parseInt(cpuUtilizationStrList[i * 2 + 1]);
                        int idleTime = Integer.parseInt(cpuUtilizationStrList[i * 2]);
                        cpuLastTotalTimeList.add(totalTime);
                        cpuLastIdleTimeList.add(idleTime);
                        cpuUtilizationList.add(0);
                    }
                } else {
                    for (int i = 0; i < listSize; i++) {
                        int totalTime = Integer.parseInt(cpuUtilizationStrList[i * 2 + 1]);
                        int idleTime = Integer.parseInt(cpuUtilizationStrList[i * 2]);
                        cpuUtilizationList.set(i, 100 - (idleTime - cpuLastIdleTimeList.get(i)) * 100 / (totalTime - cpuLastTotalTimeList.get(i)));
                        cpuLastTotalTimeList.set(i, totalTime);
                        cpuLastIdleTimeList.set(i, idleTime);
                    }
                }
            } catch (NumberFormatException ex) {
                Alog.warn(TAG, "getCpuUtilization NumberFormatException");
            }
        } else {
            Alog.warn(TAG,
                    "cpuUtilizationStrList " + cpuUtilizationStrList +
                            " size " + cpuUtilizationStrList.length + "not equals availableCpuCount " + availableCpuCount);
        }

        cpuTemp = DeviceStatusUtil.getCpuTemperature();
        gpuFreq = DeviceStatusUtil.getCurrentGpuFreq();
        gpuBusy = DeviceStatusUtil.getGpuBusy();

        fps = DeviceStatusUtil.getCurrentFps();

        batteryCapacity = DeviceStatusUtil.getBatteryCapacity();
        batteryCurrent = DeviceStatusUtil.getBatteryCurrent();
        batteryVoltage = DeviceStatusUtil.getBatteryVoltage();
        batteryPower = batteryVoltage * batteryCurrent / 1000000.0;
        batteryTemp = DeviceStatusUtil.getBatteryTemperature();

        availableRam = DeviceStatusUtil.getAvailableMemory();
        totalRam = DeviceStatusUtil.getTotalMemory();
        if (totalRam != 0) {
            ramUtilization = (int) (100.0 - availableRam * 100.0 / totalRam);
        } else {
            ramUtilization = 0;
        }

        Alog.verbose(TAG, "onTimerScheduled mTotalCpuCount " + totalCpuCount + "\n" +
                " mCpuFreqList " + cpuFreqList + " mCpuOnlineList " + cpuOnlineList + "\n" +
                " mGpuFreq " + gpuFreq + " mGpuBusy " + gpuBusy + " mFps " + fps +
                " mBatteryCapacity " + batteryCapacity + " mBatteryCurrent " + batteryCurrent +
                " mBatteryVoltage " + batteryVoltage + " mBatteryPower " + batteryPower +
                " mBatteryTemp " + batteryTemp + "\n" + " mAvailableRam " + availableRam +
                " mTotalRam " + totalRam + " mRamUtilization " + ramUtilization);

        deviceStatusInfo.cpuFreqList = cpuFreqList;
        deviceStatusInfo.cpuOnlineList = cpuOnlineList;
        deviceStatusInfo.cpuUtilizationList = cpuUtilizationList;
        deviceStatusInfo.totalCpuCount = totalCpuCount;
        deviceStatusInfo.cpuTemp = cpuTemp;
        deviceStatusInfo.gpuFreq = gpuFreq;
        deviceStatusInfo.gpuBusy = gpuBusy;
        deviceStatusInfo.cpuUtilization = cpuUtilization;
        deviceStatusInfo.batteryCapacity = batteryCapacity;
        deviceStatusInfo.batteryTemp = batteryTemp;
        deviceStatusInfo.fps = fps;
        deviceStatusInfo.totalRam = totalRam;
        deviceStatusInfo.availableRam = availableRam;
        deviceStatusInfo.ramUtilization = ramUtilization;
        deviceStatusInfo.batteryCurrent = batteryCurrent;
        deviceStatusInfo.batteryVoltage = batteryVoltage;
        deviceStatusInfo.batteryPower = batteryPower;

        for (IDeviceInfoCb cb : deviceInfoCbList) {
            cb.onDeviceInfoRefresh(deviceStatusInfo);
        }
    }
}
