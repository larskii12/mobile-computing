package com.example.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.models.location.Location;
import com.example.mainactivity.models.user.User;
import com.example.mainactivity.service.location.LocationServiceImpl;
import com.example.mainactivity.service.user.UserServiceImpl;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private TextView buttonGoToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToSignup = findViewById(R.id.buttonGoToSignup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
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

        buttonGoToSignup.setOnClickListener(new View.OnClickListener() {
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
    }

    private int loginUser() throws Exception {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            });

            return -1;
        }

        // @TODO: Here, you can integrate your backend logic to authenticate the user and validate inputs.
        User logInUser = new UserServiceImpl().logIn(email, password);
        List<Location> results = new LocationServiceImpl().findAllLocations("STUDY", "ERC", false);
        System.out.println(results.get(0).getName());

        if (logInUser == null){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Your input does not match our records, please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else{
            System.out.println("User login successful");
            System.out.println("User ID: " + logInUser.getUserId());
            System.out.println("User Name: " + logInUser.getUserName());
            System.out.println("User Email: " + logInUser.getUserEmail());
            System.out.println("User Faculty: " + logInUser.getUserFaculty());
            System.out.println("User AQF level: " + logInUser.getUserAQFLevel());

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String message = logInUser.getUserId() + " " + logInUser.getUserName()
                            + " " + logInUser.getUserEmail()
                            + " " + logInUser.getUserFaculty()
                            + " " + logInUser.getUserAQFLevel();
                    Toast.makeText(getApplicationContext(), "Login successfully!\n" + message, Toast.LENGTH_SHORT).show();
                }
            });

        }

        return 0;
    }
}