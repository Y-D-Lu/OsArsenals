package cn.arsneals.osarsenals.utils

import cn.arsneals.osarsenals.jni.ArsenalsJni
import cn.arsneals.osarsenals.root.ArsenalsRoot

class DeviceStatusUtil {
    companion object {
        val TAG = "DeviceStatusUtil"

        fun getAioStatus(): String {
            return ArsenalsJni().getAioStatus()
        }

        fun getTotalCpuCount(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromFile("/sys/devices/system/cpu/present").split("-")[1].toInt() + 1
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getTotalCpuCount NumberFormatException")
            } catch (ex: IndexOutOfBoundsException) {
                Alog.warn(TAG, "getTotalCpuCount IndexOutOfBoundsException")
            }
            return ret
        }

        fun getCurrentCpuFreq(cpuId: Int): Double {
            var ret = 0.0
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/devices/system/cpu/cpu$cpuId/cpufreq/scaling_cur_freq")
                    .toDouble() / 1000.0
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getCurrentCpuFreq NumberFormatException")
            }
            return ret
        }

        fun getCpuUtilizationStr(): String {
            return ArsenalsJni().getCpuUtilizationStr()
        }

        fun getCpuTemperature(): Double {
            var ret = 0.0
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/class/thermal/thermal_zone20/temp").toDouble() / 1000.0
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getBatteryCapacity NumberFormatException")
            }
            return ret
        }

        fun getCurrentGpuFreq(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromFile("/sys/class/kgsl/kgsl-3d0/gpuclk").toInt() / 1000000
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getCurrentGpuFreq NumberFormatException")
            }
            return ret
        }

        fun getGpuBusy(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromFile("/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage").toInt()
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getGpuBusy NumberFormatException")
            }
            return ret
        }

        fun isCpuOnline(cpuId: Int): Boolean {
            var ret = true
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/devices/system/cpu/cpu$cpuId/online").toInt() != 0
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "isCpuOnline NumberFormatException")
            }
            return ret
        }

        fun getBatteryVoltage(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/class/power_supply/battery/voltage_now").toInt() / 1000
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getBatteryVoltage NumberFormatException")
            }
            return ret
        }

        fun getBatteryCurrent(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/class/power_supply/battery/current_now").toInt() / 1000
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getBatteryCurrent NumberFormatException")
            }
            return ret
        }

        fun getBatteryCapacity(): Int {
            var ret = 100
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/class/power_supply/battery/capacity").toInt()
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getBatteryCapacity NumberFormatException")
            }
            return ret
        }

        fun getBatteryTemperature(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromFile(
                    "/sys/class/power_supply/battery/temp").toInt() / 10
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getBatteryCapacity NumberFormatException")
            }
            return ret
        }

        fun getCurrentFps(): Double {
            var ret = 0.0
            try {
                ret = ArsenalsJni().getStringValFromCmd(
                    "cat /sys/class/drm/sde-crtc-0/measured_fps | awk '{print \$2}'").toDouble()
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getCurrentFps NumberFormatException")
            }
            return ret
        }

        fun getAvailableMemory(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromCmd(
                    "cat /proc/meminfo | grep MemAvailable | awk '{print \$2}'").toInt()
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getCurrentFps NumberFormatException")
            }
            return ret
        }

        fun getTotalMemory(): Int {
            var ret = 0
            try {
                ret = ArsenalsJni().getStringValFromCmd(
                    "cat /proc/meminfo | grep MemTotal | awk '{print \$2}'").toInt()
            } catch (ex: NumberFormatException) {
                Alog.warn(TAG, "getCurrentFps NumberFormatException")
            }
            return ret
        }

        fun isThermalEnabled(): Boolean {
            val thermal1 = !"disabled".equals(ArsenalsJni().getStringValFromFile(
                "/sys/class/thermal/thermal_zone0/mode"))
            val thermal2 = !"disabled".equals(ArsenalsJni().getStringValFromFile(
                "/sys/class/thermal/thermal_zone1/mode"))
            Alog.verbose(TAG, "thermal1 $thermal1 thermal2 $thermal2")
            return thermal1 || thermal2
        }

        fun setThermalEnableStatus(isEnable: Boolean): Boolean {
            val str = if (isEnable) "enabled" else "disabled"
            val isSucc1 = ArsenalsRoot.getInstance().execAsRoot("echo $str > /sys/class/thermal/thermal_zone0/mode\n")
            val isSucc2 = ArsenalsRoot.getInstance().execAsRoot("echo $str > /sys/class/thermal/thermal_zone1/mode\n")
            if (isSucc1 && isSucc2) {
                Alog.verbose(TAG, "setThermalEnableStatus $isEnable exec succeed")
                return true
            }
            Alog.warn(TAG, "setThermalEnableStatus $isEnable exec failed!")
            return false
        }
    }
}
