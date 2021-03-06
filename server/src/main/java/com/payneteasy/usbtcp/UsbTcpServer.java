package com.payneteasy.usbtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class UsbTcpServer {

    private static final Logger LOG = LoggerFactory.getLogger(UsbTcpServer.class);

    private final ServerSocket serverSocket;

    private final int          socketReadTimeoutMs;
    private final long         usbReadTimeoutMs;
    private final long         usbWriteTimeoutMs;


    public UsbTcpServer(int aSocketReadTimeoutMs, long aUsbReadTimeoutMs, long aUsbWriteTimeoutMs) throws IOException {
        socketReadTimeoutMs = aSocketReadTimeoutMs;
        usbReadTimeoutMs    = aUsbReadTimeoutMs;
        usbWriteTimeoutMs   = aUsbWriteTimeoutMs;

        serverSocket        = new ServerSocket();
    }

    public void startAndWait(SocketAddress aAddress, RunningState aState, UsbAddress aUsbAddress) throws IOException {
        LOG.debug("Binding on {}", aAddress);
        serverSocket.bind(aAddress);

        while (aState.isRunning()) {
            LOG.debug("Waiting for client connection ...");
            Socket socket = serverSocket.accept();
            try {
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(socketReadTimeoutMs);
                LOG.debug("Connected to {}", socket);

                try(UsbConnection usbConnection = new UsbConnection(aUsbAddress, usbReadTimeoutMs, usbWriteTimeoutMs)) {
                    SocketWriteThread writeThread = new SocketWriteThread(socket, aState, usbConnection);
                    writeThread.start();
                    SocketConnectionReadThread socketConnectionThread = new SocketConnectionReadThread(socket, aState, usbConnection);
                    socketConnectionThread.run();
                }
            } catch (Exception e) {
                LOG.error("Cannot process new connection", e);
            }

        }



    }

    public void stop() {
        try {
            LOG.debug("Closing server socket");
            serverSocket.close();
        } catch (IOException e) {
            LOG.error("Cannot close server socket", e);
        }
    }
}
