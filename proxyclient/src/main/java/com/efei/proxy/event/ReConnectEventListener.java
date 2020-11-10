package com.efei.proxy.event;

import com.efei.proxy.ProxyMainClient;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ReConnectEventListener extends RetryTemplate implements ApplicationListener<ReConnectEvent>  {
    private static InternalLogger logger = InternalLoggerFactory.getInstance(ReConnectEventListener.class);

    @Autowired
    private ProxyMainClient proxyMainClient;
    @Override
    public void onApplicationEvent(ReConnectEvent event) {
        // System.out.println(String.format(">>>>>>>>>>>thread:%s,type:%s,event:%s", Thread.currentThread().getName(), event.getEventEnum(), event));
        setCount(event.getCount());
        setDelay(event.getDelay());
        setPeriod(event.getPeriod());
        execute();

    }

    @Override
    public Object doService() throws Exception{
        logger.warn("正在重新连接...");
        proxyMainClient.connect();
        return null;
    }
}
