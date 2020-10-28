package com.efei.proxy;

import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(ProxyConfig.class)
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

    private static InternalLogger logger = InternalLoggerFactory.getInstance(ProxyMainClient.class);
    private  static  transient ApplicationContext applicationContext;

    @Autowired
    private ProxyConfig.ProxyTransmitClientConfig proxyTransmitClientConfig;

    @Autowired
    private ProxyConfig.ProxyHttpClientConfig proxyHttpClientConfig;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.efei.proxy");
        applicationContext = context;

        ProxyMainClient app = context.getBean(ProxyMainClient.class);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            logger.warn("程序退出");
            context.close();
        }));
        app.start();
    }

    public void start(){
        try {
            ProxyTransmitClient proxyTransmitClient = SpringConfigTool.getBean(ProxyTransmitClient.class);
            proxyTransmitClient.connect(proxyTransmitClientConfig.getHost(),proxyTransmitClientConfig.getPort());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
