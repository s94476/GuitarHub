package com.example.guitarhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstLogin = prefs.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstLogin", false);
            editor.apply();

            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
            finish();
        }
    }
}