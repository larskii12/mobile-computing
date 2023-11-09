package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.mail.MailServiceImpl;
import com.comp90018.uninooks.service.user.UserServiceImpl;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final int OTP_TIMER = 60;
    private EditText editTextSignUpUserName;
    private EditText editTextSignUpEmail;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpConfirmPassword;
    private EditText editTextSingUpOTP;
    private Spinner spinnerSignUpFacultyList;

    private Spinner spinnerSignUpDegreeList;

    private ArrayAdapter<CharSequence> facultyAdapter;

    private ArrayAdapter<CharSequence> degreeAdapter;

    private Button buttonSignUpGetOTP;
    private Button buttonSignUpJoinNow;

    private TextView buttonSignUpReportIssue;

    private String otp;

//    private String name;
    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;

    private String faculty;

    private int aqfLevel;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

                case 1:

                    int time = (int) msg.obj;

                    if (time > 0) {
                        buttonSignUpGetOTP.setEnabled(false);
                        buttonSignUpGetOTP.setText(time + "s");
                        buttonSignUpGetOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.black));
                        buttonSignUpGetOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.grey));

                        Message message = new Message();
                        message.what = 1;
                        message.obj = time - 1;
                        handler.sendMessageDelayed(message, 1000);
                    }

                    else {
//                        ediTextSignUpName.setEnabled(true);
                        editTextSignUpEmail.setEnabled(true);
                        editTextSignUpUserName.setEnabled(true);
                        editTextSignUpPassword.setEnabled(true);
                        editTextSignUpConfirmPassword.setEnabled(true);
                        spinnerSignUpFacultyList.setEnabled(true);
                        spinnerSignUpDegreeList.setEnabled(true);
                        buttonSignUpGetOTP.setEnabled(true);
                        buttonSignUpGetOTP.setText("Get OTP");
                        buttonSignUpGetOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.white));
                        buttonSignUpGetOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.primary));
                    }

                    break;

                case 2:
//                    ediTextSignUpName.setEnabled(true);
                    editTextSignUpEmail.setEnabled(true);
                    editTextSignUpUserName.setEnabled(true);
                    editTextSignUpPassword.setEnabled(true);
                    editTextSignUpConfirmPassword.setEnabled(true);
                    spinnerSignUpFacultyList.setEnabled(true);
                    spinnerSignUpDegreeList.setEnabled(true);
                    buttonSignUpGetOTP.setEnabled(true);
                    buttonSignUpGetOTP.setText("Get OTP");
                    buttonSignUpGetOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.white));
                    buttonSignUpGetOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.primary));

                    break;

//                case 3:
//                    ediTextSignUpName.setEnabled(false);
//                    editTextSignUpEmail.setEnabled(false);
//                    editTextSignUpUserName.setEnabled(false);
//                    editTextSignUpPassword.setEnabled(false);
//                    editTextSignUpConfirmPassword.setEnabled(false);
//                    spinnerSignUpFacultyList.setEnabled(false);
//                    spinnerSignUpDegreeList.setEnabled(false);
//                    buttonSignUpGetOTP.setEnabled(false);
//                    buttonSignUpGetOTP.setText("Sending");
//                    buttonSignUpGetOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.grey));
//                    buttonSignUpGetOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.black));
//
//                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        ediTextSignUpName = findViewById(R.id.EdiTextSignUpName);
        editTextSignUpUserName = findViewById(R.id.EditTextSignUpUserName);
        editTextSignUpEmail = findViewById(R.id.EditTextSignUpEmail);
        editTextSignUpPassword = findViewById(R.id.EditTextSignUpPassword);
        editTextSignUpConfirmPassword = findViewById(R.id.EditTextSignUpConfirmPassword);
        editTextSingUpOTP = findViewById(R.id.EditTextSingUpOTP);
        spinnerSignUpFacultyList = findViewById(R.id.SpinnerSignUpFacultyList);
        spinnerSignUpDegreeList = findViewById(R.id.SpinnerSignUpDegreeList);

        // Set the faculty drop down list
        facultyAdapter = ArrayAdapter.createFromResource(this, R.array.faculty_list, android.R.layout.simple_spinner_item);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerSignUpFacultyList.setAdapter(facultyAdapter);
        spinnerSignUpFacultyList.setOnItemSelectedListener(this);

        // Set the degree drop down list
        degreeAdapter = ArrayAdapter.createFromResource(this, R.array.degree_list, android.R.layout.simple_spinner_item);
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerSignUpDegreeList.setAdapter(degreeAdapter);
        spinnerSignUpDegreeList.setOnItemSelectedListener(this);

        buttonSignUpGetOTP = findViewById(R.id.ButtonSignUpGetOTP);
        buttonSignUpJoinNow = findViewById(R.id.ButtonSignUpJoinNow);
        buttonSignUpReportIssue = findViewById(R.id.ButtonSignUpReportIssue);

        this.otp = "";

        buttonSignUpJoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    public void run() {
                        try {
                            signUp();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
            }
        });

        buttonSignUpGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread() {
                    public void run() {
                        try {

                            if (!inputCheck()) {
                                handler.sendEmptyMessage(2);
                            }

                            else if (new UserServiceImpl().hasUser(editTextSignUpEmail.getText().toString())){
                                showTextMessage("This email has been registered with us, please try another one.");
                            }

                            else if (!editTextSignUpEmail.getText().toString().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")){
                                showTextMessage("This is not a valid email address format.");

                            }

                            else if (!username.matches("\\w{1,10}")) {
                                showTextMessage("User name can only contain letters, numbers, and underscores, less than 10 characters");
                            }

                            else if (new UserServiceImpl().hasUser(editTextSignUpUserName.getText().toString())){
                                showTextMessage("This user name has been registered with us, please try another one.");
                            }

                            else{
                                otp = getOTP();
                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
            }
        });


        buttonSignUpReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    public void run() {
                        Intent intent = new Intent(SignUpActivity.this, ReportIssue.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });

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

    public void onPause() {
        super.onPause();
    }
    public void onResume() {
        super.onResume();
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    /**
     * get OTP for registration
     *
     * @return OTP
     * @throws Exception if happens
     */
    private String getOTP() throws Exception {

        showTextMessage("The OTP has been sent, please check your mail box.");

        Message counter = new Message();
        counter.what = 1;
        counter.obj = OTP_TIMER;
        handler.sendMessage(counter);

        String newOTP = String.valueOf(new MailServiceImpl().sendOTP(editTextSignUpEmail.getText().toString()));

        return newOTP;
    }

    /**
     * Sing up user
     *
     * @return true is sign up successfully, else false
     * @throws Exception if any exception happened
     */
    private boolean signUp() throws Exception {

        // Can not use input check, to ensure user cannot change the text field context after sending OTP.
        if (otp.isEmpty() || !otp.equals(editTextSingUpOTP.getText().toString())) {

            if (otp.isEmpty()) {
                showTextMessage("Signed up Failed! Please fill all fields.");
            } else {
                showTextMessage("Signed up Failed! OTP is incorrect.");
            }

            return false;
        }

        // @TODO: BACKEND: Here, you can integrate your backend logic to store the user details.
        try {

            System.out.println(faculty);

            // Add user to database
            new UserServiceImpl().addUser(username, email, password, faculty, aqfLevel);
            System.out.println("Signed up successfully!");
            otp = "";

            showTextMessage("Signed up successfully!");

            // Destroy the sign up page and jump to log in
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            System.out.println(e.getMessage());

            showTextMessage(e.getMessage());
        }

        otp = "-1";

        return false;
    }

    /**
     * Input check, make sure user input is not empty
     *
     * @return true if all fields input, otherwise false
     */
    private boolean inputCheck() {
//        name = ediTextSignUpName.getText().toString().trim();
        username = editTextSignUpUserName.getText().toString().trim();
        email = editTextSignUpEmail.getText().toString().trim();
        password = editTextSignUpPassword.getText().toString().trim();
        passwordConfirmation = editTextSignUpConfirmPassword.getText().toString().trim();

//        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
//            System.out.println("Please fill all fields");
//            showTextMessage("Please fill all fields");
//
//            return false;
//        }

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            System.out.println("Please fill all fields");
            showTextMessage("Please fill all fields");

            return false;
        }

        if (!password.equals(passwordConfirmation)) {
            System.out.println("Passwords do not match");
            showTextMessage("Passwords do not match");

            return false;
        }

        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters");
            showTextMessage("Password must be at least 8 characters");

            return false;
        }

        // Get the user degree
        Spinner facultySpinner = findViewById(R.id.SpinnerSignUpFacultyList);
        faculty = facultySpinner.getSelectedItem().toString();

        if (faculty.equals("Please select your faculty (optional)")) {
            faculty = "Not Provided";
        }

        Spinner degreeSpinner = findViewById(R.id.SpinnerSignUpDegreeList);
        String degree = degreeSpinner.getSelectedItem().toString();

        // Convert degree to AQF level
        System.out.println(degree);
        switch (degree) {
            case "Certificate I":
                aqfLevel = 1;
                break;
            case "Certificate II":
                aqfLevel = 2;
                break;
            case "Certificate III":
                aqfLevel = 3;
                break;
            case "Certificate IV":
                aqfLevel = 4;
                break;
            case "Diploma":
                aqfLevel = 5;
                break;
            case "Advanced Diploma, Associate Degree":
                aqfLevel = 6;
                break;
            case "Bachelor Degree":
                aqfLevel = 7;
                break;
            case "Bachelor Honours Degree":
                aqfLevel = 8;
                break;
            case "Masters Degree":
                aqfLevel = 9;
                break;
            case "Doctoral Degree":
                aqfLevel = 10;
                break;
            default:
                aqfLevel = 0;
        }


        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Show message text
     *
     * @param text as the showing message
     */
    private void showTextMessage(String text) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        handler.sendMessage(msg);
    }
}