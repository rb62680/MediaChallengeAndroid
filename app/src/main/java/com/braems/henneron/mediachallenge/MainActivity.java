package com.braems.henneron.mediachallenge;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public String username;
    public String streamServerUrl;
    private MediaPlayer player;
    private Integer idOfRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set username */
        final TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        Intent intent = getIntent();
        this.username = intent.getStringExtra("EXTRA_USERNAME");
        usernameTextView.setText("Welcome " + this.username);


        // Creation of mediaplayer and controllers
        this.streamServerUrl = "http://192.168.70.62:62580";

        final Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // Set variables from the socketmanager
        this.idOfRoom = SocketManager.getInstance().idOfRoom;
        System.out.println("Id de la room:"+this.idOfRoom);

    }

}
