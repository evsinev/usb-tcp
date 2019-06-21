package com.payneteasy.usbtcp;

import com.payneteasy.tlv.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GetStatusUsbConnectionApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GetStatusUsbConnectionApplication.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        UsbAddress usbAddress = new UsbAddress.Builder()
                .vendorId(0x0dd4)
                .productId(0x015d)
                .interfaceNumber(0)
                .endpointOut(2)
                .endpointIn(0x81)
                .build();

        try (UsbConnection connection = new UsbConnection(usbAddress)) {
            connection.write(HexUtil.parseHex("1D 65 02")); // eject
            connection.write("hello \nhello 2\n".getBytes());
            connection.write(HexUtil.parseHex("1B 69"));  // cut
            connection.write(HexUtil.parseHex("1D 65 02")); // eject

            connection.write(HexUtil.parseHex("10 04 14")); // status

            byte[] read = connection.read(1024);
            LOG.debug("Read {}", HexUtil.toHexString(read));
            LOG.debug("Sleeping 2 seconds ...");
            Thread.sleep(2_000);
        }
    }

}
