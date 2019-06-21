package com.payneteasy.usbtcp;

import com.payneteasy.tlv.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class GetStatusApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GetStatusApplication.class);

    public static final short VENDOR_ID    = (short) 0x0dd4;
    public static final short PRODUCT_ID   = (short) 0x015d;
    public static final int   INTERFACE    = 0;
    public static final byte  ENDPOINT_OUT = 2;
    public static final byte  ENDPOINT_IN  = (byte) 0x81;

    /**
     * The communication timeout in milliseconds.
     */
    private static final int TIMEOUT_MS = 1_000;

    public static void main(String[] args) throws InterruptedException {
        Context context = new Context();
        check(LibUsb.init(context), "init");
        try {
            DeviceHandle handle = checkNotNull(LibUsb.openDeviceWithVidPid(context, VENDOR_ID, PRODUCT_ID), "handle");
            try {

//                EventHandlingThread eventThread = new EventHandlingThread(context);
//                eventThread.start();

                check(LibUsb.claimInterface(handle, INTERFACE), "claimInterface");
                try {

//                    writeControl(handle, 0);
//                    writeControlString(handle, 3);

//                    readBytes(handle, 10, "Empty read {}");
//                    readBytes(handle, 10, "Empty read {}");

                    write(handle, HexUtil.parseHex("1D 65 02")); // eject
                    write(handle, "hello \nhello 2\n".getBytes());
                    write(handle, HexUtil.parseHex("1B 69"));  // cut
                    write(handle, HexUtil.parseHex("1D 65 02")); // eject

                    paperLength(handle);
                    
                    for(int i=0; i<10; i++) {
                        getStatus(handle);

//                        LOG.debug("Sleeping 1 seconds ...");
                        Thread.sleep(500);
                    }

//                    eventThread.interrupt();

                    LOG.debug("Sleeping 2 seconds ...");
                    Thread.sleep(2_000);

                } finally {
                    check(LibUsb.releaseInterface(handle, INTERFACE), "releaseInterface");
                }
            } finally {
                LibUsb.close(handle);
            }

        } finally {
            LibUsb.exit(context);
        }

    }

    private static void getStatus(DeviceHandle handle) {
        write(handle, HexUtil.parseHex("10 04 14")); // status
//                    transfer(context, handle);
//                    LOG.debug("Sleeping 7 seconds ...");
//                    Thread.sleep(7_000);

        readBytes(handle, 10, "Read {}");
    }

    private static void readBytes(DeviceHandle handle, int i, String s) {
        ByteBuffer read = read(handle, i);
//                    read.rewind();

        byte[] bytes = new byte[6];
        read.get(bytes);
        LOG.debug(s, HexUtil.toFormattedHexString(bytes));
    }

    private static void paperLength(DeviceHandle handle) {
        write(handle, HexUtil.parseHex("1D E3")); // paper length

        readBytes(handle, 12, "Paper length {}");
    }

    public static ByteBuffer read(DeviceHandle handle, int size) {
        ByteBuffer buffer      = BufferUtils.allocateByteBuffer(size); //.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer  transferred = BufferUtils.allocateIntBuffer();
        int        result      = LibUsb.bulkTransfer(handle, ENDPOINT_IN, buffer, transferred, 10 * TIMEOUT_MS);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to read data", result);
        }
        System.out.println(transferred.get() + " bytes read from device");
        return buffer;
    }

    public static void write(DeviceHandle handle, byte[] data) {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int       result      = LibUsb.bulkTransfer(handle, ENDPOINT_OUT, buffer, transferred, TIMEOUT_MS);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to send data", result);
        }
        System.out.println(transferred.get() + " bytes sent to device");
    }

    public static void writeControl(DeviceHandle handle, int aIndex) {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(255);
        int       result      = LibUsb.getDescriptor(
                handle
                , (byte)0x03
                , (byte)aIndex
                , buffer
        );

        LOG.debug("control count {}", result);
    }

    public static void writeControlString(DeviceHandle handle, int aIndex) {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(255);
        int       result      = LibUsb.getStringDescriptor(
                handle
                , (byte)aIndex
                , (short) 0x0409
                , buffer
        );

        LOG.debug("control count {}", result);
    }

    private static void transfer(Context aContext, DeviceHandle aHandle) {
        byte[]     data   = HexUtil.parseHex("10 04 20"); // full status
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        Transfer transfer = LibUsb.allocTransfer();
        LibUsb.fillBulkTransfer(transfer, aHandle, ENDPOINT_OUT, buffer,
                new TransferCallback() {
                    @Override
                    public void processTransfer(Transfer transfer) {
                        LOG.debug("Process Transfer {}", transfer);
                        LibUsb.freeTransfer(transfer);
                    }
                }, null, TIMEOUT_MS);

        check(LibUsb.submitTransfer(transfer), "submitTransfer");

    }


    public static void check(int result, String aName) {
        LOG.debug("Checking result for {} ...", aName);
        if (result < 0) {
            throw new LibUsbException("Unable to " + aName, result);
        }
    }

    private static <T> T checkNotNull(T aObject, String aMessage) {
        LOG.debug("Checking not null for {} ...", aMessage);
        if (aObject == null) {
            throw new NullPointerException(aMessage + " is null");
        }
        return aObject;
    }
}
