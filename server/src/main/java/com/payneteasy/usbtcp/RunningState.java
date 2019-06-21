package com.payneteasy.usbtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class RunningState {

    private static final Logger LOG = LoggerFactory.getLogger(RunningState.class);

    private AtomicBoolean state = new AtomicBoolean(true);

    public boolean isRunning() {
        return state.get() && !Thread.currentThread().isInterrupted();
    }

    public void stop() {
        LOG.info("Running state set to false");
        state.set(false);
    }
}
