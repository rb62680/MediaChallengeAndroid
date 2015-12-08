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
    public Socket socket;
    public BufferedOutputStream bo;

    private SocketManager(){
        this.serverIpAddress = "192.168.70.183";
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

    public synchronized void sendMessage(final byte[] length, final byte opcode, final byte[] message) {
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    bo.write(length);
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

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                socket  = new Socket(serverAddr, serverPort);
                bo = new BufferedOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                System.out.println(e.toString());
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
                System.out.println(e.toString());
            }
        }

        @Override
        public void run(){
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] buff = new byte[3];
                    this.reader.read(buff, 0, 3);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buff, 0, 3);
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    short length = byteBuffer.getShort();
                    byte opcode = byteBuffer.get();

                    byte[] message = new byte[length];
                    this.reader.read(message, 0, length);
                    String v = new String(message, Charset.forName("UTF-8"));
                    System.out.println(v);
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}
