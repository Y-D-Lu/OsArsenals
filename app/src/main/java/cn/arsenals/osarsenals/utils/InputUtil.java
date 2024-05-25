package cn.arsenals.osarsenals.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;

import androidx.annotation.NonNull;

import cn.arsenals.osarsenals.jni.ArsenalsJni;

public class InputUtil {
    private static final String TAG = "InputUtil";

    public static boolean writeInputEvent(String path, int type, int code, int value) {
        return ArsenalsJni.writeInputEvent(path, type, code, value);
    }

    public static void sendKeyDown(String path, int code) {
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, code, 1);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
    }

    public static void sendKeyUp(String path, int code) {
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, code, 0);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
    }

    public static void sendFingerDown(String path, int x, int y) {
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 0);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOUCH, InputConstants.DOWN);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOOL_FINGER, InputConstants.DOWN);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, x);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, y);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
    }

    public static void sendFingerUpdate(String path,  int x, int y) {
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, x);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, y);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
    }

    public static void sendFingerUp(String path) {
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 0xffffffff);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOUCH, InputConstants.UP);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOOL_FINGER, InputConstants.UP);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
    }

    public static void sendClick(String path, int x, int y) {
        sendFingerDown(path, x, y);
        sendFingerUp(path);
    }

    public static void sendSwipeAsync(String path, int startX, int startY, int endX, int endY, int duration, int frequency) {
        if (frequency == 0) {
            Alog.error(TAG, "sendSwipeAsync wrong frequency 0, return!");
            return;
        }
        final long startTime = System.currentTimeMillis();
        final int interval = frequency > 1000 ? 1 : 1000 / frequency;
        sendFingerDown(path, startX, startY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= duration) {
                        sendFingerUp(path);
                        return;
                    }
                    if (elapsedTime % interval == 0) {
                        float progress = (float) elapsedTime / duration;
                        int currentX = (int) (startX + progress * (endX - startX));
                        int currentY = (int) (startY + progress * (endY - startY));
                        sendFingerUpdate(path, currentX, currentY);
                    }
                }
            }
        }).start();
    }

    public static void sendTwoPointSwipeAsync(
        String path, int firstStartX, int firstStartY, int firstEndX, int firstEndY,
        int secondStartX, int secondStartY, int secondEndX, int secondEndY, int duration, int frequency) {
        if (frequency == 0) {
            Alog.error(TAG, "sendTwoPointSwipeAsync wrong frequency 0, return!");
            return;
        }
        final long startTime = System.currentTimeMillis();
        final int interval = frequency > 1000 ? 1 : 1000 / frequency;
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 0);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 1);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOUCH, InputConstants.DOWN);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOOL_FINGER, InputConstants.DOWN);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, firstStartX);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, firstStartY);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);

        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 1);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 2);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, secondStartX);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, secondStartY);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= duration) {
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 1);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 0xffffffff);

                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 0);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 0xffffffff);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOUCH, InputConstants.UP);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOOL_FINGER, InputConstants.UP);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
                        return;
                    }
                    if (elapsedTime % interval == 0) {
                        float progress = (float) elapsedTime / duration;
                        int firstCurrentX = (int) (firstStartX + progress * (firstEndX - firstStartX));
                        int firstCurrentY = (int) (firstStartY + progress * (firstEndY - firstStartY));
                        int secondCurrentX = (int) (secondStartX + progress * (secondEndX - secondStartX));
                        int secondCurrentY = (int) (secondStartY + progress * (secondEndY - secondStartY));
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 0);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, firstCurrentX);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, firstCurrentY);

                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);

                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 1);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, secondCurrentX);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, secondCurrentY);

                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
                    }
                }
            }
        }).start();
    }

    public static void sendSecondarySwipeAsync(
        String path, int secondStartX, int secondStartY, int secondEndX, int secondEndY, int duration, int frequency) {
        if (frequency == 0) {
            Alog.error(TAG, "sendTwoPointSwipeAsync wrong frequency 0, return!");
            return;
        }
        final long startTime = System.currentTimeMillis();
        final int interval = frequency > 1000 ? 1 : 1000 / frequency;
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 0);
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 1);
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOUCH, InputConstants.DOWN);
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOOL_FINGER, InputConstants.DOWN);
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, firstStartX);
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, firstStartY);
//        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);

        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 1);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 2);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, secondStartX);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, secondStartY);
        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= duration) {
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 1);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 0xffffffff);

//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 0);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 0xffffffff);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOUCH, InputConstants.UP);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_KEY, InputConstants.BTN_TOOL_FINGER, InputConstants.UP);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
                        return;
                    }
                    if (elapsedTime % interval == 0) {
                        float progress = (float) elapsedTime / duration;
//                        int firstCurrentX = (int) (firstStartX + progress * (firstEndX - firstStartX));
//                        int firstCurrentY = (int) (firstStartY + progress * (firstEndY - firstStartY));
                        int secondCurrentX = (int) (secondStartX + progress * (secondEndX - secondStartX));
                        int secondCurrentY = (int) (secondStartY + progress * (secondEndY - secondStartY));
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 0);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, firstCurrentX);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, firstCurrentY);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TOUCH_MAJOR, 5);
//
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);

                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_SLOT, 1);
//                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TRACKING_ID, 2);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_X, secondCurrentX);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_POSITION_Y, secondCurrentY);
                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_ABS, InputConstants.ABS_MT_TOUCH_MAJOR, 5);

                        ArsenalsJni.writeInputEvent(path, InputConstants.EV_SYN, InputConstants.SYN_REPORT, 0);
                    }
                }
            }
        }).start();
    }
}
