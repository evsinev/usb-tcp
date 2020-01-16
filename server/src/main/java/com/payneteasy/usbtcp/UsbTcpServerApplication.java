package com.payneteasy.usbtcp;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class UsbTcpServerApplication {

    public static void main(String[] args) throws IOException {

        IStartupConfig   config    = getStartupParameters(IStartupConfig.class);
        UsbStartupConfig usbConfig = new UsbStartupConfig(config);

        RunningState runningState = new RunningState();
        UsbTcpServer server       = new UsbTcpServer(config.getTcpReadTimeoutMs(), config.getUsbReadTimeoutMs(), config.getUsWriteTimeoutMs());

        UsbAddress usbAddress = new UsbAddress.Builder()
                .vendorId        ( usbConfig.getVendorId()         )
                .productId       ( usbConfig.getProductId()        )
                .interfaceNumber ( usbConfig.getInterfaceNumber()  )
                .endpointOut     ( usbConfig.getEndpointOut()      )
                .endpointIn      ( usbConfig.getEndpointIn()       )
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            runningState.stop();
            server.stop();
        }));

        server.startAndWait(new InetSocketAddress(config.getTcpPort()), runningState, usbAddress);
    }
}
