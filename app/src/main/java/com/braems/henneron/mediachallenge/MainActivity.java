package com.braems.henneron.mediachallenge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public String username;
    public String streamServerUrl;
    public MediaPlayer player;
    public Button play;
    private Integer idOfRoom;
    private Integer score;
    private ListView answerListView;
    private List<String> messages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocketManager.getInstance().setmActivity(this);

        /* Set username */
        final TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        Intent intent = getIntent();
        this.username = intent.getStringExtra("EXTRA_USERNAME");
        usernameTextView.setText("Welcome " + this.username);

        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creation of mediaplayer and controllers
                streamServerUrl = "http://192.168.70.62:62580/" + idOfRoom;

                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(streamServerUrl);
                    player.prepare();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                player.start();
                play.setEnabled(false);
            }
        });

        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.stop();
                    play.setEnabled(true);
                }
            }
        });

        final Button answerButton = (Button) findViewById(R.id.answerButton);
        final EditText answerEditText = (EditText) findViewById(R.id.answerEditText);
        this.answerListView = (ListView) findViewById(R.id.answerListView);

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide keyboard
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String message = answerEditText.getText().toString();
                answerEditText.setText("");
                byte opcode = 4;
                SocketManager.getInstance().sendMessage(opcode, message.getBytes());
            }
        });

        displayMessages();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getIdOfRoom() {
        return idOfRoom;
    }

    public void setIdOfRoom(Integer idOfRoom) {
        this.idOfRoom = idOfRoom;
    }


    public void displayMessages() {
        ArrayAdapter<String> ad = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, messages);
        answerListView.setAdapter(ad);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
