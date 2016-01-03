package com.braems.henneron.mediachallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameView = (EditText) findViewById(R.id.usernameTextView);
        final Button loginButton = (Button) findViewById(R.id.loginButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = usernameView.getText().toString();
                char opcode = 1;

                SocketManager.getInstance().connect();
                SocketManager.getInstance().sendMessage((byte) opcode, username.getBytes());

                startGame();
            }
        });
    }

    public void startGame(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("EXTRA_USERNAME", username);

        startActivity(intent);
    }
}

