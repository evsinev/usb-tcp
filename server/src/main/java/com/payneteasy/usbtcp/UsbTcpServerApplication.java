package com.payneteasy.usbtcp;

import java.io.IOException;
import java.net.InetSocketAddress;

public class UsbTcpServerApplication {

    public static void main(String[] args) throws IOException {

        RunningState runningState = new RunningState();
        UsbTcpServer server       = new UsbTcpServer();
        UsbAddress usbAddress = new UsbAddress.Builder()
                .vendorId(0x0dd4)
                .productId(0x015d)
                .interfaceNumber(0)
                .endpointOut(2)
                .endpointIn(0x81)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            runningState.stop();
            server.stop();
        }));

        server.startAndWait(new InetSocketAddress(8090), runningState, usbAddress);
    }
}
