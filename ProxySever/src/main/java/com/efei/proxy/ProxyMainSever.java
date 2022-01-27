package com.efei.proxy;

import com.efei.proxy.config.ProxyConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 启动服务
 */
@Configuration
@Import(ProxyConfig.class)
public class ProxyMainSever {

    private static  InternalLogger logger = InternalLoggerFactory.getInstance(ProxyMainSever.class);
//    public static void main(String[] args)  {
//        Thread porxyHttpServerThread = new Thread(()->{
//            try {
//                ProxyHttpServer.start(args);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        },"PorxyHttpServer");
//
//        Thread proxyTransmitServer = new Thread(()->{
//            try {
//                ProxyTransmitServer.start(args);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        },"proxyTransmitServer");
//
//        porxyHttpServerThread.start();
//        proxyTransmitServer.start();
//
//    }

    private static ApplicationContext applicationContext;

    public static void main( String[] args )
    {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.efei.proxy");
        applicationContext = context;

        ProxyMainSever app = context.getBean(ProxyMainSever.class);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            logger.warn("程序退出");
            context.close();
        }));
        app.start();
    }

    public void start(){
        // 数据传输服务
        ProxyTransmitServer proxyTransmitServer = applicationContext.getBean(ProxyTransmitServer.class);
        new Thread(()->{
            try {
                proxyTransmitServer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"proxyTransmitServer").start();


        ProxyHttpServer proxyHttpServer = applicationContext.getBean(ProxyHttpServer.class);
        // http代理服务
        new Thread(()->{
            try {
                proxyHttpServer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"PorxyHttpServer").start();

        // TCP转发服务
        ProxyTcpServerManager proxyTcpServerManager = applicationContext.getBean(ProxyTcpServerManager.class);
        proxyTcpServerManager.start();

    }

    public void stop(){
        ProxyTransmitServer proxyTransmitServer = applicationContext.getBean(ProxyTransmitServer.class);
        proxyTransmitServer.stop();
        ProxyHttpServer proxyHttpServer = applicationContext.getBean(ProxyHttpServer.class);
        proxyHttpServer.stop();
    }
}
