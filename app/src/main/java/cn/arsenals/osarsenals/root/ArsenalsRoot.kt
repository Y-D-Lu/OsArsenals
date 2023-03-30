package cn.arsenals.osarsenals.root

import cn.arsenals.osarsenals.utils.Alog
import java.io.InputStream
import java.io.OutputStream

class ArsenalsRoot {
    companion object {
        private const val TAG = "ArsenalsRoot"

        fun getInstance() = Instance.instance
    }

    object Instance {
        val instance = ArsenalsRoot()
    }

    private var rootProcess: Process? = null
    private var rootOutPutStream: OutputStream? = null
    private var rootInputStream: InputStream? = null

    fun init() {
        Alog.info(TAG, "ArsenalsRoot init")
        if (!isRootAvailable()) {
            Alog.warn(TAG, "ArsenalsRoot !isRootAvailable!")
        }
    }

    fun uninit() {
        Alog.info(TAG, "ArsenalsRoot uninit")
        rootOutPutStream?.close()
        rootInputStream?.close()
        rootProcess?.destroy()
    }

    fun isRootAvailable(): Boolean {
        if (rootProcess != null) {
            return true
        }
        try {
            val rootProcess = Runtime.getRuntime().exec("su")
            rootOutPutStream = rootProcess.outputStream
            rootInputStream = rootProcess.inputStream
            this.rootProcess = rootProcess
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    fun execAsRoot(cmd: String): Boolean {
        if (!isRootAvailable()) {
            Alog.warn(TAG, "execAsRoot !isRootAvailable!")
            return false
        }
        rootOutPutStream?.let {
            it.write(cmd.toByteArray())
            it.flush()
            return true
        } ?: let {
            Alog.warn(TAG, "execAsRoot mRootOutPutStream is null!")
            return false
        }
    }
}
