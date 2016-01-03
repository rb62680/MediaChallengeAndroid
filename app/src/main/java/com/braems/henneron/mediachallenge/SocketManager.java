package com.braems.henneron.mediachallenge;

import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;


public class SocketManager {

    private static SocketManager mInstance = null;
    private String serverIpAddress;
    private int serverPort;
    private Socket socket;
    private BufferedOutputStream bo;
    private MainActivity mActivity;
    private LoginActivity lActivity;

    private SocketManager(){
        this.serverIpAddress = "192.168.70.62";
        this.serverPort = 62300;
    }

    public void setmActivity(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    public MainActivity getmActivity() {
        return mActivity;
    }

    public LoginActivity getlActivity() {
        return lActivity;
    }

    public void setlActivity(LoginActivity lActivity) {
        this.lActivity = lActivity;
    }

    public void connect()
    {
        Thread t = new Thread(new ClientThread());
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t2 = new Thread(new Listener());
        t2.start();
    }

    public static SocketManager getInstance(){
        if(mInstance == null)
            mInstance = new SocketManager();

        return mInstance;
    }

    public synchronized void sendMessage(final byte opcode, final byte[] message) {
        Thread t = new Thread(){
            @Override
            public void run() {
                short length = (short)message.length;
                byte[] lengthToByteArray = new byte[2];
                lengthToByteArray[0] = (byte) (length);
                lengthToByteArray[1] = (byte) ((length >> 8) & 0xff);
                try {
                    bo.write(lengthToByteArray);
                    bo.write(opcode);
                    bo.write(message);
                    bo.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }

    public void processMessage(byte[] message, char opcode) {
        switch (opcode) {
            // Set id of the room which has been affected by the server
            case 2:
                ByteBuffer buffer = ByteBuffer.wrap(message);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                mActivity.setIdOfRoom(buffer.getInt());
                break;
            // Reset song
            case 3:
                if(mActivity != null && mActivity.player != null)
                {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (mActivity.player.isPlaying()) {
                                mActivity.player.stop();
                                mActivity.play.setEnabled(true);
                            }
                        }
                    });
                }
                break;
            // Put message in chatbox from MainActivity
            case 5:
                String messageToString = new String(message, Charset.forName("UTF8"));
                mActivity.getMessages().add(messageToString);
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mActivity.displayMessages();
                    }
                });
                break;
        }
    }

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                socket  = new Socket(serverAddr, serverPort);
                bo = new BufferedOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Listener implements Runnable
    {
        private BufferedInputStream reader;

        public Listener() {
            try {
                this.reader =  new BufferedInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if(mActivity != null) {
                        // Read the length and the opcode
                        byte[] buff = new byte[3];
                        this.reader.read(buff, 0, 3);
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buff, 0, 3);
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                        short length = byteBuffer.getShort();
                        byte opcode = byteBuffer.get();

                        // Read the message from the length
                        byte[] message = new byte[length];
                        this.reader.read(message, 0, length);
                        processMessage(message, (char) opcode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
