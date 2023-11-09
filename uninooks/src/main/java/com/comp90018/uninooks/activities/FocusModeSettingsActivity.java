package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
    ImageView pomTimeIcon;
    ImageView shortTimeIcon;
    ImageView longTimeIcon;
    int pomodoroTimeValue;
    int shortBreakTimeValue;
    int longBreakTimeValue;
    Boolean onAutoSequence;

    private static final int POMODORO_TIME_DEFAULT = 25;
    private static final int SHORT_PAUSE_TIME_DEFAULT = 5;
    private static final int LONG_PAUSE_TIME_DEFAULT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_setting);

        LinearLayout pomodoroTimerLayout = findViewById(R.id.LinearLayoutFocusSettingPomodoro);
        final RelativeLayout pomodoroInputsLayout = findViewById(R.id.RelativeLayoutFocusSettingPomodoro);

        returnButton = findViewById(R.id.returnButton);
        resetButton = findViewById(R.id.resetSettingsButton);
        saveButton = findViewById(R.id.saveSettingsButton);

        toggleSwitch = findViewById(R.id.toggleSwitch);
        pomodoroTime = findViewById(R.id.EditTextFocusSettingMins);
        shortBreakTime = findViewById(R.id.EditTextFocusShortBreakSetting);
        longBreakTime = findViewById(R.id.EditTextFocusLongBreakSetting);

        pomTimeIcon = findViewById(R.id.pomTimeIcon);
        shortTimeIcon = findViewById(R.id.shortTimeIcon);
        longTimeIcon = findViewById(R.id.longTimeIcon);

        // retrieve settings, and place those in the editTexts + toggle enabled or not
        retrieveSettings();

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

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) pomTimeIcon.getLayoutParams();

                // Toggle the visibility of the RelativeLayout
                if (pomodoroInputsLayout.getVisibility() == View.GONE) {
                    pomodoroInputsLayout.setVisibility(View.VISIBLE);
                    pomTimeIcon.setImageResource(R.drawable.ic_arrow_right);
                    layoutParams.leftMargin = 128;
                    layoutParams.rightMargin = 40;
                    pomTimeIcon.setLayoutParams(layoutParams);
                } else {
                    pomodoroInputsLayout.setVisibility(View.GONE);
                    pomTimeIcon.setImageResource(R.drawable.ic_arrow_down);
                    layoutParams.leftMargin = 118;
                    layoutParams.rightMargin = 22;
                    pomTimeIcon.setLayoutParams(layoutParams);
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

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) shortTimeIcon.getLayoutParams();

                // Toggle the visibility of the RelativeLayout
                if (shortBreakInputsLayout.getVisibility() == View.GONE) {
                    shortBreakInputsLayout.setVisibility(View.VISIBLE);
                    shortTimeIcon.setImageResource(R.drawable.ic_arrow_right);
                    layoutParams.leftMargin = 128;
                    layoutParams.rightMargin = 40;
                    shortTimeIcon.setLayoutParams(layoutParams);
                } else {
                    shortBreakInputsLayout.setVisibility(View.GONE);
                    shortTimeIcon.setImageResource(R.drawable.ic_arrow_down);
                    layoutParams.leftMargin = 118;
                    layoutParams.rightMargin = 22;
                    shortTimeIcon.setLayoutParams(layoutParams);
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

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) longTimeIcon.getLayoutParams();

                // Toggle the visibility of the RelativeLayout
                if (longBreakInputsLayout.getVisibility() == View.GONE) {
                    longBreakInputsLayout.setVisibility(View.VISIBLE);
                    longTimeIcon.setImageResource(R.drawable.ic_arrow_right);
                    layoutParams.leftMargin = 128;
                    layoutParams.rightMargin = 40;
                    longTimeIcon.setLayoutParams(layoutParams);
                } else {
                    longBreakInputsLayout.setVisibility(View.GONE);
                    longTimeIcon.setImageResource(R.drawable.ic_arrow_down);
                    layoutParams.leftMargin = 118;
                    layoutParams.rightMargin = 22;
                    longTimeIcon.setLayoutParams(layoutParams);
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
    private final View.OnClickListener returnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    /**
     * Resets all settings to default settings, stays on current page
     */
    private final View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pomodoroTime.setText(String.valueOf(POMODORO_TIME_DEFAULT));
            shortBreakTime.setText(String.valueOf(SHORT_PAUSE_TIME_DEFAULT));
            longBreakTime.setText(String.valueOf(LONG_PAUSE_TIME_DEFAULT));
            toggleSwitch.setChecked(false);
        }
    };

    /**
     * Goes back to previous page with settings applied
     */
    private final View.OnClickListener saveListener = new View.OnClickListener() {
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
        SharedPreferences sharedPreferences = getSharedPreferences("uninooks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (pomodoro.equals("")) {
            pomodoro = String.valueOf(POMODORO_TIME_DEFAULT);
        }
        if (shortBreak.equals("")) {
            shortBreak = String.valueOf(SHORT_PAUSE_TIME_DEFAULT);
        }
        if (longBreak.equals("")) {
            longBreak = String.valueOf(LONG_PAUSE_TIME_DEFAULT);
        }

        editor.putInt(getString(R.string.pomodoro_setting), Integer.parseInt(pomodoro)*60);
        editor.putInt(getString(R.string.short_break_setting), Integer.parseInt(shortBreak)*60);
        editor.putInt(getString(R.string.long_break_setting), Integer.parseInt(longBreak)*60);
        editor.putBoolean(getString(R.string.auto_pomodoro), onAutoPomodoro);
        editor.apply();
    }

    /**
     * Times are all in minutes
     */
    private void retrieveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("uninooks", Context.MODE_PRIVATE);
        pomodoroTimeValue = sharedPreferences.getInt(getString(R.string.pomodoro_setting), POMODORO_TIME_DEFAULT);
        shortBreakTimeValue = sharedPreferences.getInt(getString(R.string.short_break_setting), SHORT_PAUSE_TIME_DEFAULT);
        longBreakTimeValue = sharedPreferences.getInt(getString(R.string.long_break_setting), LONG_PAUSE_TIME_DEFAULT);
        onAutoSequence = sharedPreferences.getBoolean(getString(R.string.auto_pomodoro), false);

        pomodoroTime.setText(String.valueOf(pomodoroTimeValue/60));
        shortBreakTime.setText(String.valueOf(shortBreakTimeValue/60));
        longBreakTime.setText(String.valueOf(longBreakTimeValue/60));
        toggleSwitch.setChecked(onAutoSequence);

    }
}
