package cn.arsenals.osarsenals.model

class DeviceStatusInfo {
    var mCpuFreqList = ArrayList<Double>()
    var mCpuOnlineList = ArrayList<Boolean>()
    var mCpuUtilizationList = ArrayList<Int>()
    var mTotalCpuCount = 0
    var mCpuTemp = 0.0
    var mGpuFreq = 0
    var mGpuBusy = 0
    var mCpuUtilization = 0.0
    var mBatteryCapacity = 0
    var mBatteryTemp = 0
    var mFps = 0.0
    var mTotalRam = 0
    var mAvailableRam = 0
    var mRamUtilization = 0
    var mBatteryCurrent = 0
    var mBatteryVoltage = 0
    var mBatteryPower = 0.0

    override fun toString(): String {
        val sb: StringBuilder = StringBuilder()
        sb.append("DeviceStatusInfo {").append("mTotalCpuCount $mTotalCpuCount\n" +
            " mCpuFreqList ${mCpuFreqList} mCpuOnlineList $mCpuOnlineList\n mCpuUtilizationList $mCpuUtilizationList" +
            " mGpuFreq $mGpuFreq mGpuBusy $mGpuBusy mFps $mFps" +
            " mBatteryCapacity $mBatteryCapacity mBatteryCurrent $mBatteryCurrent" +
            " mBatteryVoltage $mBatteryVoltage mBatteryPower $mBatteryPower mBatteryTemp $mBatteryTemp\n" +
            " mAvailableRam $mAvailableRam mTotalRam $mTotalRam mRamUtilization $mRamUtilization").append(
            "}")
        return sb.toString()
    }
}
