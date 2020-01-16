package com.payneteasy.usbtcp;

import com.payneteasy.tlv.HexUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestScannerApplication {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 2589));
        try {
            byte[] command = HexUtil.parseHex("0x16 0x54 0x0D".replace("0x", ""));
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            out.write(command);
            byte[] buf = new byte[1024];
            int count;
            while( ( count = in.read(buf)) >= 0) {
                System.out.println("input: " + HexUtil.toHexString(buf, 0, count) + " " + new String(buf, 0, count));
            }
        } finally {
            socket.close();
        }
    }
}


//             byte[] command = HexUtil.parseHex("0x16 0x4D 0x0D 0x25 0x25 0x25 0x56 0x45 0x52 0x2E".replace("0x", ""));