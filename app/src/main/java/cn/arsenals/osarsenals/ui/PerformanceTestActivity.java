package cn.arsenals.osarsenals.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.arsenals.osarsenals.R;
import cn.arsenals.osarsenals.utils.Alog;

public class PerformanceTestActivity extends AppCompatActivity {
    private static final String TAG = "PerformanceTestActivity";

    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CORE_COUNT;
    private static final int MAXIMUM_POOL_SIZE = CORE_COUNT;
    private static final int KEEP_ALIVE_TIME = 60;

    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger index = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "PerformanceTestThread#" + index.getAndIncrement());
        }
    };

    private Button escapeBtn;
    private Button singleCoreBtn;
    private Button multiCoreBtn;

    private PerformanceTestThreadPoolExecutor mPerformanceTestThreadPoolExecutor = new PerformanceTestThreadPoolExecutor();

    private volatile boolean isProcessingTest = false;

    private class PerformanceTestThreadPoolExecutor extends ThreadPoolExecutor {
        public PerformanceTestThreadPoolExecutor() {
            super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, WORK_QUEUE, THREAD_FACTORY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_test);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        escapeBtn = findViewById(R.id.perf_escape_btn);
        escapeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "escapeBtn onClick");
                isProcessingTest = false;
            }
        });

        singleCoreBtn = findViewById(R.id.perf_single_core_btn);
        singleCoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "singleCoreBtn onClick");
                mPerformanceTestThreadPoolExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        performTestAlgorithm();
                    }
                });
            }
        });

        multiCoreBtn = findViewById(R.id.perf_multi_core_btn);
        multiCoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alog.info(TAG, "multiCoreBtn onClick");
                for (int i = 0; i < CORE_POOL_SIZE; i++) {
                    mPerformanceTestThreadPoolExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            performTestAlgorithm();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isProcessingTest = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPerformanceTestThreadPoolExecutor.shutdownNow();
    }

    private void performTestAlgorithm() {
        isProcessingTest = true;
        while (true) {
            if (!isProcessingTest || Thread.interrupted()) {
                return;
            }
            Math.sqrt(Math.random());
        }
    }
}