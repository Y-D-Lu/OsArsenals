package cn.arsenals.osarsenals.manager

import cn.arsenals.osarsenals.model.DeviceStatusInfo
import cn.arsenals.osarsenals.utils.Alog
import cn.arsenals.osarsenals.utils.DeviceStatusUtil
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.ArrayList

class DeviceStatusManager {
    companion object {
        private const val TAG = "DeviceStatusManager"

        fun getInstance() = Instance.instance
    }

    object Instance {
        val instance = DeviceStatusManager()
    }

    interface IDeviceInfoCb {
        fun onDeviceInfoRefresh(info: DeviceStatusInfo)
    }

    private val deviceStatusInfo = DeviceStatusInfo()

    private val cpuFreqList = ArrayList<Double>()
    private val cpuOnlineList = ArrayList<Boolean>()
    private val cpuUtilizationList = ArrayList<Int>()
    private val cpuLastTotalTimeList = ArrayList<Int>()
    private val cpuLastIdleTimeList = ArrayList<Int>()

    private var timer : Timer? = null
    private var ref = 0

    private var totalCpuCount = DeviceStatusUtil.getTotalCpuCount()
    private var cpuTemp = 0.0
    private var gpuFreq = 0
    private var gpuBusy = 0
    private var cpuUtilization = 0.0
    private var batteryCapacity = 0
    private var batteryTemp = 0
    private var fps = 0.0
    private var totalRam = 0
    private var availableRam = 0
    private var ramUtilization = 0
    private var batteryCurrent = 0
    private var batteryVoltage = 0
    private var batteryPower = 0.0

    private var deviceInfoCbList = ArrayList<IDeviceInfoCb>()

    fun init() {
        Alog.info(TAG, "DeviceStatusManager init")
        startTimer()
    }

    fun uninit() {
        Alog.info(TAG, "DeviceStatusManager uninit")
        stopTimer()
    }

    private fun startTimer() {
        ref++
        timer?.let {
            Alog.warn(TAG, "startTimer mTimer not null, not start")
        } ?: let {
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    onTimerScheduled()
                }
            }, 0, 1000)
        }
    }

    private fun stopTimer() {
        ref--
        if (ref <= 0) {
            timer?.cancel()
            timer = null
        }
    }

    fun registerDeviceInfoCb(cb: IDeviceInfoCb) {
        if (deviceInfoCbList.contains(cb)) {
            Alog.warn(TAG, "registerDeviceInfoCb mDeviceInfoCbList contains $cb, return!")
            return
        }
        deviceInfoCbList.add(cb)
    }

    fun unregisterDeviceInfoCb(cb: IDeviceInfoCb) {
        if (!deviceInfoCbList.contains(cb)) {
            Alog.warn(TAG, "unregisterDeviceInfoCb mDeviceInfoCbList !contains $cb, return!")
            return
        }
        deviceInfoCbList.remove(cb)
    }

    private fun onTimerScheduled() {
        totalCpuCount = DeviceStatusUtil.getTotalCpuCount()
        if (cpuFreqList.size != totalCpuCount) {
            cpuFreqList.clear()
            for (i in 0 until totalCpuCount) {
                cpuFreqList.add(DeviceStatusUtil.getCurrentCpuFreq(i))
            }
        } else {
            for (i in 0 until totalCpuCount) {
                cpuFreqList[i] = DeviceStatusUtil.getCurrentCpuFreq(i)
            }
        }

        var availableCpuCount = 0
        if (cpuOnlineList.size != totalCpuCount) {
            cpuOnlineList.clear()
            for (i in 0 until totalCpuCount) {
                val isOnline = DeviceStatusUtil.isCpuOnline(i)
                cpuOnlineList.add(isOnline)
                if (isOnline) {
                    availableCpuCount++
                }
            }
        } else {
            for (i in 0 until totalCpuCount) {
                val isOnline = DeviceStatusUtil.isCpuOnline(i)
                cpuOnlineList[i] = DeviceStatusUtil.isCpuOnline(i)
                if (isOnline) {
                    availableCpuCount++
                }
            }
        }

        val cpuUtilizationStr = DeviceStatusUtil.getCpuUtilizationStr()
        val cpuUtilizationStrList = cpuUtilizationStr.split(",")

        val listSize = availableCpuCount + 1
        if (cpuUtilizationStrList.size == listSize * 2) {
            try {
                if (cpuLastTotalTimeList.size != listSize
                    || cpuLastIdleTimeList.size != listSize || cpuUtilizationList.size != listSize) {
                    cpuLastTotalTimeList.clear()
                    cpuLastIdleTimeList.clear()
                    cpuUtilizationList.clear()
                    for (i in 0 until  listSize) {
                        val totalTime = cpuUtilizationStrList[i * 2 + 1].toInt()
                        val idleTime = cpuUtilizationStrList[i * 2].toInt()
                        cpuLastTotalTimeList.add(totalTime)
                        cpuLastIdleTimeList.add(idleTime)
                        cpuUtilizationList.add(0)
                    }
                } else {
                    for (i in 0 until  listSize) {
                        val totalTime = cpuUtilizationStrList[i * 2 + 1].toInt()
                        val idleTime = cpuUtilizationStrList[i * 2].toInt()
                        cpuUtilizationList[i] = 100 - (idleTime - cpuLastIdleTimeList[i]) * 100 / (totalTime - cpuLastTotalTimeList[i])
                        cpuLastTotalTimeList[i] = totalTime
                        cpuLastIdleTimeList[i] = idleTime
                    }
                }
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getCpuUtilization NumberFormatException")
            }
        } else {
            Alog.warn(TAG,
                "cpuUtilizationStrList ${cpuUtilizationStrList}" +
                    " size ${cpuUtilizationStrList.size} not equals availableCpuCount $availableCpuCount")
        }

        cpuTemp = DeviceStatusUtil.getCpuTemperature()
        gpuFreq = DeviceStatusUtil.getCurrentGpuFreq()
        gpuBusy = DeviceStatusUtil.getGpuBusy()

        fps = DeviceStatusUtil.getCurrentFps()

        batteryCapacity = DeviceStatusUtil.getBatteryCapacity()
        batteryCurrent = DeviceStatusUtil.getBatteryCurrent()
        batteryVoltage = DeviceStatusUtil.getBatteryVoltage()
        batteryPower = batteryVoltage * batteryCurrent / 1000000.0
        batteryTemp = DeviceStatusUtil.getBatteryTemperature()

        availableRam = DeviceStatusUtil.getAvailableMemory()
        totalRam = DeviceStatusUtil.getTotalMemory()
        if (totalRam != 0) {
            ramUtilization = (100.0 - availableRam * 100.0 / totalRam).toInt()
        } else {
            ramUtilization = 0
        }

//        Alog.verbose(TAG, "onTimerScheduled mTotalCpuCount $mTotalCpuCount\n" +
//            " mCpuFreqList ${mCpuFreqList} mCpuOnlineList $mCpuOnlineList\n" +
//            " mGpuFreq $mGpuFreq mGpuBusy $mGpuBusy mFps $mFps" +
//            " mBatteryCapacity $mBatteryCapacity mBatteryCurrent $mBatteryCurrent" +
//            " mBatteryVoltage $mBatteryVoltage mBatteryPower $mBatteryPower mBatteryTemp $mBatteryTemp\n" +
//            " mAvailableRam $mAvailableRam mTotalRam $mTotalRam mRamUtilization $mRamUtilization")

        deviceStatusInfo.cpuFreqList = cpuFreqList
        deviceStatusInfo.cpuOnlineList = cpuOnlineList
        deviceStatusInfo.cpuUtilizationList = cpuUtilizationList
        deviceStatusInfo.totalCpuCount = totalCpuCount
        deviceStatusInfo.cpuTemp = cpuTemp
        deviceStatusInfo.gpuFreq = gpuFreq
        deviceStatusInfo.gpuBusy = gpuBusy
        deviceStatusInfo.cpuUtilization = cpuUtilization
        deviceStatusInfo.batteryCapacity = batteryCapacity
        deviceStatusInfo.batteryTemp = batteryTemp
        deviceStatusInfo.fps = fps
        deviceStatusInfo.totalRam = totalRam
        deviceStatusInfo.availableRam = availableRam
        deviceStatusInfo.ramUtilization = ramUtilization
        deviceStatusInfo.batteryCurrent = batteryCurrent
        deviceStatusInfo.batteryVoltage = batteryVoltage
        deviceStatusInfo.batteryPower = batteryPower

        for (cb in deviceInfoCbList) {
            cb.onDeviceInfoRefresh(deviceStatusInfo)
        }
    }
}
