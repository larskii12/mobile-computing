package com.comp90018.uninooks.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.background_app.BackgroundAppService;
import com.comp90018.uninooks.views.TimerView;

import java.util.List;

public class FocusModeTimerActivity extends AppCompatActivity {

    private List<String> unwantedApps;

    CountDownTimer countDownTimer;

    private CountDownTimer pomodoroTimer;

    private boolean isInBackground = false;

    private int timer_length = 30;

    private int seconds = 30;

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

    private boolean isRunning = false;

    private boolean isPaused = false;

    private boolean isPomodoro = false;
    private boolean isShortPause = false;
    private boolean isLongPause = false;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    handler.postDelayed(timerRunnable, 1000);
                    break;

                case 1:

                    break;

                case 2:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        // Initialize UI components and set up timer controls
        mTimerView = findViewById(R.id.timer);
        timerText = findViewById(R.id.timerTextView);

        // Change to which setup for the timer
        pomodoroButton = findViewById(R.id.btn_pomodoro);
        pomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seconds = 1500;
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
                seconds = 300;
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
                seconds = 900;
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
                mTimerView.start(timer_length, isPaused);
                startTimer();
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
                    seconds = 1500;
                } else if (isShortPause) {
                    seconds = 300;
                } else if (isLongPause) {
                    seconds = 900;
                } else {
                    seconds = 1500;
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


        // Initialize unwantedApps list
//        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String unwantedAppsList = preferences.getString("unwanted_apps", "");
//        unwantedApps = new ArrayList<>(Arrays.asList(unwantedAppsList.split(",")));
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
        isInBackground = true;
        // Detect the recent 20 seconds used apps
        Intent serviceIntent = new Intent(this, BackgroundAppService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInBackground = false;

        Intent serviceIntent = new Intent(this, BackgroundAppService.class);
        stopService(serviceIntent);
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();
    }

//    public void startPomodoroTimer(long duration) {
//        pomodoroTimer = new CountDownTimer(duration, 1000) {
//            public void onTick(long millisUntilFinished) {
//                // Update timer UI
//            }
//
//            public void onFinish() {
//                // Handle Pomodoro timer completion
//            }
//        }.start();
//    }

    // Add methods for checking and monitoring other apps
    // ...

    // Additional methods and logic
    // ...

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == Request_Code_Location){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                updateLocation();
//            }
//        }
//    }

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
                // Timer is done, you can implement actions here
                isRunning = false;
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

    public void openSettings(View view) {

    }


}
