package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.background_app.BackgroundAppService;
import com.comp90018.uninooks.views.TimerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Vibrator;

import java.util.ArrayList;
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

    private ImageView roundOne;
    private ImageView roundTwo;
    private ImageView roundThree;
    private ImageView roundFour;
    private ImageView roundFive;
    private ImageView[] sequenceIndicators;

    private BottomNavigationView bottomNav;

    private boolean isRunning = false;

    private boolean isPaused = false;
    private boolean wasPaused = false;

    private boolean isPomodoro = false;
    private boolean isShortPause = false;
    private boolean isLongPause = false;
    private boolean settingsClicked = false;
    private boolean onAutoSequence = false;
    private boolean inSequence = false;

    private int pomodoroTime = 1500;
    private int shortPauseTime = 300;
    private int longPauseTime = 900;
    private int overallDefaultTime = 1500;

    private int pomodoroSequenceNum = 0;
    private int pomodoroSequenceMax = 4;

    private long stopTimestamp = 0;
    private long playTimestamp = 0;


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

//        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize UI components and set up timer controls
        mTimerView = findViewById(R.id.timer);
        timerText = findViewById(R.id.timerTextView);
        bottomNav = findViewById(R.id.bottom_navigation);

        roundOne = findViewById(R.id.sequence1);
        roundTwo = findViewById(R.id.sequence2);
        roundThree = findViewById(R.id.sequence3);
        roundFour = findViewById(R.id.sequence4);
        roundFive = findViewById(R.id.sequence5);

        bottomNav.setSelectedItemId(R.id.focusNav);
        sequenceIndicators = new ImageView[]{roundOne, roundTwo, roundThree, roundFour, roundFive};

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
//                playTimestamp = System.currentTimeMillis();
                if (onAutoSequence) {
                    disableAllButtons();
                    startAutoSequence();
                } else {
//                    enableAllButtons();
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
//                stopTimestamp = System.currentTimeMillis();
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

        clickPomodoroButton();
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

        clickShortPauseButton();
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

        clickLongPauseButton();
    }

    private void clickPomodoroButton() {
        if (inSequence) {
            pomodoroButton.setBackgroundColor(ContextCompat.getColor(this, R.color.stone_grey));
            pomodoroButton.setTextColor(ContextCompat.getColor(this, R.color.white));
            unclickShortPauseButton();
            unclickLongPauseButton();
        } else {
            pomodoroButton.setBackgroundColor(ContextCompat.getColor(this, R.color.deepBlue));
            pomodoroButton.setTextColor(ContextCompat.getColor(this, R.color.white));
            unclickShortPauseButton();
            unclickLongPauseButton();
        }
    }

    private void unclickPomodoroButton() {
        if (inSequence) {
            pomodoroButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            pomodoroButton.setTextColor(ContextCompat.getColor(this, R.color.stone_grey));
        } else {
            pomodoroButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            pomodoroButton.setTextColor(ContextCompat.getColor(this, R.color.deepBlue));
        }
    }

    private void clickShortPauseButton() {
        if (inSequence) {
            shortPauseButton.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
            shortPauseButton.setTextColor(ContextCompat.getColor(this, R.color.white));
            unclickPomodoroButton();
            unclickLongPauseButton();
        } else {
            shortPauseButton.setBackgroundColor(ContextCompat.getColor(this, R.color.deepBlue));
            shortPauseButton.setTextColor(ContextCompat.getColor(this, R.color.white));
            unclickPomodoroButton();
            unclickLongPauseButton();
        }

    }

    private void unclickShortPauseButton() {
        shortPauseButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        shortPauseButton.setTextColor(ContextCompat.getColor(this, R.color.deepBlue));
    }

    private void clickLongPauseButton() {
        longPauseButton.setBackgroundColor(ContextCompat.getColor(this, R.color.deepBlue));
        longPauseButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        unclickPomodoroButton();
        unclickShortPauseButton();
    }

    private void unclickLongPauseButton() {
        longPauseButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        longPauseButton.setTextColor(ContextCompat.getColor(this, R.color.deepBlue));
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
            wasPaused = true;
            handler.removeCallbacks(timerRunnable);
            handler.removeCallbacks(pomodoroRunnable);
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
                inSequence = false;
                timerStartButton.setVisibility(View.VISIBLE);
                timerPauseButton.setVisibility(View.GONE);

                if (pomodoroSequenceNum == 5) {
                    sequenceIndicators[pomodoroSequenceNum-1].setVisibility(View.VISIBLE);
                    enableAllButtons();
                }

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
        int timeSpentPaused = 0;
        @Override
        public void run() {
            System.out.println("CURRENT SECONDS: " + seconds);
//            if (inSequence && isPomodoro && wasPaused) {
//                timeSpentPaused = (int) ((playTimestamp - stopTimestamp) / 1000);
//                System.out.println("time spent paused in pom (s) " + timeSpentPaused);
//                handler.postDelayed(pomodoroRunnable, (pomodoroTime + timeSpentPaused) * 1000);
//                mTimerView.start(seconds, isPaused);
//                startTimer();
//                wasPaused = false;
//            } else if (inSequence && isShortPause && wasPaused) {
//                timeSpentPaused = (int) ((playTimestamp - stopTimestamp) / 1000);
//                System.out.println("time spent paused in short (s) " + timeSpentPaused);
//                mTimerView.start(seconds, isPaused);
//                startTimer();
//                wasPaused = false;
//            } else if (inSequence && isLongPause && wasPaused) {
//                timeSpentPaused = (int) ((playTimestamp - stopTimestamp) / 1000);
//                System.out.println("time spent paused in long (s) " + timeSpentPaused);
//                mTimerView.start(seconds, isPaused);
//                startTimer();
//                wasPaused = false;
//            } else
            if (pomodoroSequenceNum > 0) {
                System.out.println("TO SET INDICATOR IN ARRAY (have to -1): " + pomodoroSequenceNum);
                sequenceIndicators[pomodoroSequenceNum-1].setVisibility(View.VISIBLE);
                // can vibrate here?
                System.out.println("SEQUENCE OVER, WILL VIBRATE");

//                if (pomodoroSequenceNum == 5) {
//                    pomodoroSequenceNum = 0;
//                    clearSequenceIndicators();
//                }
            }

            if (pomodoroSequenceNum < pomodoroSequenceMax) {
                inSequence = true;
                seconds = pomodoroTime;
                clickPomodoroButton();
                mTimerView.start(pomodoroTime, isPaused);
                startTimer();
                System.out.println("Pomodoro timer started");

                isPomodoro = true;
                isShortPause = false;
                isLongPause = false;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seconds = shortPauseTime;
                        clickShortPauseButton();
                        mTimerView.start(shortPauseTime, isPaused);
                        startTimer();
                        System.out.println("Short pause timer started");

//                        System.out.println("DELAY for short pause (s): " + (shortPauseTime + timeSpentPaused));
                        handler.postDelayed(pomodoroRunnable, (shortPauseTime) * 1000);
                        timeSpentPaused = 0;

                        isPomodoro = false;
                        isShortPause = true;
                        isLongPause = false;

                        pomodoroSequenceNum++;
                        System.out.println("Sequence number: " + pomodoroSequenceNum);
                    }
                }, (pomodoroTime) * 1000);
            } else if (pomodoroSequenceNum == pomodoroSequenceMax) {
                inSequence = true;
                seconds = pomodoroTime;
                clickPomodoroButton();
                mTimerView.start(pomodoroTime, isPaused);
                startTimer();
                System.out.println("Pomodoro timer started");

                isPomodoro = true;
                isShortPause = false;
                isLongPause = false;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seconds = longPauseTime;
                        mTimerView.start(longPauseTime, isPaused);
                        startTimer();
                        System.out.println("Long pause timer started");

                        isPomodoro = false;
                        isShortPause = false;
                        isLongPause = true;

                        pomodoroSequenceNum = 0;
                        System.out.println("Sequence is done!");
//                        timerStartButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.deepBlue));
                    }
                }, (pomodoroTime) * 1000);
            }
            }
        };

    private void clearSequenceIndicators() {
        for (ImageView indicator : sequenceIndicators) {
            indicator.setVisibility(View.GONE);
        }
    }

    /**
     * Disable all buttons except for settings
     */
    private void disableAllButtons() {
        pomodoroButton.setClickable(false);
        shortPauseButton.setClickable(false);
        longPauseButton.setClickable(false);
        timerResetButton.setClickable(false);
        timerPauseButton.setClickable(false);
    }

    private void enableAllButtons() {
        pomodoroButton.setClickable(true);
        shortPauseButton.setClickable(true);
        longPauseButton.setClickable(true);
        timerResetButton.setClickable(true);
    }

    public void openSettings(View view) {

    }
}
