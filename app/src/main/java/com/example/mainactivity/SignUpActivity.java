package com.example.mainactivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.service.otp.OTPServiceImpl;
import com.example.mainactivity.service.user.UserServiceImpl;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName;

    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    private EditText editTextOTP;

    private Button buttonSignUp;

    private Button buttonOTP;

    private String otp;


    private String name;
    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editName);
        editTextUserName = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextOTP = findViewById(R.id.editTextOTP);
        buttonOTP = findViewById(R.id.buttonGetOTP);
        buttonSignUp = findViewById(R.id.buttonSignUp);

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

    }

    /**
     * get OTP for registration
     * @return OTP
     * @throws Exception if happens
     */
    private String getOTP() throws Exception {

        if (editTextEmail.getText().toString().trim().isEmpty() || !inputCheck()){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }

        else {
            String newOTP = String.valueOf(new OTPServiceImpl().sendOTP(editTextEmail.getText().toString().trim()));

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "The OTP has been sent, please check your mail box.", Toast.LENGTH_SHORT).show();
                }
            });

            return newOTP;
        }
    }


    /**
     * Sing up user
     * @return true is sign up successfully, else false
     * @throws Exception if any exception happened
     */
    private boolean signUp() throws Exception {

        // Can not use input check, to ensure user cannot change the text field context after sending OTP.
        if (otp.isEmpty() || name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty() || !otp.equals(editTextOTP.getText().toString())){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (otp.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Signed up Failed! Please fill all fields.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Signed up Failed! OTP is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return false;
        }

        // @TODO: BACKEND: Here, you can integrate your backend logic to store the user details.
        try {

            // Add user to database
            new UserServiceImpl().addUser(username, email, password, "Test", 9);
            System.out.println("Signed up successfully!");
            otp = "";

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Signed up successfully!", Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Signed up failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
    }

    /**
     * Input check, make sure user input is not empty
     * @return true if all fields input, otherwise false
     */
    private boolean inputCheck() {
        name = editTextName.getText().toString().trim();
        username = editTextUserName.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        passwordConfirmation = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            System.out.println("Please fill all fields");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            });

            return false;
        }

        if (!password.equals(passwordConfirmation)) {
            System.out.println("Passwords do not match");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            });

            return false;
        }

        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }

        return true;
    }
}
