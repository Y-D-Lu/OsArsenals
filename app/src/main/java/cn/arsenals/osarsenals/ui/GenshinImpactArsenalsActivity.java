package cn.arsenals.osarsenals.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.manager.GenshinImpactArsenalsViewManager;
import cn.arsenals.osarsenals.service.OsArsenalsService;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.utils.GenshinImpactUtil;

public class GenshinImpactArsenalsActivity extends AppCompatActivity {
    private static final String TAG = "GenshinImpactArsenalsActivity";
    private EditText editText;
    private EditText loopEt;
    private Button runButton;
    private SharedPreferences mSharedPreferences;
    private boolean isViewShown = false;
    private boolean isThreadRunning = false;
    private Thread executeThread;
    private Button presetsButton;
    private boolean isPresetsShow = false;
    private ScrollView scrollView;
    private Button liteClearPoolAndMountainCavernButton;
    private Button forsakenRiftButton;
    private Button paleForgottenGloryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genshin_impact_arsenals);

        mSharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);

        editText = findViewById(R.id.genshin_et);
        String defaultString = mSharedPreferences.getString("editText", "");
        editText.setText(defaultString);
        loopEt = findViewById(R.id.genshin_loop_et);
        runButton = findViewById(R.id.genshin_run_btn);
        isThreadRunning = false;
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isViewShown) {
                    isViewShown = false;
                    isThreadRunning = false;
                    if (executeThread != null) {
                        executeThread.interrupt();
                    }
                    Intent intent = new Intent(OsArsenalsService.ACTION_EXECUTE_COMMAND);
                    intent.putExtra("type", "genshin");
                    intent.putExtra("command", "cancel");
                    sendBroadcast(intent);
                    runButton.setText("RUN");
                    GenshinImpactArsenalsViewManager.getInstance().removeView();
                } else {
                    isViewShown = true;
                    runButton.setText("STOP");
                    final String string = editText.getText().toString();
                    if (TextUtils.isEmpty(string)) {
                        return;
                    }
                    GenshinImpactArsenalsViewManager.getInstance().addView();
                    GenshinImpactArsenalsViewManager.getInstance().updateTextView("START", "");
                    GenshinImpactArsenalsViewManager.getInstance().registerViewCallback(new GenshinImpactArsenalsViewManager.GenshinInpactArsenalsViewCallback() {
                        @Override
                        public void onClick() {
                            Alog.info(TAG, "GenshinImpactArsenalsView onClick isThreadRunning " + isThreadRunning);
                            if (isThreadRunning) {
                                isThreadRunning = false;
                                executeThread.interrupt();
                                Intent intent = new Intent(OsArsenalsService.ACTION_EXECUTE_COMMAND);
                                intent.putExtra("type", "genshin");
                                intent.putExtra("command", "cancel");
                                sendBroadcast(intent);
                                GenshinImpactArsenalsViewManager.getInstance().updateTextView("STOPPED", "");
                            } else {
                                isThreadRunning = true;
                                GenshinImpactArsenalsViewManager.getInstance().updateTextView("RUNNING", "");

                                executeThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alog.info(TAG, "runButton onClick " + string);
                                        mSharedPreferences.edit().putString("editText", string).apply();
                                        int loop = 1;
                                        try {
                                            loop = Integer.parseInt(loopEt.getText().toString());
                                        } catch (NumberFormatException e) {
                                            Alog.warn(TAG, "NumberFormatException");
                                        }
                                        if (loop < 1) {
                                            loop = 1;
                                        }
                                        try {
                                            for (int i = 0; i < loop; i++) {
                                                processRun(string);
                                            }
                                        } catch (InterruptedException e) {
                                            Alog.warn(TAG, "executeThread InterruptedException");
                                            Thread.interrupted();
                                        }
                                        isThreadRunning = false;
                                        GenshinImpactArsenalsViewManager.getInstance().updateTextView("END", "");
                                    }
                                });
                                executeThread.start();
                            }
                        }
                    });
                }
            }
        });
        presetsButton = findViewById(R.id.genshin_presets_btn);
        presetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPresetsShow) {
                    isPresetsShow = false;
                    editText.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                } else {
                    isPresetsShow = true;
                    scrollView.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                }
            }
        });
        scrollView = findViewById(R.id.genshin_scroll);
        liteClearPoolAndMountainCavernButton = findViewById(R.id.btn_lite_clear_pool_and_mountain_cavern);
        liteClearPoolAndMountainCavernButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(
                    "sleep 5000" + System.lineSeparator() +
                    "input tap,1990,60" + System.lineSeparator() +
                    "sleep 3000" + System.lineSeparator() +
                    "input tap,380,420" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input tap,700,235" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input tap,1150,140" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input tap,1060,280" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input swipe,1200,540,1200,500,500,50" + System.lineSeparator() +
                    "sleep 2000" + System.lineSeparator() +
                    "input tap,1936,935" + System.lineSeparator() +
                    "sleep 3000" + System.lineSeparator() +
                    "command genshin map_teleport" + System.lineSeparator() +
                    "sleep 10000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin player_operate" + System.lineSeparator() +
                    "sleep 10000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "input tap,540,500" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin domain_enter" + System.lineSeparator() +
                    "sleep 19000" + System.lineSeparator() +
                    "command genshin ahead,2400" + System.lineSeparator() +
                    "sleep 3000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin player_operate" + System.lineSeparator() +
                    "sleep 100" + System.lineSeparator() +
                    "command genshin back,1000" + System.lineSeparator() +
                    "sleep 1100" + System.lineSeparator() +
                    "command genshin ahead,500" + System.lineSeparator() +
                    "sleep 510" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin process_fight_domain_lite" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "wait_domain" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin domain_get_bonus" + System.lineSeparator() +
                    "#command genshin domain_exit" + System.lineSeparator() +
                    "sleep 25000" + System.lineSeparator());
            }
        });
        forsakenRiftButton = findViewById(R.id.btn_forsaken_rift);
        forsakenRiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(
                    "sleep 5000" + System.lineSeparator() +
                    "command genshin map,1" + System.lineSeparator() +
                    "sleep 7000" + System.lineSeparator() +
                    "input tap,1210,770" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin map_teleport" + System.lineSeparator() +
                    "sleep 10000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin player_operate" + System.lineSeparator() +
                    "sleep 10000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "input tap,540,630" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin domain_enter" + System.lineSeparator() +
                    "sleep 19000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin process_fight_domain" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "wait_domain" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin ahead,5000" + System.lineSeparator() +
                    "sleep 5000" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin domain_get_bonus" + System.lineSeparator() +
                    "#command genshin domain_exit" + System.lineSeparator() +
                    "sleep 25000" + System.lineSeparator());
            }
        });
        paleForgottenGloryButton = findViewById(R.id.btn_pale_forgotten_glory);
        paleForgottenGloryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(
                    "sleep 5000" + System.lineSeparator() +
                    "input tap,1990,60" + System.lineSeparator() +
                    "sleep 3000" + System.lineSeparator() +
                    "input tap,380,420" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input tap,700,235" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input tap,1150,140" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input tap,1060,420" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "input swipe,1200,540,1200,500,500,50" + System.lineSeparator() +
                    "sleep 2000" + System.lineSeparator() +
                    "input tap,1936,935" + System.lineSeparator() +
                    "sleep 3000" + System.lineSeparator() +
                    "command genshin map_teleport" + System.lineSeparator() +
                    "sleep 10000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin player_operate" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin player_operate" + System.lineSeparator() +
                    "sleep 10000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "input tap,540,630" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin domain_enter" + System.lineSeparator() +
                    "sleep 19000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin ahead" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin process_fight_domain" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "wait_domain" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin ahead,5000" + System.lineSeparator() +
                    "sleep 5000" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "command genshin s" + System.lineSeparator() +
                    "sleep 1000" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "command genshin domain_get_bonus" + System.lineSeparator() +
                    "#command genshin domain_exit" + System.lineSeparator() +
                    "sleep 25000" + System.lineSeparator());
            }
        });
    }

    private void processRun(String string) throws InterruptedException {
        String[] texts = string.split(System.lineSeparator());
        for (String text : texts) {
            Alog.info(TAG, "text " + text);
            if (TextUtils.isEmpty(text)) {
                continue;
            }
            String[] cmds = text.split(" ");
            if (cmds.length < 1) {
                continue;
            }
            if (!TextUtils.equals(cmds[0], "sleep")) {
                GenshinImpactArsenalsViewManager.getInstance().updateTextView("RUNNING", text);
            }
            switch (cmds[0]) {
                case "sleep": {
                    if (cmds.length != 2) {
                        return;
                    }
                    try {
                        int time = Integer.parseInt(cmds[1]);
                        Thread.sleep(time);
                    } catch (NumberFormatException e) {
                        Alog.warn(TAG, "NumberFormatException");
                    }
                    break;
                }
                case "input": {
                    if (cmds.length != 2) {
                        return;
                    }
                    String input = cmds[1];
                    Intent intent = new Intent(OsArsenalsService.ACTION_INJECT_INPUT);
                    intent.putExtra("input", input);
                    sendBroadcast(intent);
                    break;
                }
                case "command": {
                    if (cmds.length != 3) {
                        return;
                    }
                    String type = cmds[1];
                    String command = cmds[2];
                    Intent intent = new Intent(OsArsenalsService.ACTION_EXECUTE_COMMAND);
                    intent.putExtra("type", type);
                    intent.putExtra("command", command);
                    sendBroadcast(intent);
                    break;
                }
                case "update_point_map": {
                    if (cmds.length != 3) {
                        return;
                    }
                    String key = cmds[1];
                    String value = cmds[2];
                    Intent intent = new Intent(OsArsenalsService.ACTION_UPDATE_POINT_MAP);
                    intent.putExtra("key", key);
                    intent.putExtra("value", value);
                    sendBroadcast(intent);
                    break;
                }
                case "wait_domain": {
                    while (true) {
                        if (!GenshinImpactUtil.isDomainProcessing()) {
                            break;
                        }
                        Thread.sleep(1000);
                    }
                    break;
                }
                default:
                    Alog.warn(TAG, "unknown " + cmds[0]);
                    break;
            }
        }
    }
}