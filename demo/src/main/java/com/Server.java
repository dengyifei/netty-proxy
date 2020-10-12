package com;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class Server {

    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public void start(final int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup work = new NioEventLoopGroup(10);

        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture f = bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_SNDBUF, 1000)
                .childOption(ChannelOption.SO_RCVBUF, 256)
                .childHandler(getChannelInitializer())
                .bind(port);
        f.addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println(String.format("success start server %s",port));
                }
            }
        }).sync();
        f.channel().closeFuture().sync();
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }


}
