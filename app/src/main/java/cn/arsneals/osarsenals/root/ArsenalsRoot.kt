package cn.arsneals.osarsenals.root

import cn.arsneals.osarsenals.utils.Alog
import java.io.*

class ArsenalsRoot {
    private val TAG = "ArsenalsRoot"

    private var mRootProcess: Process? = null
    private var mRootOutPutStream: OutputStream? = null
    private var mRootInputStream: InputStream? = null

    companion object {
        fun getInstance() = Instance.sInstance
    }

    object Instance {
        val sInstance = ArsenalsRoot()
    }

    fun init() {
        Alog.info(TAG, "ArsenalsRoot init")
        if (!isRootAvailable()) {
            Alog.warn(TAG, "ArsenalsRoot !isRootAvailable!")
        }
    }

    fun uninit() {
        Alog.info(TAG, "ArsenalsRoot uninit")
        mRootOutPutStream?.close()
        mRootInputStream?.close()
        mRootProcess?.destroy()
    }

    fun isRootAvailable(): Boolean {
        if (mRootProcess != null) {
            return true
        }
        try {
            val rootProcess = Runtime.getRuntime().exec("su")
            mRootOutPutStream = rootProcess.outputStream
            mRootInputStream = rootProcess.inputStream
            mRootProcess = rootProcess
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
        mRootOutPutStream?.let {
            it.write(cmd.toByteArray())
            it.flush()
            return true
        } ?: let {
            Alog.warn(TAG, "execAsRoot mRootOutPutStream is null!")
            return false
        }
    }
}
