package com.efei.proxy;

/**
 * 启动服务
 */
public class ProxyMainSever {
    public static void main(String[] args)  {
        Thread porxyHttpServerThread = new Thread(()->{
            try {
                ProxyHttpServer.start(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"PorxyHttpServer");

        Thread proxyTransmitServer = new Thread(()->{
            try {
                ProxyTransmitServer.start(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"proxyTransmitServer");

        porxyHttpServerThread.start();
        proxyTransmitServer.start();

    }
}
