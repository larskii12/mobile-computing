package com.comp90018.uninooks.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.comp90018.uninooks.R;



public class FilterAdjustmentActivity extends AppCompatActivity {
    ScrollView scrollView;
    ImageButton returnButton;
    Button resetButton;
    Button applyButton;
    FrameLayout facilitiesLayout;
    RadioGroup ascGroup;
    RadioGroup descGroup;
    RadioButton selectedRadioButton;
    TextView distDisplay;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_adjustment);

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
        }
    };

    /**
     * Unselects all checkboxes that is contained in the layout (facilities)
     */
    private void unselectAllCheckBox() {
        for(int i=0;i<facilitiesLayout.getChildCount();i++) {
            View item =  (View)facilitiesLayout.getChildAt(i);
            if (item instanceof CheckBox) {
                CheckBox box = (CheckBox) item;
                box.setChecked(false);
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
            }
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
            }
        }
    };
}