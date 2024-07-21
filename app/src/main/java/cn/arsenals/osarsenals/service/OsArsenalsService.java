package cn.arsenals.osarsenals.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import java.util.Arrays;

import cn.arsenals.osarsenals.manager.InputManager;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.utils.GenshinImpactUtil;
import cn.arsenals.osarsenals.utils.InputUtil;

public class OsArsenalsService extends Service {
    private static final String TAG = "OsArsenalsService";

    private static final String ACTION_BROADCAST = "cn.arsenals.osarsenals.BROADCAST";

    // adb shell am broadcast -a cn.arsenals.osarsenals.INJECT_INPUT --es "input" "tap,0,0"
    private static final String ACTION_INJECT_INPUT = "cn.arsenals.osarsenals.INJECT_INPUT";

    // adb shell am broadcast -a cn.arsenals.osarsenals.UPDATE_POINT_MAP --es "key" "GENSHIN_POINT_MAP" --es "value" "300,157"
    private static final String ACTION_UPDATE_POINT_MAP = "cn.arsenals.osarsenals.UPDATE_POINT_MAP";

    // adb shell am broadcast -a cn.arsenals.osarsenals.EXECUTE_COMMAND --es "type" "genshin" --es "command" "a\|sleep,1000\|z,1000"
    private static final String ACTION_EXECUTE_COMMAND = "cn.arsenals.osarsenals.EXECUTE_COMMAND";

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (context == null || intent == null || intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case ACTION_BROADCAST: {
                    Alog.info(TAG, "ServiceBroadcastReceiver BROADCAST");
                    break;
                }
                case ACTION_INJECT_INPUT: {
                    Alog.info(TAG, "ServiceBroadcastReceiver INJECT_INPUT");
                    handleInjectInput(intent);
                    break;
                }
                case ACTION_UPDATE_POINT_MAP: {
                    Alog.info(TAG, "ServiceBroadcastReceiver UPDATE_POINT_MAP");
                    handleUpdatePointMap(intent);
                    break;
                }
                case ACTION_EXECUTE_COMMAND: {
                    Alog.info(TAG, "ServiceBroadcastReceiver ACTION_EXECUTE_COMMAND");
                    handleExecuteCommand(intent);
                    break;
                }
                default: {
                    Alog.warn(TAG, "ServiceBroadcastReceiver unknown " + intent.getAction());
                    break;
                }

            }
        }
    }
    public OsArsenalsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Alog.info(TAG, "OsArsenalsService onCreate");

        ServiceBroadcastReceiver serviceBroadcastReceiver = new ServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BROADCAST);
        intentFilter.addAction(ACTION_INJECT_INPUT);
        intentFilter.addAction(ACTION_UPDATE_POINT_MAP);
        intentFilter.addAction(ACTION_EXECUTE_COMMAND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(serviceBroadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(serviceBroadcastReceiver, intentFilter);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Alog.info(TAG, "OsArsenalsService onStartCommand " + intent + " flags " + flags + " startId " + startId);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "OsArsenalsService";
        String notificationName = "OsArsenalsService";
        Notification.Builder builder = new Notification.Builder(this, channelId);
        NotificationChannel channel = new NotificationChannel(
            channelId, notificationName, NotificationManager.IMPORTANCE_NONE);
        notificationManager.createNotificationChannel(channel);

        startForeground(1, builder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Alog.info(TAG, "OsArsenalsService onBind intent " + intent);
        return null;
    }

    private void handleInjectInput(Intent intent) {
        if (intent == null) {
            return;
        }
        String input = intent.getStringExtra("input");
        if (input == null) {
            return;
        }
        String[] inputs = input.split(",");
        Alog.info(TAG, "handleInjectInput " + Arrays.toString(inputs));
        switch (inputs[0]) {
            case "tap": {
                if (inputs.length < 3 || inputs.length > 4) {
                    return;
                }
                try {
                    int duration = 0;
                    if (inputs.length == 4) {
                        duration = Integer.parseInt(inputs[3]);
                    }
                    InputUtil.injectClick(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), duration);
                } catch (NumberFormatException exception) {
                    Alog.warn(TAG, "handleInjectInput NumberFormatException");
                }
                break;
            }
            case "swipe": {
                if (inputs.length < 5 || inputs.length > 7) {
                    return;
                }
                try {
                    int duration = 1000;
                    if (inputs.length >= 6) {
                        duration = Integer.parseInt(inputs[5]);
                    }
                    int frequency = 50;
                    if (inputs.length == 7) {
                        frequency = Integer.parseInt(inputs[6]);
                    }
                    InputUtil.injectSwipe(
                        Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
                        Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), duration, frequency);
                } catch (NumberFormatException exception) {
                    Alog.warn(TAG, "handleInjectInput NumberFormatException");
                }
                break;
            }
            case "pinch": {
                if (inputs.length < 9 || inputs.length > 11) {
                    return;
                }
                try {
                    int duration = 1000;
                    if (inputs.length >= 10) {
                        duration = Integer.parseInt(inputs[9]);
                    }
                    int frequency = 50;
                    if (inputs.length == 11) {
                        frequency = Integer.parseInt(inputs[10]);
                    }
                    InputUtil.injectTwoPointSwipe(
                        Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
                        Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]),
                        Integer.parseInt(inputs[5]), Integer.parseInt(inputs[6]),
                        Integer.parseInt(inputs[7]), Integer.parseInt(inputs[8]), duration, frequency);
                } catch (NumberFormatException exception) {
                    Alog.warn(TAG, "handleInjectInput NumberFormatException");
                }
                break;
            }
            case "key": {
                if (inputs.length < 2 || inputs.length > 3) {
                    return;
                }
                try {
                    int duration = 0;
                    if (inputs.length == 3) {
                        duration = Integer.parseInt(inputs[2]);
                    }
                    InputUtil.injectKey(Integer.parseInt(inputs[1]), duration);
                } catch (NumberFormatException exception) {
                    Alog.warn(TAG, "handleInjectInput NumberFormatException");
                }
                break;
            }
            default: {
                Alog.warn(TAG, "handleInjectInput unknown " + inputs[0]);
                break;
            }
        }
    }

    private void handleUpdatePointMap(Intent intent) {
        if (intent == null) {
            return;
        }
        String key = intent.getStringExtra("key");
        String value = intent.getStringExtra("value");
        if (key == null || value == null) {
            return;
        }
        String[] point = value.split(",");
        Alog.info(TAG, "handleUpdatePointMap " + key + " " + Arrays.toString(point));
        if (point.length != 2) {
            Alog.warn(TAG, "handleUpdatePointMap length not 2 " + point.length);
            return;
        }
        try {
            int posX = Integer.parseInt(point[0]);
            int posY = Integer.parseInt(point[1]);
            InputManager.getInstance().updatePointMap(key, posX, posY);
        } catch (NumberFormatException e) {
            Alog.warn(TAG, "handleUpdatePointMap NumberFormatException");
        }
    }

    private void handleExecuteCommand(Intent intent) {
        if (intent == null) {
            return;
        }
        String type = intent.getStringExtra("type");
        String command = intent.getStringExtra("command");
        if (type == null || command == null) {
            return;
        }
        Alog.info(TAG, "handleExecuteCommand " + type + " " + command);
        switch (type) {
            case "genshin": {
                GenshinImpactUtil.handleGenshinCommand(command);
                break;
            }
            default: {
                Alog.warn(TAG, "handleExecuteCommand unknown " + type);
                break;
            }
        }
    }
}