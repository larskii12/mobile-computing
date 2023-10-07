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

        // Test button
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
                                try {
//                                    // Add an user
//                                    new User().addUser("username", "userEmail", "userPass", "faculty", 0);
//                                    System.out.println("Add successfully.");
//
//
//
//
//                                    // Delete an user
//                                    new User().deleteUser("userEmail");
//                                    System.out.println("delete successful.");
//
//
//
//
//                                    // Get an user information
//                                    User user = new User().getUser("hugo.garcia@example.com");
//                                    if (user == null) {
//                                        throw new Exception("Some error happened, please contact the IT administrator.");
//                                    }
//
//                                    else{
//                                        System.out.println("Query successful.");
//                                        System.out.println(user.getUserId());
//                                        System.out.println(user.getUserName());
//                                        System.out.println(user.getUserEmail());
//                                        System.out.println(user.getUserFaculty());
//                                        System.out.println(user.getUserAQFLevel());
//                                    }
//
//
//
//                                    // Update an user information


                                }

                                // If exception when operating
                                catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }

                            // If database is not reachable
                            else {
                                System.out.println("Database offline!");
                            }
                        }

                        // If exception when operating
                        catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.start();
            }
        });
    }
}