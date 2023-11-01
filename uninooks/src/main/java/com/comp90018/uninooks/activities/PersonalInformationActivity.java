package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.user.UserServiceImpl;

public class PersonalInformationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerChangeFacultyList;

    private ArrayAdapter<CharSequence> facultyChangeAdapter;

    private Spinner spinnerChangeDegreeList;

    private ArrayAdapter<CharSequence> degreeChangeAdapter;

    private int userId;

    private String userName;

    private String userEmail;

    private String userFaculty;

    private String userDegree;

    private TextView userNameTextView;

    private TextView emailTextView;

    private TextView degreeTextView;

    private TextView facultyTextView;

    private EditText editUsernameEditText;

    private EditText editEmailEditText;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    break;

                case 1:
                    userNameTextView.setText(userName);
                    emailTextView.setText(userEmail);
                    degreeTextView.setText(userDegree);
                    facultyTextView.setText(userFaculty);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_perfonal_info);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 6);

        ImageView backArrow = findViewById(R.id.Account_Back_to_Home_Arrow_Left);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userNameTextView = findViewById(R.id.Account_Pi_Edit_Name);
        editUsernameEditText = findViewById(R.id.EditTextNewUsername);
        ImageView editNameIcon = findViewById(R.id.Account_Pi_Ic_Edit_Name);

        editNameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsernameEditText.getVisibility() == View.GONE) {
                    editUsernameEditText.setVisibility(View.VISIBLE);
                    userNameTextView.setVisibility(View.GONE);
                } else {
                    editUsernameEditText.setVisibility(View.GONE);
                    userNameTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        emailTextView = findViewById(R.id.Account_Pi_Edit_Email);
        editEmailEditText = findViewById(R.id.EditTextNewEmail);
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

        spinnerChangeDegreeList = findViewById(R.id.SpinnerPiDegreeList);

        // Set the degree drop down list
        degreeChangeAdapter = ArrayAdapter.createFromResource(this, R.array.degree_list, android.R.layout.simple_spinner_item);
        degreeChangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerChangeDegreeList.setAdapter(degreeChangeAdapter);
        spinnerChangeDegreeList.setOnItemSelectedListener(this);

        degreeTextView = findViewById(R.id.Account_Pi_Edit_Degree);
        ImageView editDegreeIcon = findViewById(R.id.Account_Pi_Ic_Edit_Degree);

        editDegreeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerChangeDegreeList.getVisibility() == View.GONE) {
                    spinnerChangeDegreeList.setVisibility(View.VISIBLE);
                    degreeTextView.setVisibility(View.GONE);
                } else {
                    spinnerChangeDegreeList.setVisibility(View.GONE);
                    degreeTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        facultyTextView = findViewById(R.id.Account_Pi_Edit_Faculty);
        ImageView editFacultyIcon = findViewById(R.id.Account_Pi_Ic_Edit_Faculty);

        spinnerChangeFacultyList = findViewById(R.id.SpinnerPiFacultyList);

        // Set the faculty drop down list
        facultyChangeAdapter = ArrayAdapter.createFromResource(this, R.array.faculty_list, android.R.layout.simple_spinner_item);
        facultyChangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerChangeFacultyList.setAdapter(facultyChangeAdapter);
        spinnerChangeFacultyList.setOnItemSelectedListener(this);

        editFacultyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerChangeFacultyList.getVisibility() == View.GONE) {
                    spinnerChangeFacultyList.setVisibility(View.VISIBLE);
                    facultyTextView.setVisibility(View.GONE);
                } else {
                    spinnerChangeFacultyList.setVisibility(View.GONE);
                    facultyTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        new Thread() {
            public void run(){
                initUserInfo();
            }
        }.start();


        // Reset Password
        ImageView editPasswordIcon = findViewById(R.id.Account_Pi_Ic_Edit_Password);
        editPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initUserInfo() {

            try {

                User user = new UserServiceImpl().getUser(userId);

                userName = user.getUserName();
                userEmail = user.getUserEmail();
                userFaculty = user.getUserFaculty();

                switch (user.getUserAQFLevel()) {
                    case 1:
                        userDegree = "Certificate I";
                        break;
                    case 2:
                        userDegree = "Certificate II";
                        break;
                    case 3:
                        userDegree = "Certificate III";
                        break;
                    case 4:
                        userDegree = "Certificate IV";
                        break;
                    case 5:
                        userDegree = "Diploma";
                        break;
                    case 6:
                        userDegree = "Advanced Diploma, Associate Degree";
                        break;
                    case 7:
                        userDegree = "Bachelor Degree";
                        break;
                    case 8:
                        userDegree = "Bachelor Honours Degree";
                        break;
                    case 9:
                        userDegree = "Masters Degree";
                        break;
                    case 10:
                        userDegree = "Doctoral Degree";
                        break;
                    default:
                        userDegree = "Not Provided";
                }

                handler.sendEmptyMessage(1);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}