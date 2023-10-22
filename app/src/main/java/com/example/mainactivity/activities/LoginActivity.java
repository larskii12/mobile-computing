package com.example.mainactivity.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.R;
import com.example.mainactivity.models.user.User;
import com.example.mainactivity.service.user.UserServiceImpl;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private Button buttonLoginLogIn;
    private TextView buttonLogInGoToSignup;

    private TextView buttonLogInForgetPassword;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.EditTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.EditTextLoginPassword);
        buttonLoginLogIn = findViewById(R.id.ButtonLoginLogIn);
        buttonLogInGoToSignup = findViewById(R.id.ButtonLogInGoToSignup);
        buttonLogInForgetPassword = findViewById(R.id.ButtonLogInForgetPassword);

        buttonLoginLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            loginUser();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
            }
        });

        buttonLogInGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {

                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });

        buttonLogInForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });
    }



    private int loginUser() throws Exception {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {

            showTextMessage("Please fill all fields.");
            return -1;
        }

        // @TODO: Here, you can integrate your backend logic to authenticate the user and validate inputs.
        User logInUser = new UserServiceImpl().logIn(email, password);

        if (logInUser == null){

            showTextMessage("Your input does not match our records, please try again.");
        }

        else{
            System.out.println("User login successful");
            System.out.println("User ID: " + logInUser.getUserId());
            System.out.println("User Name: " + logInUser.getUserName());
            System.out.println("User Email: " + logInUser.getUserEmail());
            System.out.println("User Faculty: " + logInUser.getUserFaculty());
            System.out.println("User AQF level: " + logInUser.getUserAQFLevel());

            // Show login successful message
            String message = logInUser.getUserId() + " " + logInUser.getUserName()
                    + " " + logInUser.getUserEmail()
                    + " " + logInUser.getUserFaculty()
                    + " " + logInUser.getUserAQFLevel();
            showTextMessage("Login successfully!\n" + message);

        }

        return 0;
    }

    /**
     * Show message text
     * @param text as the showing message
     */
    private void showTextMessage(String text){
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        handler.sendMessage(msg);
    }
}