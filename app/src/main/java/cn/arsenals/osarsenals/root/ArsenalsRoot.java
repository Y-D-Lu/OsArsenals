package cn.arsenals.osarsenals.root;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.arsenals.osarsenals.utils.Alog;

public class ArsenalsRoot {
    private static final String TAG = "ArsenalsRoot";

    private static class SingletonHolder {
        private static final ArsenalsRoot sInstance = new ArsenalsRoot();
    }

    public static ArsenalsRoot getInstance() {
        return ArsenalsRoot.SingletonHolder.sInstance;
    }

    private Process rootProcess = null;
    private OutputStream rootOutPutStream = null;
    private InputStream rootInputStream = null;

    public void init() {
        Alog.info(TAG, "ArsenalsRoot init");
        if (!isRootAvailable()) {
            Alog.warn(TAG, "ArsenalsRoot !isRootAvailable!");
        }
    }

    public void uninit() {
        Alog.info(TAG, "ArsenalsRoot uninit");
        if (rootOutPutStream != null) {
            try {
                rootOutPutStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (rootInputStream != null) {
            try {
                rootInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (rootProcess != null) {
            rootProcess.destroy();
        }
    }

    private boolean isRootAvailable() {
        if (rootProcess != null) {
            return true;
        }
        try {
            Process rootProcess = Runtime.getRuntime().exec("su");
            rootOutPutStream = rootProcess.getOutputStream();
            rootInputStream = rootProcess.getInputStream();
            this.rootProcess = rootProcess;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean execAsRoot(String cmd) {
        if (!isRootAvailable()) {
            Alog.warn(TAG, "execAsRoot !isRootAvailable!");
            return false;
        }
        if (rootOutPutStream == null) {
            Alog.warn(TAG, "execAsRoot mRootOutPutStream is null!");
            return false;
        } else {
            try {
                rootOutPutStream.write(cmd.getBytes());
                rootOutPutStream.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return true;
        }
    }
}
