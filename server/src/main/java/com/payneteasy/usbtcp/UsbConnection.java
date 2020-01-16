package com.payneteasy.usbtcp;

import com.payneteasy.tlv.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usb4java.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class UsbConnection implements Closeable {

    private static final Logger LOG     = LoggerFactory.getLogger(UsbConnection.class);

    private final Context      context;
    private final DeviceHandle handle;
    private final byte         endpointOut;
    private final byte         endpointIn;
    private final int          interfaceNumber;
    private final long         usbReadTimeoutMs;
    private final long         usbWriteTimeoutMs;

    public UsbConnection(UsbAddress aAddress, long aUsbReadTimeoutMs, long aUsbWriteTimeoutMs) throws IOException {
        context = new Context();
        endpointIn = aAddress.getEndpointIn();
        endpointOut = aAddress.getEndpointOut();
        interfaceNumber = aAddress.getInterfaceNumber();
        usbReadTimeoutMs = aUsbReadTimeoutMs;
        usbWriteTimeoutMs = aUsbWriteTimeoutMs;

        checkResult(LibUsb.init(context), "init");

        try {
            handle = checkNotNull(LibUsb.openDeviceWithVidPid(context, aAddress.getVendorId(), aAddress.getProductId()), "openDeviceWithVidPid()");
        } catch (Exception e) {
            LibUsb.exit(context);
            throw new IOException("Cannot invoke openDeviceWithVidPid()", e);
        }

        try {
            checkResult(LibUsb.claimInterface(handle, interfaceNumber), "claimInterface");
        } catch (Exception e) {
            LibUsb.close(handle);
            LibUsb.exit(context);
            throw new IOException("Cannot invoke claimInterface", e);
        }

    }

    public void write(byte[] data) {
        LOG.debug("Sending {} to usb ...", HexUtil.toHexString(data));

        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int       result      = LibUsb.bulkTransfer(handle, endpointOut , buffer, transferred, usbWriteTimeoutMs);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to send data", result);
        }
        LOG.debug("{} bytes sent to device", transferred.get());
    }

    public byte[] read(int size) {
        ByteBuffer buffer      = BufferUtils.allocateByteBuffer(size);
        IntBuffer  transferred = BufferUtils.allocateIntBuffer();
        int        result      = LibUsb.bulkTransfer(handle, endpointIn, buffer, transferred, usbReadTimeoutMs);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to read data", result);
        }

        byte[] bytes = new byte[transferred.get()];
        buffer.get(bytes);


        LOG.debug("Read {}", HexUtil.toFormattedHexString(bytes));
        return bytes;
    }


    private static void checkResult(int result, String aName) {
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


    @Override
    public void close() throws IOException {
        try {
            LOG.debug("Releasing interface {} ...", interfaceNumber);
            checkResult(LibUsb.releaseInterface(handle, interfaceNumber), "releaseInterface");
        } finally {
            LOG.debug("Closing device handle ...");
            LibUsb.close(handle);
            LOG.debug("Exiting from context ...");
            LibUsb.exit(context);
        }
    }
}
