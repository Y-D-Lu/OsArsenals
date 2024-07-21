package cn.arsenals.osarsenals.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.arsenals.osarsenals.manager.InputManager;

public class GenshinImpactUtil {
    public static final String POINT_LAST_ICON_ON_RECENT = "330,540";
    public static final String POINT_MORE_INFO_ON_RECENT = "650,540";
    public static final String POINT_FORCE_STOP_IN_SETTINGS = "875,1080";
    public static final String POINT_SCREEN_CENTER = "1200,540";
    public static final String POINT_CONFORM_FORCE_STOP_IN_SETTINGS = "825,1380";
    public static final String POINT_GENSHIN_ON_PRIME_SCREEN = "120,1050";

    public static final int TIME_WAIT_ONE_SECOND = 1000;
    public static final int TIME_WAIT_TWO_SECOND = 2 * 1000;
    public static final int TIME_WAIT_TEN_SECOND = 10 * 1000;
    public static final int TIME_WAIT_THIRTY_SECOND = 30 * 1000;
    public static final int TIME_WAIT_ONE_MINUTE = 60 * 1000;

    public static final String GENSHIN_POINT_MAP = "300,157";
    public static final String GENSHIN_POINT_MAP_SCALE_LEFT = "300,540";
    public static final String GENSHIN_POINT_MAP_SCALE_RIGHT = "2100,540";
    public static final String GENSHIN_POINT_MAP_SELECTOR = "2213,1002";
    public static final String GENSHIN_POINT_MAP_SELECTOR_MONDSTADT = "1730,220";
    public static final String GENSHIN_POINT_MAP_SELECTOR_LIYUE = "2080,220";
    public static final String GENSHIN_POINT_MAP_SELECTOR_INAZUMA = "1730,360";
    public static final String GENSHIN_POINT_MAP_TELEPORT = "2023,987";
    public static final String GENSHIN_POINT_PLAYER_OPERATE = "1451,537";
    public static final String GENSHIN_POINT_DOMAIN_LIST_SWIPE_START = "540,805";
    public static final String GENSHIN_POINT_DOMAIN_FIGHT = "2070,1000";
    public static final String GENSHIN_POINT_DOMAIN_SELECT_TEAM = "185,1005";
    public static final String GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM = "540,375";
    public static final String GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE = "500,1000";
    public static final String GENSHIN_POINT_DOMAIN_SKIP_TIPS = "1200,240";
    public static final String GENSHIN_POINT_DOMAIN_EXIT = "160,60";
    public static final String GENSHIN_POINT_DOMAIN_EXIT_CONFORM = "1450,810";
    public static final String GENSHIN_POINT_DOMAIN_EXIT_FINAL = "1200,975";
    public static final String GENSHIN_POINT_DOMAIN_GET_BONUS = "1451,537";
    public static final String GENSHIN_POINT_DOMAIN_USE_ORIGINAL_RESIN = "1450,810";
    public static final String GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS = "950,980";
    public static final String GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1 = "2100,240";
    public static final String GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2 = "2100,360";
    public static final String GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3 = "2100,480";
    public static final String GENSHIN_POINT_PLAYER_WALK_AHEAD = "440,625";
    public static final String GENSHIN_POINT_PLAYER_WALK_LEFT = "440,825";
    public static final String GENSHIN_POINT_PLAYER_WALK_RIGHT = "441,825";
    public static final String GENSHIN_POINT_PLAYER_JUMP = "2100,700";
    public static final String GENSHIN_POINT_PLAYER_SPRINT = "2100,930";
    public static final String GENSHIN_POINT_PLAYER_ATTACK = "1920,825";
    public static final String GENSHIN_POINT_PLAYER_E = "1740,930";

    private static final String TAG = "GenshinImpactUtil";

    private static final HandlerThread handlerThread = new HandlerThread("GenshinImpactUtilHandlerThread");
    private static Handler handler;
    private static String fightLoopStr = "e,1000|sleep,600|switch,1|sleep,1000|nahida_e|sleep,600|switch,2|sleep,1000|e|sleep,1000|switch,3|sleep,1000|e|sleep,600|neuvillette_z|switch,1|sleep,600";
    private static boolean isDomainProcessing = false;
    private static void initHandler() {
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static void handleGenshinCommand(String command) {
        if (TextUtils.isEmpty(command)) {
            Alog.warn(TAG, "handleGenshinCommand command empty !" + command);
            return;
        }
        if (handler == null) {
            initHandler();
        } else {
            handler.removeCallbacksAndMessages(null);
            handlerThread.interrupt();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Thread.interrupted();
                }
            });
        }
        handleGenshinCommandInner(command);
    }

    private static void handleGenshinCommandInner(String command) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleGenshinCommandSync(command);
            }
        }, 0);
    }

    private static void handleGenshinCommandSync(String command) {
        try {
            Alog.info(TAG, "handleGenshinCommand inner " + command);
            String[] cmds = command.split("\\|");
            for (String cmd : cmds) {
                String[] arr = cmd.split(",");
                switch (arr[0]) {
                    case "a": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_ATTACK");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "z": {
                        int zTime = 500;
                        if (arr.length > 1) {
                            try {
                                zTime = Integer.parseInt(arr[1]);
                            } catch (NumberFormatException e) {
                                Alog.warn(TAG, "handleGenshinCommand z NumberFormatException!");
                            }
                        }
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_ATTACK");
                        InputUtil.injectClick(point.x, point.y, zTime);
                        Thread.sleep(zTime);
                        break;
                    }
                    case "e": {
                        int eTime = 0;
                        if (arr.length > 1) {
                            try {
                                eTime = Integer.parseInt(arr[1]);
                            } catch (NumberFormatException e) {
                                Alog.warn(TAG, "handleGenshinCommand z NumberFormatException!");
                            }
                        }
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_E");
                        InputUtil.injectClick(point.x, point.y, eTime);
                        if (eTime == 0) {
                            break;
                        }
                        Thread.sleep(eTime);
                        break;
                    }
                    case "nahida_e": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_E");
                        InputUtil.injectSwipe(point.x, point.y, point.x - 2400, point.y, 1000, 50);
                        Thread.sleep(1000);
                        break;
                    }
                    case "neuvillette_z": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_ATTACK");
                        InputUtil.injectSwipe(point.x, point.y, point.x - 20000, point.y, 8000, 50);
                        Thread.sleep(8000);
                        break;
                    }
                    case "s": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_SPRINT");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "j": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_JUMP");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "sleep": {
                        if (arr.length < 2) {
                            break;
                        }
                        int sleepTime = 0;
                        try {
                            sleepTime = Integer.parseInt(arr[1]);
                        } catch (NumberFormatException e) {
                            Alog.warn(TAG, "handleGenshinCommand inner NumberFormatException!");
                        }
                        if (sleepTime == 0) {
                            break;
                        }
                        Thread.sleep(sleepTime);
                        break;
                    }
                    case "left": {
                        int duration = 500;
                        if (arr.length > 1) {
                            try {
                                duration = Integer.parseInt(arr[1]);
                            } catch (NumberFormatException e) {
                                Alog.warn(TAG, "handleGenshinCommand left NumberFormatException!");
                            }
                        }
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_LEFT");
                        InputUtil.injectClick(point.x, point.y, duration);
                        Thread.sleep(duration);
                        break;
                    }
                    case "right": {
                        int duration = 500;
                        if (arr.length > 1) {
                            try {
                                duration = Integer.parseInt(arr[1]);
                            } catch (NumberFormatException e) {
                                Alog.warn(TAG, "handleGenshinCommand right NumberFormatException!");
                            }
                        }
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_RIGHT");
                        InputUtil.injectClick(point.x, point.y, duration);
                        Thread.sleep(duration);
                        break;
                    }
                    case "ahead": {
                        int duration = 500;
                        if (arr.length > 1) {
                            try {
                                duration = Integer.parseInt(arr[1]);
                            } catch (NumberFormatException e) {
                                Alog.warn(TAG, "handleGenshinCommand ahead NumberFormatException!");
                            }
                        }
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_AHEAD");
                        InputUtil.injectClick(point.x, point.y, duration);
                        Thread.sleep(duration);
                        break;
                    }
                    case "switch": {
                        if (arr.length < 2) {
                            Alog.warn(TAG, "handleGenshinCommand switch length < 2 " + arr.length);
                            return;
                        }
                        try {
                            int player = Integer.parseInt(arr[1]);
                            if (player == 1) {
                                Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1");
                                InputUtil.injectClick(point.x, point.y, 0);
                            } else if (player == 2) {
                                Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2");
                                InputUtil.injectClick(point.x, point.y, 0);
                            } else if (player == 3) {
                                Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3");
                                InputUtil.injectClick(point.x, point.y, 0);
                            } else {
                                Alog.warn(TAG, "handleGenshinCommand switch invalid player " + player);
                            }
                        } catch (NumberFormatException e) {
                            Alog.warn(TAG, "handleGenshinCommand switch NumberFormatException!");
                        }
                        break;
                    }
                    case "map": {
                        if (arr.length < 2) {
                            Alog.warn(TAG, "handleGenshinCommand map length < 2 " + arr.length);
                            return;
                        }
                        try {
                            int place = Integer.parseInt(arr[1]);
                            Point pointPlace = null;
                            if (place == 1) {
                                pointPlace = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_SELECTOR_MONDSTADT");
                            } else if (place == 2) {
                                pointPlace = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_SELECTOR_LIYUE");
                            } else if (place == 3) {
                                pointPlace = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_SELECTOR_INAZUMA");
                            } else {
                                Alog.warn(TAG, "handleGenshinCommand switch invalid place " + place);
                            }
                            if (pointPlace != null) {
                                Point pointMap = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP");
                                InputUtil.injectClick(pointMap.x, pointMap.y, 0);
                                Thread.sleep(1000);
                                Point pointSelector = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_SELECTOR");
                                InputUtil.injectClick(pointSelector.x, pointSelector.y, 0);
                                Thread.sleep(1000);
                                InputUtil.injectClick(pointPlace.x, pointPlace.y, 0);
                                Thread.sleep(1000);
                                Point pointScaleLeft = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_SCALE_LEFT");
                                Point pointScaleRight = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_SCALE_RIGHT");
                                Point pointScreenCenter = InputManager.getInstance().getPointFromMap("POINT_SCREEN_CENTER");
                                InputUtil.injectTwoPointSwipe(pointScaleLeft.x, pointScaleLeft.y, pointScreenCenter.x, pointScreenCenter.y,
                                    pointScaleRight.x, pointScaleRight.y, pointScreenCenter.x, pointScreenCenter.y, 2000, 50);
                                Thread.sleep(2000);
                                InputUtil.injectClick(pointSelector.x, pointSelector.y, 0);
                                Thread.sleep(1000);
                                InputUtil.injectClick(pointPlace.x, pointPlace.y, 0);
                            }
                        } catch (NumberFormatException e) {
                            Alog.warn(TAG, "handleGenshinCommand map NumberFormatException!");
                        }
                        break;
                    }
                    case "map_teleport": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_MAP_TELEPORT");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "player_operate": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_OPERATE");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "domain_enter": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_FIGHT");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(2000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_SELECT_TEAM");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_LIST_SWIPE_START");
                        InputUtil.injectSwipe(point.x, point.y, point.x, 0, 500, 50);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_FIGHT");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(10000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_SKIP_TIPS");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_SKIP_TIPS");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_SKIP_TIPS");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "domain_finish": {
                        rotateTowardsEast(new IRotateTowardsEastCb() {
                            @Override
                            public void onFinish() {
                                Alog.info(TAG, "domain_finish rotateTowardsEast onFinish!");
                            }
                        });
                        break;
                    }
                    case "domain_get_bonus": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_OPERATE");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_EXIT_CONFORM");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(10000);
                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "domain_exit": {
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_EXIT");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(1000);

                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_EXIT_CONFORM");
                        InputUtil.injectClick(point.x, point.y, 0);
                        Thread.sleep(3000);

                        point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_DOMAIN_EXIT_FINAL");
                        InputUtil.injectClick(point.x, point.y, 0);
                        break;
                    }
                    case "get_rotate": {
                        long startTime = System.currentTimeMillis();
                        getRotateAsync(new IGetRotateCb() {
                            @Override
                            public void onCallback(int rotate) {
                                long time = System.currentTimeMillis() - startTime;
                                Alog.info(TAG, "handleGenshinCommand rotate " + rotate + " time cost " + time);
                            }
                        });
                        break;
                    }
                    case "is_domain_succeed": {
                        isDomainSucceed(new IDomainSucceedCb() {
                            @Override
                            public void onCallback(boolean isSucceed) {
                                Alog.info(TAG, "handleGenshinCommand is_domain_succeed " + isSucceed);
                            }
                        });
                        break;
                    }
                    case "judge_position_and_adjust": {
                        judgePositionAndAdjust(new IJudgePositionAndAdjustCb() {
                            @Override
                            public void onFinish(boolean isSucceed) {
                                Alog.info(TAG, "handleGenshinCommand judgePositionAndAdjust " + isSucceed);
                            }
                        }, 10, 5);
                        break;
                    }
                    case "cancel": {
                        break;
                    }
                    // adb shell am broadcast -a cn.arsenals.osarsenals.EXECUTE_COMMAND --es "type" "genshin" --es "command" "fight_loop,a\|sleep,1000\|a"
                    case "fight_loop": {
                        if (arr.length < 2) {
                            Alog.info(TAG, "handleGenshinCommand fight_loop use profile");
                            handleGenshinCommand(fightLoopStr);
                            return;
                        }
                        handleGenshinCommand(command.substring("fight_loop,".length()));
                        // should return here to avoid command in fightLoopStr
                        return;
                    }
                    // adb shell am broadcast -a cn.arsenals.osarsenals.EXECUTE_COMMAND --es "type" "genshin" --es "command" "set_fight_loop,e,1000\|sleep,600\|switch,1\|sleep,1000\|nahida_e\|sleep,600\|switch,2\|sleep,1000\|e\|sleep,1000\|switch,3\|sleep,1000\|e\|sleep,600\|neuvillette_z\|switch,1\|sleep,600"
                    case "set_fight_loop": {
                        if (arr.length < 2) {
                            Alog.warn(TAG, "handleGenshinCommand set_fight_loop lenth < 2 " + arr.length);
                            return;
                        }
                        fightLoopStr = command.substring("set_fight_loop,".length());
                        // should return here to avoid command in fightLoopStr
                        return;
                    }
                    case "process_fight_domain": {
                        isDomainProcessing = true;
                        InputUtil.saveAsFile("isDomainProcessing", "isDomainProcessing");
                        processFightDomain(new IProcessFightDomainCb() {
                            @Override
                            public void onFinish(boolean isSucceed) {
                                Alog.info(TAG, "handleGenshinCommand process_fight_domain onFinish " + isSucceed);
                                isDomainProcessing = false;
                                InputUtil.saveAsFile("isDomainProcessing", isSucceed ? "succeed" : "failed");
                            }
                        });
                        break;
                    }
                    default: {
                        Alog.warn(TAG, "handleGenshinCommand invalid type " + arr[0]);
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Alog.warn(TAG, "handleGenshinCommand InterruptedException!");
        }
    }

    public interface IRotateTowardsEastCb {
        void onFinish();
    }

    private static void rotateTowardsEast(IRotateTowardsEastCb callback) {
        if (callback == null) {
            Alog.warn(TAG, "rotateTowardsEast callBack is null!");
            return;
        }
        getRotateAsync(new IGetRotateCb() {
            @Override
            public void onCallback(int rotate) {
                try {
                    if (rotate == 0) {
                        Alog.info(TAG, "rotateTowardsEast already towards east");
                        callback.onFinish();
                        return;
                    }
                    if (rotate < 33) {
                        Alog.info(TAG, "rotateTowardsEast rotate " + rotate + " move slowly!");
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_ATTACK");
                        InputUtil.injectSwipe(point.x, point.y, point.x - 10, point.y, 1000, 50);
                        Thread.sleep(1000);
                        Thread.sleep(1000);
                        rotateTowardsEast(callback);
                        return;
                    }
                    Alog.info(TAG, "rotateTowardsEast rotate " + rotate + " move rapidly");
                    Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_ATTACK");
                    InputUtil.injectSwipe(point.x, point.y, point.x - 100, point.y, 1000, 50);
                    Thread.sleep(1000);
                    Thread.sleep(1000);
                    rotateTowardsEast(callback);
                    return;
                } catch (InterruptedException e) {
                    Alog.warn(TAG, "rotateTowardsEast InterruptedException!");
                }
            }
        });
    }

    public interface IGetRotateCb {
        void onCallback(int rotate);
    }

    private static void getRotateAsync(IGetRotateCb callback) {
        if (callback == null) {
            Alog.warn(TAG, "getRotateAsync callBack is null!");
            return;
        }
        InputUtil.captureDisplayAsync(0, 0, 2400, 1080, 0, new InputUtil.ICaptureDisplayCb() {
            @Override
            public void onCaptureDisplay(Bitmap bitmap) {
                if (bitmap == null) {
                    Alog.warn(TAG, "getRotate bitmap is null!");
                    return;
                }
                Bitmap map = Bitmap.createBitmap(bitmap, 164, 21, 272, 272);
                // InputUtil.saveBitmap(map, "map");
                Bitmap arrow = Bitmap.createBitmap(map, 121, 121, 30, 28);
                // InputUtil.saveBitmap(arrow, "arrow");
                for (int x = 0; x < arrow.getWidth(); x++) {
                    for (int y = 0; y < arrow.getHeight(); y++) {
                        int color = arrow.getPixel(x, y);
                        int r = Color.red(color);
                        int g = Color.green(color);
                        int b = Color.blue(color);
                        if (r < 10 && g > 245 && b > 245) {
                            arrow.setPixel(x, y, 0xFFFFFFFF);
                        } else {
                            arrow.setPixel(x, y, 0xFF000000);
                        }
                    }
                }
//                InputUtil.saveBitmap(arrow, "debug_arrow");
                int left = getRectLeft(arrow);
                int right = getRectRight(arrow);
                int top = getRectTop(arrow);
                int bottom = getRectBottom(arrow);
                double tmp = Math.atan2((((top + bottom) / 2.0) - 13.5), (((left + right) / 2.0) - 14));
                double angle = tmp / Math.PI * 180 + 180;
                if (angle > 359) {
                    angle = 360.0 - angle;
                }
                Alog.debug(TAG, "getRotate left " + left + " right " + right + " top " + top + " bottom " + bottom + " tmp " + tmp + " angle " + angle);
                callback.onCallback((int) angle);
            }
        }, handler);
    }

    private static int getRectLeft(Bitmap arrow) {
        for (int x = 0; x < arrow.getWidth(); x++) {
            for (int y = 0; y < arrow.getHeight(); y++) {
                int color = arrow.getPixel(x, y);
                int r = Color.red(color);
                if (r == 255) {
                    return x;
                }
            }
        }
        return 0;
    }

    private static int getRectRight(Bitmap arrow) {
        for (int x = 0; x < arrow.getWidth(); x++) {
            for (int y = 0; y < arrow.getHeight(); y++) {
                int color = arrow.getPixel(arrow.getWidth() - 1 - x, y);
                int r = Color.red(color);
                if (r == 255) {
                    return arrow.getWidth() - 1 - x;
                }
            }
        }
        return 0;
    }

    private static int getRectTop(Bitmap arrow) {
        for (int y = 0; y < arrow.getHeight(); y++) {
            for (int x = 0; x < arrow.getWidth(); x++) {
                int color = arrow.getPixel(x, y);
                int r = Color.red(color);
                if (r == 255) {
                    return y;
                }
            }
        }
        return 0;
    }

    private static int getRectBottom(Bitmap arrow) {
        for (int y = 0; y < arrow.getHeight(); y++) {
            for (int x = 0; x < arrow.getWidth(); x++) {
                int color = arrow.getPixel(x, arrow.getHeight() - 1 - y);
                int r = Color.red(color);
                if (r == 255) {
                    return arrow.getHeight() - 1 - y;
                }
            }
        }
        return 0;
    }

    public interface IJudgePositionAndAdjustCb {
        void onFinish(boolean isSucceed);
    }

    private static void judgePositionAndAdjust(IJudgePositionAndAdjustCb callback, int tryCount, int moveAheadCount) {
        if (callback == null) {
            Alog.warn(TAG, "judgePositionAndAdjust callBack is null!");
            return;
        }
        if (tryCount <= 0) {
            Alog.warn(TAG, "judgePositionAndAdjust tryCount <= 0, failed!");
            callback.onFinish(false);
        }
        if (moveAheadCount <= 0) {
            Alog.warn(TAG, "judgePositionAndAdjust moveAheadCount <= 0, failed!");
            callback.onFinish(false);
        }
        InputUtil.captureDisplayAsync(0, 0, 2400, 1080, 0, new InputUtil.ICaptureDisplayCb() {
            @Override
            public void onCaptureDisplay(Bitmap bitmap) {
                if (bitmap == null) {
                    Alog.warn(TAG, "judgePositionAndAdjust bitmap is null!");
                    return;
                }
                for (int x = 0; x < bitmap.getWidth(); x++) {
                    for (int y = 0; y < bitmap.getHeight(); y++) {
                        int color = bitmap.getPixel(x, y);
                        int r = Color.red(color);
                        int g = Color.green(color);
                        int b = Color.blue(color);
                        if (r > 127 && g > 127 && b > 127) {
                            bitmap.setPixel(x, y, 0xFFFFFFFF);
                        } else {
                            bitmap.setPixel(x, y, 0xFF000000);
                        }
                    }
                }

                ArrayList<Integer> validCenterList = getValidCenterList(bitmap);
                try {
                    if (validCenterList.size() == 0) {
                        Alog.warn(TAG, "judgePositionAndAdjust invalid valid_center_list! try move ahead!");
                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_AHEAD");
                        InputUtil.injectClick(point.x, point.y, 500);
                        Thread.sleep(500);
                        Thread.sleep(1000);
                        judgePositionAndAdjust(callback, tryCount - 1, moveAheadCount - 1);
                    } else if (validCenterList.size() == 1) {
                        if (validCenterList.get(0) < 1200) {
                            Alog.warn(TAG, "judgePositionAndAdjust stage is at left side? move to left");
                            Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_LEFT");
                            InputUtil.injectClick(point.x, point.y, 500);
                            Thread.sleep(500);
                            Thread.sleep(1000);
                            judgePositionAndAdjust(callback, tryCount - 1, moveAheadCount);
                        } else {
                            Alog.warn(TAG, "judgePositionAndAdjust stage is at right side? move to right");
                            Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_RIGHT");
                            InputUtil.injectClick(point.x, point.y, 500);
                            Thread.sleep(500);
                            Thread.sleep(1000);
                            judgePositionAndAdjust(callback, tryCount - 1, moveAheadCount);
                        }
                    } else {
                        int left = validCenterList.get(0);
                        int right = validCenterList.get(1);
                        if (right < 1250) {
                            Alog.info(TAG, "judgePositionAndAdjust stage is at left side, move to left");
                            Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_LEFT");
                            InputUtil.injectClick(point.x, point.y, 500);
                            Thread.sleep(500);
                            Thread.sleep(1000);
                            judgePositionAndAdjust(callback, tryCount - 1, moveAheadCount);
                        } else if (left > 1150) {
                            Alog.info(TAG, "judgePositionAndAdjust stage is at right side, move to right");
                            Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_RIGHT");
                            InputUtil.injectClick(point.x, point.y, 500);
                            Thread.sleep(500);
                            Thread.sleep(1000);
                            judgePositionAndAdjust(callback, tryCount - 1, moveAheadCount);
                        } else {
                            Alog.info(TAG, "judgePositionAndAdjust no need to move");
                            callback.onFinish(true);
                        }
                    }
                } catch (InterruptedException e) {
                    Alog.warn(TAG, "judgePositionAndAdjust InterruptedException");
                }
            }
        }, handler);
    }

    private static ArrayList<Integer> getValidCenterList(Bitmap bitmap) {
        ArrayList<Integer> centerPointList = new ArrayList<>();
        int length = 0;
        // find the stage step right
        for (int x = 0; x < 2400; x++) {
            // assume line is stage
            boolean isLineOk = true;
            for (int i = 0; i < 20; i++) {
                // if not pure white, the line is not stage
                if (bitmap.getPixel(x, 400 - i) == 0xFF000000) {
                    if (length != 0) {
                        centerPointList.add(x - length / 2);
                        length = 0;
                        isLineOk = false;
                        break;
                    }
                }
            }

            // if all line is pure white, assume is the stage
            if (isLineOk) {
                length++;
            }
        }

        ArrayList<Integer> validCenterList = new ArrayList<>();
        for (int pt : centerPointList) {
            int top = 380;
            int lengthTopLeft = 0;
            while (pt - lengthTopLeft > 0 && bitmap.getPixel(pt - lengthTopLeft, top) == 0xFFFFFFFF) {
                lengthTopLeft += 1;
            }
            int lengthTopRight = 0;
            while (pt + lengthTopRight < bitmap.getWidth() && bitmap.getPixel(pt + lengthTopRight, top) == 0xFFFFFFFF) {
                lengthTopRight += 1;
            }
            int lengthTop = lengthTopLeft + lengthTopRight;

            int bottom = 400;
            int lengthBottomLeft = 0;
            while (pt - lengthBottomLeft > 0 && bitmap.getPixel(pt - lengthBottomLeft, bottom) == 0xFFFFFFFF) {
                lengthBottomLeft += 1;
            }
            int lengthBottomRight = 0;
            while (pt + lengthBottomRight < bitmap.getWidth() && bitmap.getPixel(pt + lengthBottomRight, bottom) == 0xFFFFFFFF) {
                lengthBottomRight += 1;
            }
            int lengthBottom = lengthBottomLeft + lengthBottomRight;

            if (lengthTop > 10 && lengthTop < 50 && lengthBottom > 10 && lengthBottom < 50 && Math.abs(lengthTop - lengthBottom) < 5) {
                for (int x = 0; x < 20; x++) {
                    for (int y = 0; y < 20; y++) {
                        bitmap.setPixel(pt - 10 + x, 400 - y, 0xFFFF0000);
                    }
                }
                validCenterList.add(pt);
            }
        }

        ArrayList<Integer> resultList = new ArrayList<>();
        int currentMaxWhitePixelCount = 0;
        int currentMaxIndex = 0;
        if (validCenterList.size() > 2) {
            for (int i = 0; i < validCenterList.size() - 1; i++) {
                int center = (validCenterList.get(i) + validCenterList.get(i + 1)) / 2;
                int whitePixelCount = 0;
                for (int x = 0; x < 100; x++) {
                    for (int y = 0; y < 100; y++) {
                        if (bitmap.getPixel(center - 50 + x, 200 + y) == 0xFFFFFFFF) {
                            whitePixelCount++;
                        }
                    }
                }
                if (currentMaxWhitePixelCount < whitePixelCount) {
                    currentMaxWhitePixelCount = whitePixelCount;
                    currentMaxIndex = i;
                }
                if (whitePixelCount > 9900) {
                    break;
                }
            }
            resultList.add(currentMaxIndex);
            resultList.add(currentMaxIndex + 1);
            for (int x = 0; x < 20; x++) {
                for (int y = 0; y < 20; y++) {
                    bitmap.setPixel(resultList.get(0) - 10 + x, 400 - y, 0xFFFFFFFF);
                    bitmap.setPixel(resultList.get(1) - 10 + x, 400 - y, 0xFFFFFFFF);
                }
            }
            return resultList;
        } else {
            return validCenterList;
        }
    }

    public interface IDomainSucceedCb {
        void onCallback(boolean isSucceed);
    }

    private static void isDomainSucceed(IDomainSucceedCb callback) {
        if (callback == null) {
            Alog.warn(TAG, "isDomainSucceed callBack is null!");
            return;
        }
        InputUtil.captureDisplayAsync(0, 0, 2400, 1080, 0, new InputUtil.ICaptureDisplayCb() {
            @Override
            public void onCaptureDisplay(Bitmap bitmap) {
                if (bitmap == null) {
                    Alog.warn(TAG, "judgePositionAndAdjust bitmap is null!");
                    return;
                }
                // the center of "出"
                int color = bitmap.getPixel(1320, 926);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                if (r > 240 && g > 177 && b < 15) {
                    Alog.info(TAG, "isDomainSucceed true");
                    callback.onCallback(true);
                } else {
                    Alog.debug(TAG, "isDomainSucceed false");
                    callback.onCallback(false);
                }
            }
        }, handler);
    }

    public interface IProcessFightDomainCb {
        void onFinish(boolean isSucceed);
    }

    private static void processFightDomain(IProcessFightDomainCb callback) {
        if (callback == null) {
            Alog.warn(TAG, "processFightDomain callBack is null!");
            return;
        }
        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_OPERATE");
        InputUtil.injectClick(point.x, point.y, 0);
        fightDomainLoop(new IFightDomainLoopCb() {
            @Override
            public void onFinish(boolean isSucceed) {
                Alog.info(TAG, "processFightDomain onFinish isSucceed " + isSucceed);
                if (isSucceed) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Alog.warn(TAG, "processFightDomain onFinish InterruptedException");
                    }
                    Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3");
                    InputUtil.injectClick(point.x, point.y, 0);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Alog.warn(TAG, "processFightDomain onFinish InterruptedException");
                    }
                    rotateTowardsEast(new IRotateTowardsEastCb() {
                        @Override
                        public void onFinish() {
                            Alog.info(TAG, "rotateTowardsEast onFinish");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Alog.warn(TAG, "processFightDomain onFinish InterruptedException");
                            }
                            Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1");
                            InputUtil.injectClick(point.x, point.y, 0);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Alog.warn(TAG, "processFightDomain onFinish InterruptedException");
                            }
                            judgePositionAndAdjust(new IJudgePositionAndAdjustCb() {
                                @Override
                                public void onFinish(boolean isSucceed) {
                                    Alog.info(TAG, "judgePositionAndAdjust onFinish isSucceed " + isSucceed);
                                    if (isSucceed) {
                                        Point point = InputManager.getInstance().getPointFromMap("GENSHIN_POINT_PLAYER_WALK_AHEAD");
                                        InputUtil.injectClick(point.x, point.y, 3000);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            Alog.warn(TAG, "processFightDomain judgePositionAndAdjust onFinish InterruptedException");
                                        }
                                        callback.onFinish(true);
                                    } else {
                                        callback.onFinish(false);
                                    }
                                }
                            }, 10, 5);
                        }
                    });
                } else {
                    callback.onFinish(false);
                }
            }
        }, 10);
    }

    public interface IFightDomainLoopCb {
        void onFinish(boolean isSucceed);
    }

    private static void fightDomainLoop(IFightDomainLoopCb callback, int tryCount) {
        if (callback == null) {
            Alog.warn(TAG, "fightDomainLoop callBack is null!");
            return;
        }
        if (tryCount < 0) {
            Alog.warn(TAG, "fightDomainLoop tryCount < 0, failed!");
            callback.onFinish(false);
        }
        GenshinImpactUtil.isDomainSucceed(new GenshinImpactUtil.IDomainSucceedCb() {
            @Override
            public void onCallback(boolean isSucceed) {
                Alog.info(TAG, "handleGenshinCommand is_domain_succeed " + isSucceed);
                if (isSucceed) {
                    callback.onFinish(true);
                    return;
                }
                handleGenshinCommandInner(fightLoopStr);
                fightDomainLoop(callback, tryCount - 1);
            }
        });
    }

    public static boolean isDomainProcessing() {
        return isDomainProcessing;
    }
}
