package com.efei.proxy;

import com.efei.proxy.config.ProxyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * 启动服务
 */

@Component
public class ProxyMainSever {

    private static Logger logger = LoggerFactory.getLogger(ProxyMainSever.class);

    @Autowired
    private ProxyTransmitServer proxyTransmitServer;

    @Autowired
    private ProxyHttpServer proxyHttpServer;

    @Autowired
    private ProxyTcpServerManager proxyTcpServerManager;

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

        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            logger.info("程序退出");
            context.close();
        }));
        context.getBean(ProxyMainSever.class).start();
    }

    public void start(){
        // 数据传输服务
        new Thread(()->{
            try {
                proxyTransmitServer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"proxyTransmitServer").start();

        // http代理服务
        new Thread(()->{
            try {
                proxyHttpServer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"PorxyHttpServer").start();

        // TCP转发服务
        proxyTcpServerManager.start();

    }

    public void stop(){
        proxyHttpServer.stop();
        proxyTransmitServer.stop();
    }
}
