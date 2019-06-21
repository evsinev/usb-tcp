package com.payneteasy.usbtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class SocketConnectionReadThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(SocketConnectionReadThread.class);

    private final Socket        socket;
    private final RunningState  runningState;
    private final UsbConnection usbConnection;

    public SocketConnectionReadThread(Socket aSocket, RunningState aState, UsbConnection aUsbConnection) throws SocketException {
        socket = aSocket;
        runningState = aState;
        usbConnection = aUsbConnection;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();

            byte[] buffer = new byte[1024 * 8];

            while (runningState.isRunning()) {
                int count = inputStream.read(buffer);
                if (count < 0) {
                    break;
                }
                usbConnection.write(trimBuffer(buffer, count));
            }
        } catch (Exception e) {
            LOG.error("IO exception while reading from socket {}", socket, e);
        } finally {
            closeSocket();
        }
    }

    private byte[] trimBuffer(byte[] buffer, int count) {
        byte[] ret = new byte[count];
        System.arraycopy(buffer, 0, ret, 0, count);
        return ret;
    }

    private void closeSocket() {
        try {
            LOG.debug("Closing socket {} ...", socket);
            socket.close();
        } catch (IOException e) {
            LOG.error("Cannot close socket {}", socket, e);
        }
    }
}
