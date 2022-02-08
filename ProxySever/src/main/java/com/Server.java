package com;

import com.efei.proxy.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Server {

    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public abstract ServerConfig getServerConfig();

    EventLoopGroup boss = null;
    EventLoopGroup work = null;

    Channel channel;

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
//        boss = new NioEventLoopGroup(getServerConfig().getBossThreadNum());
//        work = new NioEventLoopGroup(getServerConfig().getWorkThreadNum());
        boss = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(1);
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture f = bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_BACKLOG, getServerConfig().getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
//                .childOption(ChannelOption.SO_SNDBUF, getServerConfig().getSoSendBuf())
//                .childOption(ChannelOption.SO_RCVBUF, getServerConfig().getSoRcvbuf())
//                .childOption(ChannelOption.TCP_NODELAY,getServerConfig().isTcpNodeLay())
                .childHandler(getChannelInitializer())
                .bind(port);
        f.addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info(String.format("success start server %s %s",port,f.channel()));
                }
            }
        }).sync();
        channel = f.channel();
        f.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info(String.format("success stop server %s %s",port,f.channel()));
                    work.shutdownGracefully();
                    boss.shutdownGracefully();
                }
            }
        });
    }

    public void stop(){
        log.info("stop server...");
        channel.close();
    }
}
