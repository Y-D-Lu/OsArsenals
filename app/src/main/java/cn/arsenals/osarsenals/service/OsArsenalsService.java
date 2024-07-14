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

import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.utils.InputUtil;

public class OsArsenalsService extends Service {
    private static final String TAG = "OsArsenalsService";

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (context == null || intent == null || intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case "cn.arsenals.osarsenals.BROADCAST": {
                    Alog.info(TAG, "ServiceBroadcastReceiver BROADCAST");
                    break;
                }
                case "cn.arsenals.osarsenals.INJECT_INPUT": {
                    Alog.info(TAG, "ServiceBroadcastReceiver INJECT_INPUT");
                    handleInjectInput(intent);
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
        intentFilter.addAction("cn.arsenals.osarsenals.BROADCAST");
        intentFilter.addAction("cn.arsenals.osarsenals.INJECT_INPUT");
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
}