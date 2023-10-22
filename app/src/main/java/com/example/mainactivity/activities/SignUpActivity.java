package com.example.mainactivity.activities;

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
import androidx.core.content.ContextCompat;

import com.example.mainactivity.R;
import com.example.mainactivity.service.mail.mailServiceImpl;
import com.example.mainactivity.service.user.UserServiceImpl;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final int OTP_TIMER = 20;
    private EditText editTextSignUpName;
    private EditText editTextSignUpUserName;
    private EditText editTextSignUpEmail;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpConfirmPassword;
    private EditText editTextSignUpOTP;
    private Spinner facultyList;

    private Spinner degreeList;

    private ArrayAdapter<CharSequence> facultyAdapter;

    private ArrayAdapter<CharSequence> degreeAdapter;

    private Button buttonOTP;
    private Button buttonSignUp;

    private TextView buttonRaiseIssue;

    private String otp;

    private String name;
    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;

    private String faculty;

    private int aqfLevel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextSignUpName = findViewById(R.id.EdiTextSignUpName);
        editTextSignUpUserName = findViewById(R.id.EditTextSignUpUserName);
        editTextSignUpEmail = findViewById(R.id.EditTextSignUpEmail);
        editTextSignUpPassword = findViewById(R.id.EditTextSignUpPassword);
        editTextSignUpConfirmPassword = findViewById(R.id.editTextSignUpConfirmPassword);
        editTextSignUpOTP = findViewById(R.id.EditTextSignUpResetPasswordOTP);
        facultyList = findViewById(R.id.spinnerFacultyList);
        degreeList = findViewById(R.id.spinnerDegreeList);

        // Set the faculty drop down list
        facultyAdapter = ArrayAdapter.createFromResource(this, R.array.faculty_list, android.R.layout.simple_spinner_item);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        facultyList.setAdapter(facultyAdapter);
        facultyList.setOnItemSelectedListener(this);

        // Set the degree drop down list
        degreeAdapter = ArrayAdapter.createFromResource(this, R.array.degree_list, android.R.layout.simple_spinner_item);
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        degreeList.setAdapter(degreeAdapter);
        degreeList.setOnItemSelectedListener(this);

        buttonOTP = findViewById(R.id.buttonSignUpGetOTP);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonRaiseIssue = findViewById(R.id.buttonSignUpReportIssue);

        this.otp = "";

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
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

        buttonOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    public void run() {
                        try {
                            otp = getOTP();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
            }
        });


        buttonRaiseIssue.setOnClickListener(new View.OnClickListener() {
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

    /**
     * get OTP for registration
     *
     * @return OTP
     * @throws Exception if happens
     */
    private String getOTP() throws Exception {

        handler.sendEmptyMessage(3);

        if (!inputCheck()) {

            showTextMessage("Please fill all fields.");
            handler.sendEmptyMessage(2);

            return "";
        } else {
            String newOTP = String.valueOf(new mailServiceImpl().sendOTP(editTextSignUpEmail.getText().toString().trim()));

            showTextMessage("The OTP has been sent, please check your mail box.");

            Message counter = new Message();
            counter.what = 1;
            counter.obj = OTP_TIMER;
            handler.sendMessage(counter);

            return newOTP;
        }
    }    @SuppressLint("HandlerLeak")
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
                        buttonOTP.setEnabled(false);
                        buttonOTP.setText(time + "s");
                        buttonOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.white));
                        buttonOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.grey));

                        Message message = new Message();
                        message.what = 1;
                        message.obj = time - 1;
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        editTextSignUpName.setEnabled(true);
                        editTextSignUpEmail.setEnabled(true);
                        editTextSignUpUserName.setEnabled(true);
                        editTextSignUpPassword.setEnabled(true);
                        editTextSignUpConfirmPassword.setEnabled(true);
                        facultyList.setEnabled(true);
                        degreeList.setEnabled(true);
                        buttonOTP.setEnabled(true);
                        buttonOTP.setText("Get OTP");
                        buttonOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.primary));
                    }

                    break;

                case 2:
                    editTextSignUpName.setEnabled(true);
                    editTextSignUpEmail.setEnabled(true);
                    editTextSignUpUserName.setEnabled(true);
                    editTextSignUpPassword.setEnabled(true);
                    editTextSignUpConfirmPassword.setEnabled(true);
                    facultyList.setEnabled(true);
                    degreeList.setEnabled(true);
                    buttonOTP.setEnabled(true);
                    buttonOTP.setText("Get OTP");
                    buttonOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.white));
                    buttonOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.primary));

                    break;

                case 3:
                    editTextSignUpName.setEnabled(false);
                    editTextSignUpEmail.setEnabled(false);
                    editTextSignUpUserName.setEnabled(false);
                    editTextSignUpPassword.setEnabled(false);
                    editTextSignUpConfirmPassword.setEnabled(false);
                    facultyList.setEnabled(false);
                    degreeList.setEnabled(false);
                    buttonOTP.setEnabled(false);
                    buttonOTP.setText("Sending");
                    buttonOTP.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this, R.color.grey));
                    buttonOTP.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.white));

                    break;
            }
        }
    };

    /**
     * Sing up user
     *
     * @return true is sign up successfully, else false
     * @throws Exception if any exception happened
     */
    private boolean signUp() throws Exception {

        // Can not use input check, to ensure user cannot change the text field context after sending OTP.
        if (otp.isEmpty() || !otp.equals(editTextSignUpOTP.getText().toString())) {

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
//            return true;
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            showTextMessage(e.getMessage());
        }
        return false;
    }

    /**
     * Input check, make sure user input is not empty
     *
     * @return true if all fields input, otherwise false
     */
    private boolean inputCheck() {
        name = editTextSignUpName.getText().toString().trim();
        username = editTextSignUpUserName.getText().toString().trim();
        email = editTextSignUpEmail.getText().toString().trim();
        password = editTextSignUpPassword.getText().toString().trim();
        passwordConfirmation = editTextSignUpConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
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
        Spinner facultySpinner = findViewById(R.id.spinnerFacultyList);
        faculty = facultySpinner.getSelectedItem().toString();

        if (faculty.equals("Please select your faculty (optional)")) {
            faculty = "Not Provided";
        }

        Spinner degreeSpinner = findViewById(R.id.spinnerDegreeList);
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