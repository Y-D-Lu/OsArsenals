package cn.arsenals.osarsenals.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DeviceStatusInfo {
    public ArrayList<Double> cpuFreqList = new ArrayList<Double>();
    public ArrayList<Boolean> cpuOnlineList = new ArrayList<Boolean>();
    public ArrayList<Integer> cpuUtilizationList = new ArrayList<Integer>();
    public int totalCpuCount = 0;
    public double cpuTemp = 0.0;
    public int gpuFreq = 0;
    public int gpuBusy = 0;
    public double cpuUtilization = 0.0;
    public int batteryCapacity = 0;
    public int batteryTemp = 0;
    public double fps = 0.0;
    public int totalRam = 0;
    public int availableRam = 0;
    public int ramUtilization = 0;
    public int batteryCurrent = 0;
    public int batteryVoltage = 0;
    public double batteryPower = 0.0;

    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeviceStatusInfo {").append("mTotalCpuCount $totalCpuCount\n" +
                " mCpuFreqList ${cpuFreqList} mCpuOnlineList $cpuOnlineList\n mCpuUtilizationList $cpuUtilizationList" +
                " mGpuFreq $gpuFreq mGpuBusy $gpuBusy mFps $fps" +
                " mBatteryCapacity $batteryCapacity mBatteryCurrent $batteryCurrent" +
                " mBatteryVoltage $batteryVoltage mBatteryPower $batteryPower mBatteryTemp $batteryTemp\n" +
                " mAvailableRam $availableRam mTotalRam $totalRam mRamUtilization $ramUtilization").append(
                "}");
        return sb.toString();
    }
}
