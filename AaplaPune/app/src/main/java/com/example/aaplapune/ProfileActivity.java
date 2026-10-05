package com.example.aaplapune;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        backButton =
                findViewById(R.id.profileBackButton);

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}