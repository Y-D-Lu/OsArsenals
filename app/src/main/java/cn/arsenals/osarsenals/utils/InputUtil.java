package cn.arsenals.osarsenals.utils;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;

import cn.arsenals.aos.input.AosInputUtil;

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
}
