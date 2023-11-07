package com.comp90018.uninooks.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.background_app.BackgroundAppService;
import com.comp90018.uninooks.views.TimerView;
import com.comp90018.uninooks.worker.FocusModeWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Vibrator;

import java.util.concurrent.TimeUnit;

public class FocusModeTimerActivity extends AppCompatActivity {

    private int pomodoroTimer;

    private int shortPauseTimer;

    private int longPauseTimer;

    public static boolean isCurrentlyOnApp = false;

    private int timer_length = 60;

    private int seconds = 60;
    private Vibrator v;

    // Setup Timer Buttons
    private Button pomodoroButton;
    private Button shortPauseButton;
    private Button longPauseButton;

    // Timer Views
    private TimerView mTimerView;
    private TextView timerText;

    // Timer Related Buttons
    private Button timerStartButton;
    private Button timerPauseButton;
    private Button timerResetButton;
    private Button settingsButton;
    private BottomNavigationView bottomNav;

    public static boolean isRunning = false;

    private boolean isPaused = false;

    private boolean isPomodoro = false;
    private boolean isShortPause = false;
    private boolean isLongPause = false;

    SharedPreferences.Editor editor;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    handler.postDelayed(timerRunnable, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize UI components and set up timer controls
        mTimerView = findViewById(R.id.timer);
        timerText = findViewById(R.id.timerTextView);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.focusNav);

        SharedPreferences sharedPreferences = getSharedPreferences("uninooks", MODE_PRIVATE);
        pomodoroTimer = sharedPreferences.getInt("pomodoroTimer", 1500);
        shortPauseTimer = sharedPreferences.getInt("shortPauseTimer", 300);
        longPauseTimer = sharedPreferences.getInt("longPauseTimer", 900);

        editor = sharedPreferences.edit();

        // Change to which setup for the timer
        pomodoroButton = findViewById(R.id.btn_pomodoro);
        pomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                seconds = pomodoroTimer;
                isPomodoro = true;
                isShortPause = false;
                isLongPause = false;
                updateTimerText();
            }
        });

        shortPauseButton = findViewById(R.id.btn_short_pause);
        shortPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                seconds = shortPauseTimer;
                isPomodoro = false;
                isShortPause = true;
                isLongPause = false;
                updateTimerText();
            }
        });

        longPauseButton = findViewById(R.id.btn_long_pause);
        longPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                seconds = longPauseTimer;
                isPomodoro = false;
                isShortPause = false;
                isLongPause = true;
                updateTimerText();
            }
        });


        // Start or Resume the timer button
        timerStartButton = findViewById(R.id.btn_start);
        timerStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mTimerView.start(timer_length, isPaused);
                startTimer();
                mTimerView.start(seconds+1, isPaused);
            }
        });

        // Pause the timer button
        timerPauseButton = findViewById(R.id.btn_pause);
        timerPauseButton.setVisibility(View.GONE);
        timerPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        // Reset the timer button
        timerResetButton = findViewById(R.id.btn_restart);
        timerResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                if (isPomodoro) {
                    seconds = pomodoroTimer;
                } else if (isShortPause) {
                    seconds = shortPauseTimer;
                } else if (isLongPause) {
                    seconds = longPauseTimer;
                } else {
                    seconds = pomodoroTimer;
                }
                updateTimerText();
            }
        });

        // Open the Settings for the Timer
        settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(FocusModeTimerActivity.this, FocusModeSettingsActivity.class);
                startActivity(Intent);
            }
        });

        isCurrentlyOnApp = true;
    }

    public void onStart(){
        super.onStart();
    }

    public void onRestart(){
        super.onRestart();;
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRunning) {
            isCurrentlyOnApp = false;
            // Detect the recent 20 seconds used apps

            startServiceViaWorker();
            startService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning) {
            isCurrentlyOnApp = true;
            Log.d("TimerActivity", "Service has stopped");
            stopService();
        }
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();
        stopService();
    }

    private void startTimer() {
        if (!isRunning) {
            handler.sendEmptyMessage(0);
            isRunning = true;
            isPaused = false;
            timerStartButton.setVisibility(View.GONE);
            timerPauseButton.setVisibility(View.VISIBLE);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            mTimerView.pause();
            isRunning = false;
            isPaused = true;
            handler.removeCallbacks(timerRunnable);
            timer_length = seconds;
            timerStartButton.setVisibility(View.VISIBLE);
            timerPauseButton.setVisibility(View.GONE);
        }
    }

    private void resetTimer() {
        mTimerView.stop();
        isRunning = false;
        seconds = 0;
        handler.removeCallbacks(timerRunnable);
        timerStartButton.setVisibility(View.VISIBLE);
        timerPauseButton.setVisibility(View.GONE);
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            if (seconds >= 0) {
                updateTimerText();
                handler.postDelayed(this, 1000);
            } else {
                isRunning = false;
                if (!isScreenOn()) {
                    Log.d("FocusModeTimerActivity", "trying to stop service and notify");
                    isCurrentlyOnApp = true;
                    stopService();
                }
                v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));

                resetTimer();
                if (isPomodoro) {
                    seconds = pomodoroTimer;
                } else if (isShortPause) {
                    seconds = shortPauseTimer;
                } else if (isLongPause) {
                    seconds = longPauseTimer;
                } else {
                    seconds = pomodoroTimer;
                }
                updateTimerText();
            }
        }
    };

    private void updateTimerText() {
        //int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        String timeString = String.format("%02d:%02d", minutes, secs);
        timerText.setText(timeString);
    }

    private boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return powerManager.isInteractive();
    }

    public void startService() {
        Log.d("FocusModeTimerActivity", "startService called");
        if (!BackgroundAppService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, BackgroundAppService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    public void stopService() {
        Log.d("FocusModeTimerActivity", "stopService called");
        if (BackgroundAppService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, BackgroundAppService.class);
            stopService(serviceIntent);
        }
    }

    public void startServiceViaWorker() {
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        FocusModeWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);

    }
}
