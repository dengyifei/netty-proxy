package com;

import com.efei.proxy.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class Server {

    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public abstract ServerConfig getServerConfig();

    EventLoopGroup boss = null;
    EventLoopGroup work = null;

//    private int soBacklog;
//    private int soSendBuf;
//    private int soRcvbuf;
//    private boolean tcpNodeLay;

//    public Server(int soBacklog, int soSendBuf, int soRcvbuf, boolean tcpNodeLay) {
//        this.soBacklog = soBacklog;
//        this.soSendBuf = soSendBuf;
//        this.soRcvbuf = soRcvbuf;
//        this.tcpNodeLay = tcpNodeLay;
//    }


    public void start(final int port) throws InterruptedException {
        boss = new NioEventLoopGroup(2);
        work = new NioEventLoopGroup(2);

        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture f = bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, getServerConfig().getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_SNDBUF, getServerConfig().getSoSendBuf())
                .childOption(ChannelOption.SO_RCVBUF, getServerConfig().getSoRcvbuf())
                .childOption(ChannelOption.TCP_NODELAY,getServerConfig().isTcpNodeLay())
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

    public void stop(){
        System.out.println("关闭");
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
