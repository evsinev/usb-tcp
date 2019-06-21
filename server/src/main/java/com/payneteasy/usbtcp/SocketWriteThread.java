package com.payneteasy.usbtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class SocketWriteThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(SocketWriteThread.class);

    private final Socket        socket;
    private final RunningState  runningState;
    private final UsbConnection usbConnection;

    public SocketWriteThread(Socket aSocket, RunningState aState, UsbConnection aUsbConnection) throws SocketException {
        socket = aSocket;
        runningState = aState;
        usbConnection = aUsbConnection;
    }

    @Override
    public void run() {
        try {
            OutputStream out = socket.getOutputStream();

            while (runningState.isRunning()) {
                byte[] read = usbConnection.read(1024);
                out.write(read);
            }
        } catch (Exception e) {
            LOG.error("IO exception while reading from socket {}", socket, e);
        } finally {
            closeSocket();
        }
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.error("Cannot close socket {}", socket, e);
        }
    }
}
