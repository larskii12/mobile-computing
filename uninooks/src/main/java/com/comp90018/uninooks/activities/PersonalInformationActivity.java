package com.comp90018.uninooks.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;

public class PersonalInformationActivity extends AppCompatActivity {

    private ArrayAdapter<CharSequence> facultyAdapter;

    private Spinner spinnerSignUpFacultyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_perfonal_info);

        ImageView backArrow = findViewById(R.id.Account_Back_to_Home_Arrow_Left);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextView usernameTextView = findViewById(R.id.Account_Pi_Edit_Name);
        final EditText editUsernameEditText = findViewById(R.id.EditTextNewUsername);
        ImageView editIcon = findViewById(R.id.Account_Pi_Ic_Edit_Name);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsernameEditText.getVisibility() == View.GONE) {
                    editUsernameEditText.setVisibility(View.VISIBLE);
                    usernameTextView.setVisibility(View.GONE);
                } else {
                    editUsernameEditText.setVisibility(View.GONE);
                    usernameTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView emailTextView = findViewById(R.id.Account_Pi_Edit_Email);
        final EditText editEmailEditText = findViewById(R.id.EditTextNewEmail);
        ImageView editEmailIcon = findViewById(R.id.Account_Pi_Ic_Edit_Email);

        editEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editEmailEditText.getVisibility() == View.GONE) {
                    editEmailEditText.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.GONE);
                } else {
                    editEmailEditText.setVisibility(View.GONE);
                    emailTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView mobileNumberTextView = findViewById(R.id.Account_Pi_Edit_MobileNumber);
        final EditText editMobileNumberEditText = findViewById(R.id.EditTextNewMobileNumber);
        ImageView editMobileNumberIcon = findViewById(R.id.Account_Pi_Ic_Edit_MobileNumber);

        editMobileNumberIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMobileNumberEditText.getVisibility() == View.GONE) {
                    editMobileNumberEditText.setVisibility(View.VISIBLE);
                    mobileNumberTextView.setVisibility(View.GONE);
                } else {
                    editMobileNumberEditText.setVisibility(View.GONE);
                    mobileNumberTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView facultyTextView = findViewById(R.id.Account_Pi_Edit_Faculty);
        final Spinner facultySpinner = findViewById(R.id.SpinnerPiFacultyList);
        ImageView editFacultyIcon = findViewById(R.id.Account_Pi_Ic_Edit_Faculty);

//        // Set the faculty drop down list
//        facultyAdapter = ArrayAdapter.createFromResource(this, R.array.faculty_list, android.R.layout.simple_spinner_item);
//        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spinnerSignUpFacultyList.setAdapter(facultyAdapter);
//        spinnerSignUpFacultyList.setOnItemSelectedListener(this);


        editFacultyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facultySpinner.getVisibility() == View.GONE) {
                    facultySpinner.setVisibility(View.VISIBLE);
                    facultyTextView.setVisibility(View.GONE);
                } else {
                    facultySpinner.setVisibility(View.GONE);
                    facultyTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}