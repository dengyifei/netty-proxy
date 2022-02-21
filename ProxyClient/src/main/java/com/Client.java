package com;

import com.efei.proxy.config.ClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class Client {
    private  InternalLogger logger = InternalLoggerFactory.getInstance(getClass());

    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public abstract ClientConfig getClientConfig();

    public abstract void onConnectSuccess();

    public EventLoopGroup work =null;

    public Bootstrap boot = null;

    public String host;

    public int port;

    public volatile boolean isConnected = false;

    public void onConnectFail(){
        //System.exit(1);
        // 重连接
        if(work!=null){
            work.schedule(()->{
                doConnect(host,port);
            },1, TimeUnit.SECONDS);
        }
    };

    public void onClosed(){
        if(work!=null){
            work.schedule(()->{
                doConnect(host,port);
            },1, TimeUnit.SECONDS);
        }
    }
    private Channel channel = null;
    private ChannelFuture clientChannelFuture;

    public Bootstrap bulidBootstrap(){
        work = new NioEventLoopGroup(1);
        boot = new Bootstrap();
        boot.channel(NioSocketChannel.class)
                .handler(getChannelInitializer())
                // .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.SO_SNDBUF, getClientConfig().getSoSendBuf())
//                .option(ChannelOption.SO_RCVBUF, getClientConfig().getSoRcvbuf())
//                .option(ChannelOption.TCP_NODELAY,getClientConfig().isTcpNodeLay())
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,getClientConfig().getConnectTimeout())
                .group(work);
        return boot;
    }

    public void doConnect(final String host, final int port) {
        if (channel != null && channel.isActive()) {
            return;
        }
        this.host = host;
        this.port = port;
//        work = new NioEventLoopGroup(getClientConfig().getNthreads());
//        Bootstrap boot = new Bootstrap();
//        ChannelFuture f = boot.channel(NioSocketChannel.class)
//                .handler(getChannelInitializer())
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.SO_SNDBUF, getClientConfig().getSoSendBuf())
//                .option(ChannelOption.SO_RCVBUF, getClientConfig().getSoRcvbuf())
//                .option(ChannelOption.TCP_NODELAY,getClientConfig().isTcpNodeLay())
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,getClientConfig().getConnectTimeout())
//                .group(work)
        //ChannelFuture clientChannelFuture = boot.connect(host, port).sync();//直到连接返回，才会退出当前线程
        clientChannelFuture = boot.connect(host, port);//直到连接返回，才会退出当前线程

        clientChannelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info(String.format("success connect %s %s", host, port));
                    channel = clientChannelFuture.channel();
                    isConnected = true;
                    addCloseFuture();
                    onConnectSuccess();
                } else {
                    logger.info(String.format("fail connect %s %s", host, port));
                    onConnectFail();
                }
//                if(future.isCancelled()){
//                    System.out.println(String.format("cancelled connect %s %s", host, port));
//                }
//                if(future.isDone()){
//                    System.out.println(String.format("done connect %s %s", host, port));
//                }
//                if(future.isVoid()){
//                    System.out.println(String.format("Void connect %s %s", host, port));
//                }
            }
        });
//        clientChannelFuture.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
//            public void operationComplete(ChannelFuture future) throws Exception {
//                System.out.println(String.format(Thread.currentThread().getName() + " over connect %s %s", host, port));
//                if (future.isSuccess()) {
//                    //addCloseFuture();
//                    System.out.println(channel+"is closed");
//                    work.shutdownGracefully();
//                    isConnected = false;
//                    onClosed();
//                }
//            }
//        });
//        f.sync();
//        System.out.println("xxxx");
//        f.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if(future.isSuccess()){
//                    System.out.println(String.format(Thread.currentThread().getName()+" over connect %s %s", host, port));
//                    onClosed();
//                }
//            }
//        }).sync();
//        work.shutdownGracefully();
        //System.out.println(String.format(Thread.currentThread().getName()+" over connect %s %s", host, port));
    }

    public void close(){
        logger.info("client closed connection");
        this.channel.close();
    }

    public void shutdown(){
        if(work!=null){
            work.shutdownGracefully();
        }
    }

    private void addCloseFuture(){
        this.channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info(String.format(Thread.currentThread().getName() + " over connect %s %s", host, port));
                if (future.isSuccess()) {
                    logger.info(channel+"is closed");
                    // work.shutdownGracefully();
                    isConnected = false;
                    onClosed();
                }
            }
        });
    }

    public ChannelFuture sendMsg(Object msg) {
        if(!isConnected){
            logger.info("host not connected");
            return null;
        }
        return this.channel.writeAndFlush(msg).addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    // System.out.println("send msg success");
                } else {
                    logger.info("send msg fail::" + future);
                }
            }
        });
    }

    public ChannelFuture sendMsg(Object msg,GenericFutureListener<ChannelFuture> listener) {
        return this.channel.writeAndFlush(msg).addListener(listener);
    }

    public Channel getChannel() {
        return channel;
    }
}
