package cn.arsneals.osarsenals.jni

class ArsenalsJni {
    /**
     * A native method that is implemented by the 'arsenalslib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(input: String): String

    external fun getStringValFromFile(path: String): String

    external fun getStringValFromCmd(cmd: String): String

    external fun getCpuUtilizationStr(): String

    external fun getAioStatus(): String

    companion object {
        // Used to load the 'arsenalslib' library on application startup.
        init {
            System.loadLibrary("arsenalslib")
        }
    }
}