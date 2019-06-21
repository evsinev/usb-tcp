package com.payneteasy.usbtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usb4java.Context;
import org.usb4java.LibUsb;

import java.nio.ByteBuffer;

public class EventHandlingThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(EventHandlingThread.class);

    private static final int TIMEOUT_NS = 3_000_000 ;

    private final Context context;

    public EventHandlingThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        while (!isInterrupted()) {

//            GetStatusApplication.check(LibUsb.handleEventsTimeoutCompleted(context, TIMEOUT_NS, buffer.asIntBuffer()), "handleEventsTimeoutCompleted");
            GetStatusApplication.check(LibUsb.handleEventsTimeout(context, TIMEOUT_NS), "handleEventsTimeout");
//            buffer.flip();
//            LOG.debug("Complete status is {} ", buffer);

//            buffer.rewind();
        }
        LOG.debug("Exiting ...");
    }
}
