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
        sb.append("DeviceStatusInfo {")
                .append("mTotalCpuCount ").append(totalCpuCount).append(System.lineSeparator())
                .append("mCpuFreqList ").append(cpuFreqList).append(System.lineSeparator())
                .append("mCpuOnlineList ").append(cpuOnlineList).append(System.lineSeparator())
                .append("mCpuUtilizationList ").append(cpuUtilizationList).append(System.lineSeparator())
                .append("mGpuFreq ").append(gpuFreq).append(System.lineSeparator())
                .append("mGpuBusy ").append(gpuBusy).append(System.lineSeparator())
                .append("mFps ").append(fps).append(System.lineSeparator())
                .append("mBatteryCapacity ").append(batteryCapacity).append(System.lineSeparator())
                .append("mBatteryCurrent ").append(batteryCurrent).append(System.lineSeparator())
                .append("mBatteryVoltage ").append(batteryVoltage).append(System.lineSeparator())
                .append("mTotalCpuCount ").append(totalCpuCount).append(System.lineSeparator())
                .append("mBatteryPower ").append(batteryPower).append(System.lineSeparator())
                .append("mBatteryTemp ").append(batteryTemp).append(System.lineSeparator())
                .append("mAvailableRam ").append(availableRam).append(System.lineSeparator())
                .append("mTotalRam ").append(totalRam).append(System.lineSeparator())
                .append("mRamUtilization ").append(ramUtilization).append(System.lineSeparator())
                .append("mTotalCpuCount ").append(totalCpuCount).append(System.lineSeparator());
        return sb.toString();
    }
}
