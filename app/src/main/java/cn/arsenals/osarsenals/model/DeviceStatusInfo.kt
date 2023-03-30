package cn.arsenals.osarsenals.model

class DeviceStatusInfo {
    var cpuFreqList = ArrayList<Double>()
    var cpuOnlineList = ArrayList<Boolean>()
    var cpuUtilizationList = ArrayList<Int>()
    var totalCpuCount = 0
    var cpuTemp = 0.0
    var gpuFreq = 0
    var gpuBusy = 0
    var cpuUtilization = 0.0
    var batteryCapacity = 0
    var batteryTemp = 0
    var fps = 0.0
    var totalRam = 0
    var availableRam = 0
    var ramUtilization = 0
    var batteryCurrent = 0
    var batteryVoltage = 0
    var batteryPower = 0.0

    override fun toString(): String {
        val sb: StringBuilder = StringBuilder()
        sb.append("DeviceStatusInfo {").append("mTotalCpuCount $totalCpuCount\n" +
            " mCpuFreqList ${cpuFreqList} mCpuOnlineList $cpuOnlineList\n mCpuUtilizationList $cpuUtilizationList" +
            " mGpuFreq $gpuFreq mGpuBusy $gpuBusy mFps $fps" +
            " mBatteryCapacity $batteryCapacity mBatteryCurrent $batteryCurrent" +
            " mBatteryVoltage $batteryVoltage mBatteryPower $batteryPower mBatteryTemp $batteryTemp\n" +
            " mAvailableRam $availableRam mTotalRam $totalRam mRamUtilization $ramUtilization").append(
            "}")
        return sb.toString()
    }
}
