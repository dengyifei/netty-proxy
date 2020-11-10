package com.efei.proxy.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class ReConnectEventPublisher<C extends ApplicationEvent> implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    public void  publish (C event){
        applicationEventPublisher.publishEvent(event);

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
