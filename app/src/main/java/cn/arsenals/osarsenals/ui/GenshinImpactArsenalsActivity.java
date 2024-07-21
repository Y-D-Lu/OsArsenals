package cn.arsenals.osarsenals.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.service.OsArsenalsService;
import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.utils.GenshinImpactUtil;

public class GenshinImpactArsenalsActivity extends AppCompatActivity {
    private static final String TAG = "GenshinImpactArsenalsActivity";
    private EditText editText;
    private EditText loopEt;
    private Button runButton;
    private SharedPreferences mSharedPreferences;
    private boolean isThreadRunning = false;
    private Thread executeThread;

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
                if (isThreadRunning) {
                    isThreadRunning = false;
                    executeThread.interrupt();
                    Intent intent = new Intent(OsArsenalsService.ACTION_EXECUTE_COMMAND);
                    intent.putExtra("type", "genshin");
                    intent.putExtra("command", "cancel");
                    sendBroadcast(intent);
                    runButton.setText("RUN");
                } else {
                    isThreadRunning = true;
                    runButton.setText("STOP");
                    final String string = editText.getText().toString();
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
                            for (int i = 0; i < loop; i++) {
                                processRun(string);
                            }
                            isThreadRunning = false;
                            runButton.setText("RUN");
                        }
                    });
                    executeThread.start();
                }
            }
        });
    }

    private void processRun(String string) {
        String[] texts = string.split(System.lineSeparator());
        try {
            for (String text : texts) {
                Alog.info(TAG, "text " + text);
                String[] cmds = text.split(" ");
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
        } catch (InterruptedException e) {
            Alog.warn(TAG, "executeThread InterruptedException");
            Thread.interrupted();
        }
    }
}