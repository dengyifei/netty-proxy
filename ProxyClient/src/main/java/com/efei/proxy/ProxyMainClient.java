package com.efei.proxy;

import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ProxyConfig;
import com.efei.proxy.config.ProxyHttpClientConfig;
import com.efei.proxy.config.ProxyTransmitClientConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Timer;

/**
 * -server -Xms500m -Xmx500m -XX:NewRatio=5 -XX:SurvivorRatio=8 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:log/gc.log -XX:+UseConcMarkSweepGC -DdomainUser=xefei -Dhost=127.0.0.1 -Dport=8788
 */
@Component
@Slf4j
public class ProxyMainClient {
//    public static void main(String[] args) {
//        int i = SystemPropertyUtil.getInt(
//                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2);
//        System.out.println(i);
//        try {
//            System.out.println("ssssss".getBytes("GBK").length);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        try {
//            ProxyTransmitClient.start(args);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            ProxyTransmitClient.start(args);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    private static  ApplicationContext applicationContext;

    @Autowired
    private ProxyTransmitClientConfig proxyTransmitClientConfig;

    @Autowired
    private ProxyHttpClientConfig proxyHttpClientConfig;

    @Autowired
    private ProxyTransmitClient proxyTransmitClient;

    @Autowired
    private Timer timer;

//    @Autowired
//    private ReConnectEventPublisher eventPublisher;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.efei.proxy");
        applicationContext = context;

        ProxyMainClient app = context.getBean(ProxyMainClient.class);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            log.info("程序退出");
            context.close();
        }));
        app.start();
    }

    public void start(){
        // 初始化配置 -DdomainUser=xefei -Dhost=127.0.0.1 -Dport=8788
        String domainUser = System.getProperty("domainUser");
        String host = System.getProperty("host");
        String port = System.getProperty("port");
        if(!StringUtils.isEmpty(domainUser)){
            proxyTransmitClientConfig.setLoginName(domainUser);
        }
        if(!StringUtils.isEmpty(host)){
            proxyHttpClientConfig.setHost(host);
        }
        if(!StringUtils.isEmpty(port)){
            proxyHttpClientConfig.setPort(Integer.valueOf(port));
        }
        proxyTransmitClient.bulidBootstrap(1);
        proxyTransmitClient.doConnect(proxyTransmitClientConfig.getHost(),proxyTransmitClientConfig.getPort());
        // 监控缓存
//        timer.schedule(new TimerTask(){
//            @Override
//            public void run() {
//                logger.info("Cache size == {}",Cache.size());
//                for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
//                    logger.info( " {} 总量: {} 使用的内存: {}", memoryPoolMXBean.getName(),memoryPoolMXBean.getUsage().getCommitted(),memoryPoolMXBean.getUsage().getUsed());
//                }
//            }
//        },0,60000);

    }

    public static void shutdown(){
        AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext)applicationContext;
        context.close();
    }
}
