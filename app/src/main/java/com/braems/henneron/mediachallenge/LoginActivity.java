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
                short length = (short)username.length();
                byte[] lengthToByteArray = new byte[2];
                lengthToByteArray[0] = (byte) (length);
                lengthToByteArray[1] = (byte) ((length >> 8) & 0xff);
                char opcode = 1;
                SocketManager.getInstance().connect();
                SocketManager.getInstance().sendMessage(lengthToByteArray, (byte)opcode, username.getBytes());

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("EXTRA_USERNAME", username);
                startActivity(intent);
            }
        });
    }

    /*public class ClientThread implements Runnable {
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
    }*/
}

