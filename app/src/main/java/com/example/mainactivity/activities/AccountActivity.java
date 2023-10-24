//package com.example.mainactivity.activities;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.LinearLayout;
//
//public class AccountActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.your_xml_layout_name);
//
//        // Personal Information LinearLayout
//        LinearLayout personalInfoLayout = findViewById(R.id.Account_Personal_Info_Icon).getParent();
//        personalInfoLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AccountActivity.this, PersonalInformationActivity.class);
//                startActivity(intent);
//            }
//        });
//
//}
