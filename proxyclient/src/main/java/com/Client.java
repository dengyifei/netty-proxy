package com;

import com.efei.proxy.config.ClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class Client {
    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public abstract ClientConfig getClientConfig();

    public abstract void onConnectSuccess();

    public EventLoopGroup work =null;

    public String host;

    public int port;

    public volatile boolean isConnected = false;

    public void onConnectFail(){
        //System.exit(1);
    };

    public void onClosed(){

    }

    private Channel channel = null;

    public void connect(final String host, final int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        work = new NioEventLoopGroup(2);// 两个work线程
        Bootstrap boot = new Bootstrap();
        ChannelFuture f = boot.channel(NioSocketChannel.class)
                .handler(getChannelInitializer())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_SNDBUF, getClientConfig().getSoSendBuf())
                .option(ChannelOption.SO_RCVBUF, getClientConfig().getSoRcvbuf())
                .option(ChannelOption.TCP_NODELAY,getClientConfig().isTcpNodeLay())
                .group(work)
                .connect(host, port).sync();
        channel = f.channel();
        f.addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println(String.format("success connect %s %s", host, port));
                    isConnected = true;
                    addCloseFuture();
                    onConnectSuccess();
                } else {
                    System.out.println(String.format("fail connect %s %s", host, port));
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
        System.out.println("client closed connection");
        this.channel.close();
    }

    public void addCloseFuture(){
        this.channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println(String.format(Thread.currentThread().getName() + " over connect %s %s", host, port));
                if (future.isSuccess()) {
                    System.out.println(channel+"is closed");
                    work.shutdownGracefully();
                    isConnected = false;
                    onClosed();
                }
            }
        });
    }

    public void sendMsg(Object msg) {
        if(!isConnected){
            System.err.println("host not connected");
        }
        this.channel.writeAndFlush(msg).addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    // System.out.println("send msg success");
                } else {
                    // System.err.println("send msg error::" + future);
                }
            }
        });
    }

    public void sendMsg(Object msg,GenericFutureListener<ChannelFuture> listener) {
        this.channel.writeAndFlush(msg).addListener(listener);
    }
}
