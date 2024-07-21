package cn.arsenals.osarsenals.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.IWindowManager;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import cn.arsenals.aos.input.AosInputUtil;
import cn.arsenals.osarsenals.OsApplication;

public class InputUtil {
    private static final String TAG = "InputUtil";

    private static int sTouchScreenDeviceId = -1;
    private static int sTouchScreenSource = -1;

    public static int getTouchScreenSource() {
        if (sTouchScreenSource != -1) {
            return sTouchScreenSource;
        }
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (device == null) {
                continue;
            }
            int source = device.getSources();
            if ((source & InputDevice.SOURCE_TOUCHSCREEN) == 0) {
                continue;
            }
            Alog.info(TAG, "getTouchScreenSource find " + device);
            sTouchScreenSource = source;
            return sTouchScreenSource;
        }
        return -1;
    }

    public static int getTouchScreenDeviceId() {
        if (sTouchScreenDeviceId != -1) {
            return sTouchScreenDeviceId;
        }
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (device == null) {
                continue;
            }
            int source = device.getSources();
            if ((source & InputDevice.SOURCE_TOUCHSCREEN) == 0) {
                continue;
            }
            Alog.info(TAG, "getTouchScreenDeviceId find " + device);
            sTouchScreenDeviceId = device.getId();
            return sTouchScreenDeviceId;
        }
        return -1;
    }

    public static void injectTouchEvent(int action, float x, float y) {
        int pointerCount = 1;
        int metaState = 0;
        int buttonState = 0;
        float xPrecision = 0;
        float yPrecision = 0;
        int deviceId = getTouchScreenDeviceId();
        int edgeFlags = 0;
        int source = getTouchScreenSource();
        int flags = 0;
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerCoords[i] = new MotionEvent.PointerCoords();
            pointerCoords[i].x = x;
            pointerCoords[i].y = y;
            pointerCoords[i].pressure = 1.0f;
            pointerCoords[i].size = 0.001f;
            pointerCoords[i].touchMajor = 1.0f;
            pointerCoords[i].touchMinor = 1.0f;
            pointerCoords[i].toolMajor = 1.0f;
            pointerCoords[i].toolMinor = 1.0f;
            pointerCoords[i].orientation = 0;
        }

        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerProperties[i] = new MotionEvent.PointerProperties();
            pointerProperties[i].toolType = MotionEvent.TOOL_TYPE_FINGER;
            pointerProperties[i].id = i;
        }
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action,
            pointerCount, pointerProperties, pointerCoords, metaState, buttonState,
            xPrecision, yPrecision, deviceId, edgeFlags, source, flags);
        Alog.debug(TAG, "injectTouchEvent " + event);
        AosInputUtil.injectInputEvent(event, AosInputUtil.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    public static void injectTouchEvent(int action, float[] x, float[] y) {
        if (x.length != y.length) {
            Alog.warn(TAG, "injectTouchEvent x.length " + x.length + " y.length " + y.length + ", return!");
            return;
        }
        int pointerCount = x.length;
        int metaState = 0;
        int buttonState = 0;
        float xPrecision = 0;
        float yPrecision = 0;
        int deviceId = getTouchScreenDeviceId();
        int edgeFlags = 0;
        int source = getTouchScreenSource();
        int flags = 0;
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerCoords[i] = new MotionEvent.PointerCoords();
            pointerCoords[i].x = x[i];
            pointerCoords[i].y = y[i];
            pointerCoords[i].pressure = 1.0f;
            pointerCoords[i].size = 0.001f;
            pointerCoords[i].touchMajor = 1.0f;
            pointerCoords[i].touchMinor = 1.0f;
            pointerCoords[i].toolMajor = 1.0f;
            pointerCoords[i].toolMinor = 1.0f;
            pointerCoords[i].orientation = 0;
        }

        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            pointerProperties[i] = new MotionEvent.PointerProperties();
            pointerProperties[i].toolType = MotionEvent.TOOL_TYPE_FINGER;
            pointerProperties[i].id = i;
        }
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action,
            pointerCount, pointerProperties, pointerCoords, metaState, buttonState,
            xPrecision, yPrecision, deviceId, edgeFlags, source, flags);
        Alog.debug(TAG, "injectTouchEvent " + event);
        AosInputUtil.injectInputEvent(event, AosInputUtil.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    public static void injectKeyEvent(int action, int keyCode) {
        KeyEvent event = new KeyEvent(
            /* downTime= */ 0, /* eventTime= */ 0,
            /* action= */ action, /* code= */keyCode, /* repeat= */ 0,
            /* metaState= */ 0, /* deviceId= */ KeyCharacterMap.VIRTUAL_KEYBOARD,
            /* scancode= */ 0, /* flags= */ 0, /* source= */ 0);
        Alog.debug(TAG, "injectKeyEvent " + event);
        AosInputUtil.injectInputEvent(event, AosInputUtil.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    public static void injectClick(float x, float y) {
        injectClick(x, y, 0);
    }

    public static void injectClick(float x, float y, int duration) {
        if (duration <= 0) {
            injectTouchEvent(MotionEvent.ACTION_DOWN, x, y);
            injectTouchEvent(MotionEvent.ACTION_UP, x, y);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                injectTouchEvent(MotionEvent.ACTION_DOWN, x, y);
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    Alog.warn(TAG, "injectClick sleep InterruptedException");
                }
                injectTouchEvent(MotionEvent.ACTION_UP, x, y);
            }
        }).start();
    }

    public static void injectSwipe(float startX, float startY, float endX, float endY, int duration, int frequency) {
        if (frequency == 0) {
            Alog.error(TAG, "injectSwipe wrong frequency 0, return!");
            return;
        }
        final long startTime = System.currentTimeMillis();
        final int interval = frequency > 1000 ? 1 : 1000 / frequency;
        injectTouchEvent(MotionEvent.ACTION_DOWN, startX, startY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= duration) {
                        injectTouchEvent(MotionEvent.ACTION_UP, endX, endY);
                        return;
                    }
                    if (elapsedTime % interval == 0) {
                        float progress = (float) elapsedTime / duration;
                        int currentX = (int) (startX + progress * (endX - startX));
                        int currentY = (int) (startY + progress * (endY - startY));
                        injectTouchEvent(MotionEvent.ACTION_MOVE, currentX, currentY);
                    }
                }
            }
        }).start();
    }

    public static void injectTwoPointSwipe(float firstStartX, float firstStartY, float firstEndX, float firstEndY,
        float secondStartX, float secondStartY, float secondEndX, float secondEndY, int duration, int frequency) {
        if (frequency == 0) {
            Alog.error(TAG, "injectTwoPointSwipe wrong frequency 0, return!");
            return;
        }
        final long startTime = System.currentTimeMillis();
        final int interval = frequency > 1000 ? 1 : 1000 / frequency;
        injectTouchEvent(MotionEvent.ACTION_DOWN, firstStartX, firstStartY);
        injectTouchEvent(MotionEvent.ACTION_POINTER_DOWN | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            new float[]{firstStartX, secondStartX}, new float[]{firstStartY, secondStartY});
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= duration) {
                        injectTouchEvent(MotionEvent.ACTION_POINTER_UP | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                            new float[]{firstEndX, secondEndX}, new float[]{firstEndY, secondEndY});
                        injectTouchEvent(MotionEvent.ACTION_UP, firstEndX, firstEndY);
                        return;
                    }
                    if (elapsedTime % interval == 0) {
                        float progress = (float) elapsedTime / duration;
                        int firstCurrentX = (int) (firstStartX + progress * (firstEndX - firstStartX));
                        int firstCurrentY = (int) (firstStartY + progress * (firstEndY - firstStartY));
                        int secondCurrentX = (int) (secondStartX + progress * (secondEndX - secondStartX));
                        int secondCurrentY = (int) (secondStartY + progress * (secondEndY - secondStartY));

                        injectTouchEvent(MotionEvent.ACTION_MOVE,
                            new float[]{firstCurrentX, secondCurrentX}, new float[]{firstCurrentY, secondCurrentY});
                    }
                }
            }
        }).start();
    }

    public static void injectKey(int keyCode, int duration) {
        if (duration <= 0) {
            injectKeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            injectKeyEvent(KeyEvent.ACTION_UP, keyCode);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                injectKeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    Alog.warn(TAG, "injectKey sleep InterruptedException");
                }
                injectKeyEvent(KeyEvent.ACTION_UP, keyCode);
            }
        }).start();
    }

    /**
     * CALL IN MAIN THREAD
     * captureDisplay
     *
     * @param left      capture rect left
     * @param top       capture rect top
     * @param right     capture rect right
     * @param bottom    capture rect bottom
     * @param displayId capture displayId
     * @return bitmap as ARGB_8888
     */
    public static Bitmap captureDisplay(int left, int top, int right, int bottom, int displayId) {
        Bitmap bitmap = null;
        Rect sourceCrop = new Rect(left, top, right, bottom);
        try {
            IWindowManager windowManager = IWindowManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.WINDOW_SERVICE));
            Class<?> screenCaptureClass = Class.forName("android.window.ScreenCapture");
            Class<?> captureArgsClass = Class.forName("android.window.ScreenCapture$CaptureArgs");
            Class<?> captureArgsBuilderClass = Class.forName("android.window.ScreenCapture$CaptureArgs$Builder");
            Class<?> screenCaptureListenerClass = Class.forName("android.window.ScreenCapture$ScreenCaptureListener");
            Class<?> synchronousScreenCaptureListenerClass = Class.forName("android.window.ScreenCapture$SynchronousScreenCaptureListener");
            Class<?> screenshotHardwareBufferClass = Class.forName("android.window.ScreenCapture$ScreenshotHardwareBuffer");
            Method setSourceCropMethod = captureArgsBuilderClass.getDeclaredMethod("setSourceCrop", Rect.class);
            Object captureArgsBuilder = captureArgsBuilderClass.newInstance();
            setSourceCropMethod.invoke(captureArgsBuilder, sourceCrop);
            Method buildMethod = captureArgsBuilderClass.getDeclaredMethod("build");
            Object captureArgs = buildMethod.invoke(captureArgsBuilder);
            Method createSyncCaptureListenerMethod = screenCaptureClass.getMethod("createSyncCaptureListener");
            Object syncScreenCapture = createSyncCaptureListenerMethod.invoke(null);
            Method captureDisplayMethod = windowManager.getClass().getMethod("captureDisplay", int.class, captureArgsClass, screenCaptureListenerClass);
            captureDisplayMethod.invoke(windowManager, displayId, captureArgs, syncScreenCapture);
            Method getBufferMethod = synchronousScreenCaptureListenerClass.getMethod("getBuffer");
            Object buffer = getBufferMethod.invoke(syncScreenCapture);
            if (buffer == null) {
                Alog.warn(TAG, "captureDisplayAsync buffer is null!");
                return bitmap;
            }
            Method asBitmapMethod = screenshotHardwareBufferClass.getMethod("asBitmap");
            bitmap = (Bitmap) asBitmapMethod.invoke(buffer);
            if (bitmap == null) {
                Alog.warn(TAG, "captureDisplayAsync bitmap is null!");
                return bitmap;
            }
            return bitmap.copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception e) {
            Alog.warn(TAG, "captureDisplay Exception " + e);
        }
        return bitmap;
    }

    public interface ICaptureDisplayCb {
        void onCaptureDisplay(Bitmap bitmap);
    }

    /**
     * captureDisplayAsync
     *
     * @param left      capture rect left
     * @param top       capture rect top
     * @param right     capture rect right
     * @param bottom    capture rect bottom
     * @param displayId capture displayId
     * @param callback capture callback
     */
    public static void captureDisplayAsync(int left, int top, int right, int bottom, int displayId, final ICaptureDisplayCb callback, Handler originalHandler) {
        if (callback == null) {
            Alog.warn(TAG, "captureDisplayAsync callBack is null!");
            return;
        }
        new Handler(OsApplication.application.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                Rect sourceCrop = new Rect(left, top, right, bottom);
                try {
                    IWindowManager windowManager = IWindowManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.WINDOW_SERVICE));
                    Class<?> screenCaptureClass = Class.forName("android.window.ScreenCapture");
                    Class<?> captureArgsClass = Class.forName("android.window.ScreenCapture$CaptureArgs");
                    Class<?> captureArgsBuilderClass = Class.forName("android.window.ScreenCapture$CaptureArgs$Builder");
                    Class<?> screenCaptureListenerClass = Class.forName("android.window.ScreenCapture$ScreenCaptureListener");
                    Class<?> synchronousScreenCaptureListenerClass = Class.forName("android.window.ScreenCapture$SynchronousScreenCaptureListener");
                    Class<?> screenshotHardwareBufferClass = Class.forName("android.window.ScreenCapture$ScreenshotHardwareBuffer");
                    Method setSourceCropMethod = captureArgsBuilderClass.getDeclaredMethod("setSourceCrop", Rect.class);
                    Object captureArgsBuilder = captureArgsBuilderClass.newInstance();
                    setSourceCropMethod.invoke(captureArgsBuilder, sourceCrop);
                    Method buildMethod = captureArgsBuilderClass.getDeclaredMethod("build");
                    Object captureArgs = buildMethod.invoke(captureArgsBuilder);
                    Method createSyncCaptureListenerMethod = screenCaptureClass.getMethod("createSyncCaptureListener");
                    Object syncScreenCapture = createSyncCaptureListenerMethod.invoke(null);
                    Method captureDisplayMethod = windowManager.getClass().getMethod("captureDisplay", int.class, captureArgsClass, screenCaptureListenerClass);
                    captureDisplayMethod.invoke(windowManager, displayId, captureArgs, syncScreenCapture);
                    Method getBufferMethod = synchronousScreenCaptureListenerClass.getMethod("getBuffer");
                    Object buffer = getBufferMethod.invoke(syncScreenCapture);
                    if (buffer == null) {
                        Alog.warn(TAG, "captureDisplayAsync buffer is null!");
                        originalHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCaptureDisplay(null);
                            }
                        });
                        return;
                    }
                    Method asBitmapMethod = screenshotHardwareBufferClass.getMethod("asBitmap");
                    bitmap = (Bitmap) asBitmapMethod.invoke(buffer);
                    if (bitmap == null) {
                        Alog.warn(TAG, "captureDisplayAsync bitmap is null!");
                        originalHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCaptureDisplay(null);
                            }
                        });
                        return;
                    }
                    final Bitmap finalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    originalHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCaptureDisplay(finalBitmap);
                        }
                    });
                    return;
                } catch (Exception e) {
                    Alog.warn(TAG, "captureDisplay Exception " + e);
                }
                originalHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCaptureDisplay(null);
                    }
                });
            }
        });
    }

    public static void saveBitmap(Bitmap bitmap, String fileName) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/OsArsenals/" + fileName + ".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            Alog.warn(TAG, "saveBitmap IOException");
        }
    }

    public static void saveAsFile(String fileName, String content) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/OsArsenals/" + fileName + ".txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Alog.warn(TAG, "saveAsFile IOException");
        }
    }
}
