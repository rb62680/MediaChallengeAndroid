package com.braems.henneron.mediachallenge;

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

    // Here are the variables of activities that the socketmanager has to modify
    // MainActivity
    public Integer score;
    public Integer idOfRoom;

    private SocketManager(){
        this.serverIpAddress = "192.168.70.62";
        this.serverPort = 62300;
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
                this.idOfRoom = buffer.getInt();
                System.out.println("test");
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
                    System.out.println((char)opcode);
                    processMessage(message, (char) opcode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
