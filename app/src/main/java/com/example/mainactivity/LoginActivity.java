package com.example.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
                loginUser();
            }
        });

        buttonGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // @TODO: Here, you can integrate your backend logic to authenticate the user and validate inputs.
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
    }
}