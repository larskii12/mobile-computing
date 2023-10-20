package com.example.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.mainactivity.R;



public class FilterAdjustmentActivity extends AppCompatActivity {
    ImageButton returnButton;
    RadioGroup ascGroup;
    RadioGroup descGroup;
    RadioButton selectedRadioButton;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_adjustment);

        returnButton = findViewById(R.id.returnButton);
        ascGroup = findViewById(R.id.radioGroupAsc);
        descGroup = findViewById(R.id.radioGroupDesc);

        ascGroup.clearCheck();
        descGroup.clearCheck();

        ascGroup.setOnCheckedChangeListener(ascListener);

        descGroup.setOnCheckedChangeListener(descListener);

        // return back to the previous page
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

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