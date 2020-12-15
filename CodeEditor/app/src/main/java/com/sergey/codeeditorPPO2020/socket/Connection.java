package com.sergey.codeeditorPPO2020.socket;

import android.util.Log;

import com.sergey.codeeditorPPO2020.models.RunMetaInfo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    public Socket getSocket() {
        return mSocket;
    }

    private Socket mSocket = null;
    private String mHost;
    private int mPort;

    public static final String LOG_TAG = "SOCKET";

    public Connection (final String host, final int port) {
        this.mHost = host;
        this.mPort = port;
    }

    public void openConnection() throws Exception {
        closeConnection();

        try {
            mSocket = new Socket(mHost, mPort);
        } catch (IOException e) {
            throw new Exception("Error create socket " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error for close socket :" + e.getMessage());
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }

    public void sendData(final RunMetaInfo data) throws Exception {
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Socket not create or close");
        }

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());

            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new Exception("Error sent data: " + e.getMessage());
        }
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }
}
