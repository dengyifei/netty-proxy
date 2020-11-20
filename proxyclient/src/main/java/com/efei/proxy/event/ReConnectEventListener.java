package com.efei.proxy.event;

import com.efei.proxy.ProxyMainClient;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ReConnectEventListener extends RetryTemplate implements ApplicationListener<ReConnectEvent>  {
    private static InternalLogger logger = InternalLoggerFactory.getInstance(ReConnectEventListener.class);

//    ExecutorService executorService = new ThreadPoolExecutor(3, 5,
//            1, TimeUnit.MINUTES,
//            new LinkedBlockingQueue<Runnable>());
    public final static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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
        return null;
    }
}
