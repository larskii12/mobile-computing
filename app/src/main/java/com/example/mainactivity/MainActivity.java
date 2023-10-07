package com.example.mainactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        // 设置按钮监听器
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            DatabaseHelper db = new DatabaseHelper();
                            if (db.databaseConnectionTest()) {
                                System.out.println("Database online!");
                                System.out.println(db.getAllUsers());
                            } else {
                                System.out.println("Database offline!");
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.start();
            }
        });
    }
}