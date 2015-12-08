package com.braems.henneron.mediachallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);

        Intent intent = getIntent();
        this.username = intent.getStringExtra("EXTRA_USERNAME");

        usernameTextView.setText("Bienvenue "+this.username);
    }
}
