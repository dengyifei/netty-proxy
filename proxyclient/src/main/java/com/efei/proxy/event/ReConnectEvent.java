package com.efei.proxy.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

public class ReConnectEvent extends ApplicationEvent {

    private int count;

    private long delay;

    private long period;


    public ReConnectEvent(Object source) {
        super(source);
    }

    public ReConnectEvent(Object source, int count,long delay,long period) {
        super(source);
        this.count = count;
        this.delay = delay;
        this.period = period;
    }

    public int getCount() {
        return count;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }
}
