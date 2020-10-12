package com.efei.proxy;

import com.Server;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


/**
 * 代理转发服务，一般转发给客户端
 */
public class ProxyTransmitServer extends Server{

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

            }
        };
    }

    public static void start(String[] args) throws InterruptedException {
        new ProxyTransmitServer().start(5000);
    }
}
