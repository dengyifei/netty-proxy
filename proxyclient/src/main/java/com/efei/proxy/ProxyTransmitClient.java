package com.efei.proxy;

import com.Client;
import com.efei.proxy.channelHandler.ProxyTcpDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ProxyTransmitClient extends Client {

    private ProxyTcpDecoder proxyTcpDecoder = ProxyTcpDecoder.getSelf();

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(proxyTcpDecoder);
            }
        };
    }

    @Override
    public void onConnectSuccess() {
    }

    @Override
    public void onClosed() {

    }

    public static void start(String[] args) throws InterruptedException {
        new ProxyTransmitClient().connect("127.0.0.1",8081);
    }
}
