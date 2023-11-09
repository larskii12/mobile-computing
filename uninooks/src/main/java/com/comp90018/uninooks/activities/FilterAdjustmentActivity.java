package com.comp90018.uninooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.comp90018.uninooks.R;

import java.util.HashMap;



public class FilterAdjustmentActivity extends AppCompatActivity {

    ScrollView scrollView;
    ImageButton returnButton;
    Button resetButton;
    Button applyButton;
    ConstraintLayout facilitiesLayout;
    RadioGroup ascGroup;
    RadioGroup descGroup;
    RadioButton selectedRadioButton;
    TextView distDisplay;
    SeekBar seekBar;
    HashMap<String, String> filtersChosen;

    private int userId;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_adjustment);
        filtersChosen = new HashMap<>();

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");

        returnButton = findViewById(R.id.returnButton);
        applyButton = findViewById(R.id.applyButton);
        resetButton = findViewById(R.id.resetButton);
        scrollView = findViewById(R.id.scrollView);
        seekBar = findViewById(R.id.seekBar);
        distDisplay = findViewById(R.id.distanceIndicator);
        facilitiesLayout = findViewById(R.id.facilitiesFilter);
        ascGroup = findViewById(R.id.radioGroupAsc);
        descGroup = findViewById(R.id.radioGroupDesc);

        ascGroup.clearCheck();
        descGroup.clearCheck();

        returnButton.setOnClickListener(returnListener);
        applyButton.setOnClickListener(applyListener);
        resetButton.setOnClickListener(resetListener);
        seekBar.setOnSeekBarChangeListener(barListener);
        ascGroup.setOnCheckedChangeListener(ascListener);
        descGroup.setOnCheckedChangeListener(descListener);
    }

    /**
     * This activity finishes, returns back to the previous page (search page)
     */
    private View.OnClickListener returnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if (getIntent().hasExtra("filters")) {
//                getIntent().removeExtra("filters");
//            }
            finish();
        }
    };

    /**
     * Filters are applied,
     *
     * If no filters are applied, all places will be shown from nearest to furthest from user
     * current location (default setting)
     */
    private View.OnClickListener applyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(FilterAdjustmentActivity.this, SearchResults.class);

            retrieveAllCheckedBox();
            for (String key : filtersChosen.keySet()) {
                String value = filtersChosen.get(key);
                System.out.println("Key: " + key + " Value: " + value);
            }

            // Pass the filter to next page
            intent.putExtra("filters", filtersChosen);

            // Pass the user to next page
            intent.putExtra("USER_ID_EXTRA", userId);
            intent.putExtra("USER_EMAIL_EXTRA", userEmail);
            intent.putExtra("USER_NAME_EXTRA", userName);

            startActivity(intent);
        }
    };

    /**
     * Resets all filters chosen by the user
     */
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            seekBar.setProgress(0);
            barListener.onStopTrackingTouch(seekBar);
            unselectAllCheckBox();
            ascGroup.clearCheck();
            descGroup.clearCheck();
            filtersChosen.clear();
        }
    };

    /**
     * Unselects all checkboxes that is contained in the layout (facilities)
     */
    private void unselectAllCheckBox() {
        for(int i = 0 ; i < facilitiesLayout.getChildCount() ; i++) {
            View item =  (View) facilitiesLayout.getChildAt(i);
            if (item instanceof CheckBox) {
                CheckBox box = (CheckBox) item;
                box.setChecked(false);
            }
        }
    }

    /**
     * Adds all checked box into the filters list
     */
    private void retrieveAllCheckedBox() {
        int count = 0;
        for (int i = 0 ; i < facilitiesLayout.getChildCount() ; i++) {
            View item = (View) facilitiesLayout.getChildAt(i);
            if (item instanceof CheckBox) {
                CheckBox box = (CheckBox) item;
                if (box.isChecked()) {
                    count += 1;
                    String checkBoxName = box.getTag().toString();
                    String key = "CHECKBOX" + count;
                    filtersChosen.put(key, checkBoxName);
                }
            }
        }
    }

    /**
     * Tracks changes happening on the seek bar
     */
    private SeekBar.OnSeekBarChangeListener barListener = new SeekBar.OnSeekBarChangeListener() {
        int distanceVal;
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
            distanceVal = i;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (distanceVal == 1000) {
                // text is 0m - 1km
                distDisplay.setText("10m - 1km");
            } else if (distanceVal == 10) {
                distDisplay.setText("10m");
            } else {
                // text is 0m - __m
                distDisplay.setText("10m - " + distanceVal + "m");
                distDisplay.setTextSize(18);
            }
            filtersChosen.put("DISTANCE", String.valueOf(distanceVal));
        }
    };

    /**
     * Listens for any clicks in the radio group
     *
     * Due to the layout of having 2 radio groups, it checks with the other group and unselects any
     * button that has been selected (only keeps latest selected radio button)
     */
    private OnCheckedChangeListener ascListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int id) {
            if (id != -1) {
                descGroup.setOnCheckedChangeListener(null);
                descGroup.clearCheck();
                selectedRadioButton = findViewById(id);
                String radioSelected = selectedRadioButton.getText().toString();
                descGroup.setOnCheckedChangeListener(descListener);
                filtersChosen.put("RADIO", radioSelected);
            }
            ascGroup.getCheckedRadioButtonId();
        }
    };

    private OnCheckedChangeListener descListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int id) {
            if (id != -1) {
                ascGroup.setOnCheckedChangeListener(null);
                ascGroup.clearCheck();
                selectedRadioButton = findViewById(id);
                String radioSelected = selectedRadioButton.getText().toString();
                ascGroup.setOnCheckedChangeListener(ascListener);
                filtersChosen.put("RADIO", radioSelected);
            }
        }
    };
}