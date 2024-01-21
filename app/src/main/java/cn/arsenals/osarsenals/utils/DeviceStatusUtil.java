package cn.arsenals.osarsenals.utils;

import cn.arsenals.osarsenals.jni.ArsenalsJni;
import cn.arsenals.osarsenals.root.ArsenalsRoot;

public class DeviceStatusUtil {
    private static final String TAG = "DeviceStatusUtil";

    public static String getAioStatus() {
        return ArsenalsJni.getAioStatus();
    }

    public static int getTotalCpuCount() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile("/sys/devices/system/cpu/present").split("-")[1]) + 1;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getTotalCpuCount NumberFormatException");
        } catch (IndexOutOfBoundsException ex) {
            Alog.warn(TAG, "getTotalCpuCount IndexOutOfBoundsException");
        }
        return ret;
    }

    public static double getCurrentCpuFreq(int cpuId) {
        double ret = 0.0;
        try {
            ret = Double.parseDouble(ArsenalsJni.getStringValFromFile(
                    "/sys/devices/system/cpu/cpu$cpuId/cpufreq/scaling_cur_freq"))
                    / 1000.0;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getCurrentCpuFreq NumberFormatException");
        }
        return ret;
    }

    public static String getCpuUtilizationStr() {
        return ArsenalsJni.getCpuUtilizationStr();
    }

    public static double getCpuTemperature() {
        double ret = 0.0;
        try {
            ret = Double.parseDouble(ArsenalsJni.getStringValFromFile(
                    "/sys/class/thermal/thermal_zone20/temp")) / 1000.0;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getBatteryCapacity NumberFormatException");
        }
        return ret;
    }

    public static int getCurrentGpuFreq() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile("/sys/class/kgsl/kgsl-3d0/gpuclk")) / 1000000;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getCurrentGpuFreq NumberFormatException");
        }
        return ret;
    }

    public static int getGpuBusy() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile("/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage"));
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getGpuBusy NumberFormatException");
        }
        return ret;
    }

    public static boolean isCpuOnline(int cpuId) {
        boolean ret = true;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile(
                    "/sys/devices/system/cpu/cpu$cpuId/online")) != 0;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "isCpuOnline NumberFormatException");
        }
        return ret;
    }

    public static int getBatteryVoltage() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile(
                    "/sys/class/power_supply/battery/voltage_now")) / 1000;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getBatteryVoltage NumberFormatException");
        }
        return ret;
    }

    public static int getBatteryCurrent() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile(
                    "/sys/class/power_supply/battery/current_now")) / 1000;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getBatteryCurrent NumberFormatException");
        }
        return ret;
    }

    public static int getBatteryCapacity() {
        int ret = 100;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile(
                    "/sys/class/power_supply/battery/capacity"));
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getBatteryCapacity NumberFormatException");
        }
        return ret;
    }

    public static int getBatteryTemperature() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromFile(
                    "/sys/class/power_supply/battery/temp")) / 10;
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getBatteryCapacity NumberFormatException");
        }
        return ret;
    }

    public static double getCurrentFps() {
        double ret = 0.0;
        try {
            ret = Double.parseDouble(ArsenalsJni.getStringValFromCmd(
                    "cat /sys/class/drm/sde-crtc-0/measured_fps | awk '{print \\$2}'"));
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getCurrentFps NumberFormatException");
        }
        return ret;
    }

    public static int getAvailableMemory() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromCmd(
                    "cat /proc/meminfo | grep MemAvailable | awk '{print \\$2}'"));
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getCurrentFps NumberFormatException");
        }
        return ret;
    }

    public static int getTotalMemory() {
        int ret = 0;
        try {
            ret = Integer.parseInt(ArsenalsJni.getStringValFromCmd(
                    "cat /proc/meminfo | grep MemTotal | awk '{print \\$2}'"));
        } catch (NumberFormatException ex) {
            Alog.warn(TAG, "getCurrentFps NumberFormatException");
        }
        return ret;
    }

    public static boolean isThermalEnabled() {
        boolean thermal1 = !"disabled".equals(ArsenalsJni.getStringValFromFile(
                "/sys/class/thermal/thermal_zone0/mode"));
        boolean thermal2 = !"disabled".equals(ArsenalsJni.getStringValFromFile(
                "/sys/class/thermal/thermal_zone1/mode"));
        Alog.verbose(TAG, "thermal1 " + thermal1 + " thermal2 " + thermal2);
        return thermal1 || thermal2;
    }

    public static boolean setThermalEnableStatus(Boolean isEnable) {
        String str = isEnable ? "enabled" : "disabled";
        boolean isSucc1 = ArsenalsRoot.getInstance().execAsRoot("echo $str > /sys/class/thermal/thermal_zone0/mode\n");
        boolean isSucc2 = ArsenalsRoot.getInstance().execAsRoot("echo $str > /sys/class/thermal/thermal_zone1/mode\n");
        if (isSucc1 && isSucc2) {
            Alog.verbose(TAG, "setThermalEnableStatus $isEnable exec succeed");
            return true;
        }
        Alog.warn(TAG, "setThermalEnableStatus $isEnable exec failed!");
        return false;
    }
}
