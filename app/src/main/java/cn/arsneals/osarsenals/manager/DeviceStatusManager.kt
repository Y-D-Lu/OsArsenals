package cn.arsneals.osarsenals.manager

import cn.arsneals.osarsenals.model.DeviceStatusInfo
import cn.arsneals.osarsenals.utils.Alog
import cn.arsneals.osarsenals.utils.DeviceStatusUtil
import java.util.*
import kotlin.collections.ArrayList

class DeviceStatusManager {
    private val TAG = "DeviceStatusManager"

    private val mDeviceStatusInfo = DeviceStatusInfo()

    private val mCpuFreqList = ArrayList<Double>()
    private val mCpuOnlineList = ArrayList<Boolean>()
    private val mCpuUtilizationList = ArrayList<Int>()
    private val mCpuLastTotalTimeList = ArrayList<Int>()
    private val mCpuLastIdleTimeList = ArrayList<Int>()

    private var mTimer : Timer? = null
    private var mRef = 0

    private var mTotalCpuCount = DeviceStatusUtil.getTotalCpuCount()
    private var mCpuTemp = 0.0
    private var mGpuFreq = 0
    private var mGpuBusy = 0
    private var mCpuUtilization = 0.0
    private var mBatteryCapacity = 0
    private var mBatteryTemp = 0
    private var mFps = 0.0
    private var mTotalRam = 0
    private var mAvailableRam = 0
    private var mRamUtilization = 0
    private var mBatteryCurrent = 0
    private var mBatteryVoltage = 0
    private var mBatteryPower = 0.0

    private var mDeviceInfoCbList = ArrayList<IDeviceInfoCb>()

    companion object {
        fun getInstance() = Instance.sInstance
    }

    object Instance {
        val sInstance = DeviceStatusManager()
    }

    interface IDeviceInfoCb {
        fun onDeviceInfoRefresh(info: DeviceStatusInfo)
    }

    fun init() {
        Alog.info(TAG, "DeviceStatusManager init")
        startTimer()
    }

    fun uninit() {
        Alog.info(TAG, "DeviceStatusManager uninit")
        stopTimer()
    }

    private fun startTimer() {
        mRef++
        mTimer?.let {
            Alog.warn(TAG, "startTimer mTimer not null, not start")
        } ?: let {
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    onTimerScheduled()
                }
            }, 0, 1000)
        }
    }

    private fun stopTimer() {
        mRef--
        if (mRef <= 0) {
            mTimer?.cancel()
            mTimer = null
        }
    }

    fun registerDeviceInfoCb(cb: IDeviceInfoCb) {
        if (mDeviceInfoCbList.contains(cb)) {
            Alog.warn(TAG, "registerDeviceInfoCb mDeviceInfoCbList contains $cb, return!")
            return
        }
        mDeviceInfoCbList.add(cb)
    }

    fun unregisterDeviceInfoCb(cb: IDeviceInfoCb) {
        if (!mDeviceInfoCbList.contains(cb)) {
            Alog.warn(TAG, "unregisterDeviceInfoCb mDeviceInfoCbList !contains $cb, return!")
            return
        }
        mDeviceInfoCbList.remove(cb)
    }

    private fun onTimerScheduled() {
        mTotalCpuCount = DeviceStatusUtil.getTotalCpuCount()
        if (mCpuFreqList.size != mTotalCpuCount) {
            mCpuFreqList.clear()
            for (i in 0 until mTotalCpuCount) {
                mCpuFreqList.add(DeviceStatusUtil.getCurrentCpuFreq(i))
            }
        } else {
            for (i in 0 until mTotalCpuCount) {
                mCpuFreqList[i] = DeviceStatusUtil.getCurrentCpuFreq(i)
            }
        }

        var availableCpuCount = 0
        if (mCpuOnlineList.size != mTotalCpuCount) {
            mCpuOnlineList.clear()
            for (i in 0 until mTotalCpuCount) {
                val isOnline = DeviceStatusUtil.isCpuOnline(i)
                mCpuOnlineList.add(isOnline)
                if (isOnline) {
                    availableCpuCount++
                }
            }
        } else {
            for (i in 0 until mTotalCpuCount) {
                val isOnline = DeviceStatusUtil.isCpuOnline(i)
                mCpuOnlineList[i] = DeviceStatusUtil.isCpuOnline(i)
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
                if (mCpuLastTotalTimeList.size != listSize
                    || mCpuLastIdleTimeList.size != listSize || mCpuUtilizationList.size != listSize) {
                    mCpuLastTotalTimeList.clear()
                    mCpuLastIdleTimeList.clear()
                    mCpuUtilizationList.clear()
                    for (i in 0 until  listSize) {
                        val totalTime = cpuUtilizationStrList[i * 2 + 1].toInt()
                        val idleTime = cpuUtilizationStrList[i * 2].toInt()
                        mCpuLastTotalTimeList.add(totalTime)
                        mCpuLastIdleTimeList.add(idleTime)
                        mCpuUtilizationList.add(0)
                    }
                } else {
                    for (i in 0 until  listSize) {
                        val totalTime = cpuUtilizationStrList[i * 2 + 1].toInt()
                        val idleTime = cpuUtilizationStrList[i * 2].toInt()
                        mCpuUtilizationList[i] = 100 - (idleTime - mCpuLastIdleTimeList[i]) * 100 / (totalTime - mCpuLastTotalTimeList[i])
                        mCpuLastTotalTimeList[i] = totalTime
                        mCpuLastIdleTimeList[i] = idleTime
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

        mCpuTemp = DeviceStatusUtil.getCpuTemperature()
        mGpuFreq = DeviceStatusUtil.getCurrentGpuFreq()
        mGpuBusy = DeviceStatusUtil.getGpuBusy()

        mFps = DeviceStatusUtil.getCurrentFps()

        mBatteryCapacity = DeviceStatusUtil.getBatteryCapacity()
        mBatteryCurrent = DeviceStatusUtil.getBatteryCurrent()
        mBatteryVoltage = DeviceStatusUtil.getBatteryVoltage()
        mBatteryPower = mBatteryVoltage * mBatteryCurrent / 1000000.0
        mBatteryTemp = DeviceStatusUtil.getBatteryTemperature()

        mAvailableRam = DeviceStatusUtil.getAvailableMemory()
        mTotalRam = DeviceStatusUtil.getTotalMemory()
        if (mTotalRam != 0) {
            mRamUtilization = (100.0 - mAvailableRam * 100.0 / mTotalRam).toInt()
        } else {
            mRamUtilization = 0
        }

//        Alog.verbose(TAG, "onTimerScheduled mTotalCpuCount $mTotalCpuCount\n" +
//            " mCpuFreqList ${mCpuFreqList} mCpuOnlineList $mCpuOnlineList\n" +
//            " mGpuFreq $mGpuFreq mGpuBusy $mGpuBusy mFps $mFps" +
//            " mBatteryCapacity $mBatteryCapacity mBatteryCurrent $mBatteryCurrent" +
//            " mBatteryVoltage $mBatteryVoltage mBatteryPower $mBatteryPower mBatteryTemp $mBatteryTemp\n" +
//            " mAvailableRam $mAvailableRam mTotalRam $mTotalRam mRamUtilization $mRamUtilization")

        mDeviceStatusInfo.mCpuFreqList = mCpuFreqList
        mDeviceStatusInfo.mCpuOnlineList = mCpuOnlineList
        mDeviceStatusInfo.mCpuUtilizationList = mCpuUtilizationList
        mDeviceStatusInfo.mTotalCpuCount = mTotalCpuCount
        mDeviceStatusInfo.mCpuTemp = mCpuTemp
        mDeviceStatusInfo.mGpuFreq = mGpuFreq
        mDeviceStatusInfo.mGpuBusy = mGpuBusy
        mDeviceStatusInfo.mCpuUtilization = mCpuUtilization
        mDeviceStatusInfo.mBatteryCapacity = mBatteryCapacity
        mDeviceStatusInfo.mBatteryTemp = mBatteryTemp
        mDeviceStatusInfo.mFps = mFps
        mDeviceStatusInfo.mTotalRam = mTotalRam
        mDeviceStatusInfo.mAvailableRam = mAvailableRam
        mDeviceStatusInfo.mRamUtilization = mRamUtilization
        mDeviceStatusInfo.mBatteryCurrent = mBatteryCurrent
        mDeviceStatusInfo.mBatteryVoltage = mBatteryVoltage
        mDeviceStatusInfo.mBatteryPower = mBatteryPower

        for (cb in mDeviceInfoCbList) {
            cb.onDeviceInfoRefresh(mDeviceStatusInfo)
        }
    }
}
