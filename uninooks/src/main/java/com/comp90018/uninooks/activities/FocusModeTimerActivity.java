package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.view.LayoutInflater;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Vibrator;

import java.util.List;

public class FocusModeTimerActivity extends AppCompatActivity {

    private List<String> unwantedApps;

    CountDownTimer countDownTimer;

    private CountDownTimer pomodoroTimer;

    private boolean isInBackground = false;

    private int timer_length = 30;

    private int seconds = 30;
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

    private boolean isRunning = false;

    private boolean isPaused = false;

    private boolean isPomodoro = false;
    private boolean isShortPause = false;
    private boolean isLongPause = false;
    private boolean settingsClicked = false;
    private boolean onAutoSequence = false;

    private int pomodoroTime = 1500;
    private int shortPauseTime = 300;
    private int longPauseTime = 900;
    private int overallDefaultTime = 1500;

    private int pomodoroSequenceNum = 0;
    private int pomodoroSequenceMax = 4;

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

        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize UI components and set up timer controls
        mTimerView = findViewById(R.id.timer);
        timerText = findViewById(R.id.timerTextView);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.focusNav);

        // Change to which setup for the timer
        pomodoroButton = findViewById(R.id.btn_pomodoro);
        pomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pomodoroButtonActions();
            }
        });

        shortPauseButton = findViewById(R.id.btn_short_pause);
        shortPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortPauseButtonActions();

            }
        });


        longPauseButton = findViewById(R.id.btn_long_pause);
        longPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longPauseButtonActions();
            }
        });


        // Start or Resume the timer button
        timerStartButton = findViewById(R.id.btn_start);
        timerStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mTimerView.start(timer_length, isPaused);
                System.out.println("autosequence on: " + onAutoSequence);
                if (onAutoSequence) {
                    System.out.println("starting auto sequence");
                    startAutoSequence();
                } else {
                    mTimerView.start(seconds, isPaused);
                    startTimer();
                }
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
                    seconds = pomodoroTime;
                } else if (isShortPause) {
                    seconds = shortPauseTime;
                } else if (isLongPause) {
                    seconds = longPauseTime;
                } else {
                    seconds = overallDefaultTime;
                }
                updateTimerText();
            }
        });

        // Open the Settings for the Timer
        settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsClicked = true;
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
        if (settingsClicked) {
            retrieveSettings();
            settingsClicked = false;

            if (onAutoSequence) {
                resetTimer();
                seconds = pomodoroTime;
                isPomodoro = true;
                isShortPause = false;
                isLongPause = false;
                updateTimerText();
            }
        } else {
            isInBackground = false;

            Intent serviceIntent = new Intent(this, BackgroundAppService.class);
            stopService(serviceIntent);
        }
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

    /**
     * Actions to be executed when pomodoro button is clicked
     */
    private void pomodoroButtonActions() {
        resetTimer();
        seconds = pomodoroTime;
        isPomodoro = true;
        isShortPause = false;
        isLongPause = false;
        updateTimerText();
    }

    /**
     * Actions to be executed when short pause button is clicked
     */
    private void shortPauseButtonActions() {
        resetTimer();
        seconds = shortPauseTime;
        isPomodoro = false;
        isShortPause = true;
        isLongPause = false;
        updateTimerText();
    }

    /**
     * Actions to be executed when long pause button is clicked
     */
    private void longPauseButtonActions() {
        resetTimer();
        seconds = longPauseTime;
        isPomodoro = false;
        isShortPause = false;
        isLongPause = true;
        updateTimerText();
    }

    private void startTimer() {
        if (!isRunning) {
            System.out.println("starting timer..");
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
            System.out.println(seconds);
            if (seconds >= 0) {
                updateTimerText();
                handler.postDelayed(this, 1000);
            } else {
                System.out.println("timer is done");
                v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));

                resetTimer();
                isRunning = false;
                timerStartButton.setVisibility(View.VISIBLE);
                timerPauseButton.setVisibility(View.GONE);

                // if auto..., else do the below
                if (isPomodoro) {
                    seconds = pomodoroTime;
                } else if (isShortPause) {
                    seconds = shortPauseTime;
                } else if (isLongPause) {
                    seconds = longPauseTime;
                } else {
                    seconds = overallDefaultTime;
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

    /**
     * Retrieve settings from the setting page
     */
    private void retrieveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        pomodoroTime = sharedPreferences.getInt(getString(R.string.pomodoro_setting), pomodoroTime);
        shortPauseTime = sharedPreferences.getInt(getString(R.string.short_break_setting), shortPauseTime);
        longPauseTime = sharedPreferences.getInt(getString(R.string.long_break_setting), longPauseTime);
        onAutoSequence = sharedPreferences.getBoolean(getString(R.string.auto_pomodoro), false);
    }

    /**
     * Starts auto sequence of the pomodoro timer, ran on a separate thread
     *
     * focus --> short pause --> focus --> short pause --> focus --> short pause --> focus -->
     * short pause --> focus --> long pause --> focus
     *
     * Buttons will be disabled while pomodoro sequence is running!!
     */
    private void startAutoSequence() {
        pomodoroRunnable.run();
    }

    private Runnable pomodoroRunnable = new Runnable() {
        @Override
        public void run() {
            seconds = pomodoroTime;
            mTimerView.start(pomodoroTime, isPaused);
            startTimer();
            System.out.println("Pomodoro timer started");

            if (pomodoroSequenceNum < pomodoroSequenceMax) {
//                System.out.println("isPaused for pomodoro time:" + isPaused);
//                mTimerView.start(pomodoroTime, isPaused);
//                startTimer();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start the short pause timer
                        seconds = shortPauseTime;
                        System.out.println("isPaused for short pause:" + isPaused);
                        mTimerView.start(shortPauseTime, isPaused);
                        startTimer();
                        System.out.println("Short pause timer started");

                        // Increment the sequence number
                        pomodoroSequenceNum++;
                        System.out.println("Sequence number: " + pomodoroSequenceNum);

                        // Trigger the next step in the sequence
                        handler.postDelayed(pomodoroRunnable, shortPauseTime * 1000);
                    }
                }, pomodoroTime * 1000);
            } else if (pomodoroSequenceNum == pomodoroSequenceMax) {
                // do the long pause
                // set pomodoroSequenceNum back to 0
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start the short pause timer
                        seconds = longPauseTime;
                        System.out.println("isPaused for long pause:" + isPaused);
                        mTimerView.start(longPauseTime, isPaused);
                        startTimer();
                        System.out.println("Long pause timer started");

                        // Increment the sequence number
                        pomodoroSequenceNum = 0;
                        System.out.println("Sequence is done!");
                    }
                }, pomodoroTime * 1000);
            }
            }
        };

    public void openSettings(View view) {

    }
}
