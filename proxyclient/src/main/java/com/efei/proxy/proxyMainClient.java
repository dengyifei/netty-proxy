package com.efei.proxy;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

import java.io.UnsupportedEncodingException;

public class proxyMainClient {
    public static void main(String[] args) {
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
        try {
            ProxyTransmitClient.start(args);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
