package cn.arsenals.osarsenals.jni;

public class ArsenalsJni {
    static {
        System.loadLibrary("arsenalslib");
    }

    public static native String stringFromJNI(String input);

    public static native String getStringValFromFile(String path);

    public static native String getStringValFromCmd(String cmd);

    public static native String getCpuUtilizationStr();

    public static native String getAioStatus();
}
