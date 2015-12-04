package com.braems.henneron.mediachallenge;

import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class LoginActivity extends AppCompatActivity {

    private boolean connected = false;
    private String serverIpAddress;
    private Integer serverPort;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameView = (EditText) findViewById(R.id.username);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!connected) {
                    serverIpAddress = "192.168.202.108";
                    serverPort = 62300;
                    username = usernameView.getText().toString();
                    Thread thread = new Thread(new ClientThread());
                    thread.start();
                }
            }
        });
    }

    public class ClientThread implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                System.out.println("Connexion");
                Socket socket = new Socket(serverAddr, serverPort);
                connected = true;
                while (connected) {
                    try {
                        System.out.println("C: Sending command.");
                        BufferedOutputStream bo = new BufferedOutputStream(socket.getOutputStream());
                        short length = (short)username.length();
                        byte[] lengthToByteArray = new byte[2];
                        lengthToByteArray[0] = (byte) (length);
                        lengthToByteArray[1] = (byte) ((length >> 8) & 0xff);
                        bo.write(lengthToByteArray);

                        char opcode = 1;
                        bo.write((byte)opcode);
                        bo.write(username.getBytes());
                        bo.flush();


                        Thread.sleep(2*1000);
                        System.out.println("C: Sent.");
                    } catch (Exception e) {
                        System.out.println("S: Error");
                    }
                }
                socket.close();
                System.out.println("ClientActivity C: Closed");
            } catch (Exception e) {
                System.out.println("ClientActivity C: Error");
                connected = false;
            }
        }
    }
}

