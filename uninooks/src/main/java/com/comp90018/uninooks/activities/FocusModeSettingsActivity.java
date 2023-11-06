package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.comp90018.uninooks.R;


public class FocusModeSettingsActivity extends AppCompatActivity {
    ImageButton returnButton;
    Button resetButton;
    Button saveButton;
    Switch toggleSwitch;
    EditText pomodoroTime;
    EditText shortBreakTime;
    EditText longBreakTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_setting);

        LinearLayout pomodoroTimerLayout = findViewById(R.id.LinearLayoutFocusSettingPomodoro);
        final RelativeLayout pomodoroInputsLayout = findViewById(R.id.RelativeLayoutFocusSettingPomodoro);

        returnButton = findViewById(R.id.returnButton);
        resetButton = findViewById(R.id.resetSettingsButton);
        saveButton = findViewById(R.id.saveSettingsButton);

        toggleSwitch = findViewById(R.id.toggleSwitch);
        pomodoroTime = findViewById(R.id.EditTextFocusSettingMins); // these are all in minutes, have to convert to milliiseconds
        shortBreakTime = findViewById(R.id.EditTextFocusShortBreakSetting);
        longBreakTime = findViewById(R.id.EditTextFocusLongBreakSetting);

        // Set the click listener for the Pomodoro Timer LinearLayout
        pomodoroTimerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a transition instance, a fade transition in this case
                Transition transition = new Fade();
                transition.setDuration(200); // Set the duration of the transition
                transition.addTarget(pomodoroInputsLayout); // Set the target view for the transition

                // Begin delayed transition
                TransitionManager.beginDelayedTransition((ViewGroup) v.getParent(), transition);

                // Toggle the visibility of the RelativeLayout
                if (pomodoroInputsLayout.getVisibility() == View.GONE) {
                    pomodoroInputsLayout.setVisibility(View.VISIBLE);
                } else {
                    pomodoroInputsLayout.setVisibility(View.GONE);
                }
            }
        });

        LinearLayout shortBreakLayout = findViewById(R.id.LinearLayoutFocusSettingShortBreak);
        final RelativeLayout shortBreakInputsLayout = findViewById(R.id.RelativeLayoutFocusSettingShortBreak);
        shortBreakLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a transition instance, a fade transition in this case
                Transition transition = new Fade();
                transition.setDuration(200); // Set the duration of the transition
                transition.addTarget(shortBreakInputsLayout); // Set the target view for the transition

                // Begin delayed transition
                TransitionManager.beginDelayedTransition((ViewGroup) v.getParent(), transition);

                // Toggle the visibility of the RelativeLayout
                if (shortBreakInputsLayout.getVisibility() == View.GONE) {
                    shortBreakInputsLayout.setVisibility(View.VISIBLE);
                } else {
                    shortBreakInputsLayout.setVisibility(View.GONE);
                }
            }
        });

        LinearLayout longBreakLayout = findViewById(R.id.LinearLayoutFocusSettingLongBreak);
        final RelativeLayout longBreakInputsLayout = findViewById(R.id.RelativeLayoutFocusSettingLongBreak);
        longBreakLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a transition instance, a fade transition in this case
                Transition transition = new Fade();
                transition.setDuration(200); // Set the duration of the transition
                transition.addTarget(longBreakInputsLayout); // Set the target view for the transition

                // Begin delayed transition
                TransitionManager.beginDelayedTransition((ViewGroup) v.getParent(), transition);

                // Toggle the visibility of the RelativeLayout
                if (longBreakInputsLayout.getVisibility() == View.GONE) {
                    longBreakInputsLayout.setVisibility(View.VISIBLE);
                } else {
                    longBreakInputsLayout.setVisibility(View.GONE);
                }
            }
        });

        returnButton.setOnClickListener(returnListener);
        resetButton.setOnClickListener(resetListener);
        saveButton.setOnClickListener(saveListener);
    }

    /**
     * Goes back to the previous page, no settings are applied/saved
     */
    private View.OnClickListener returnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    /**
     * Resets all settings to default settings, stays on current page
     */
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pomodoroTime.setText("");
            shortBreakTime.setText("");
            longBreakTime.setText("");
            toggleSwitch.setChecked(false);
        }
    };

    /**
     * Goes back to previous page with settings applied
     */
    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String pomodoro = pomodoroTime.getText().toString();
            String shortBreak = shortBreakTime.getText().toString();
            String longBreak = longBreakTime.getText().toString();
            Boolean onAutoPomodoro = toggleSwitch.isChecked();
            confirmSettings(pomodoro, shortBreak, longBreak, onAutoPomodoro);
            finish();
        }
    };

    private void confirmSettings(String pomodoro, String shortBreak, String longBreak, Boolean onAutoPomodoro) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.pomodoro_setting), Integer.parseInt(pomodoro)*60);
        editor.putInt(getString(R.string.short_break_setting), Integer.parseInt(shortBreak)*60);
        editor.putInt(getString(R.string.long_break_setting), Integer.parseInt(longBreak)*60);
        editor.putBoolean(getString(R.string.auto_pomodoro), onAutoPomodoro);
        editor.apply();
    }
}
