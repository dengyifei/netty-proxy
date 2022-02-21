package com;

import com.efei.proxy.common.face.CallBack;
import com.efei.proxy.common.util.ChannelUtil;
import com.efei.proxy.config.ClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class Client {

    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public abstract ClientConfig getClientConfig();

    public abstract void onConnectSuccess();

    public EventLoopGroup work =null;

    public Bootstrap boot = null;

    public String host;

    public int port;

    public volatile boolean isConnected = false;

    private Channel channel = null;

    private ChannelFuture clientChannelFuture;

    private CallBack<Channel> sucCallBack;

    private CallBack<Channel> failCallBack;


    protected Client(CallBack<Channel> sucCallBack, CallBack<Channel> failCallBack){
        this.sucCallBack=sucCallBack;
        this.failCallBack=failCallBack;
    }

    public void onConnectFail(){
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


    public Bootstrap bulidBootstrap(int threads){
        work = new NioEventLoopGroup(threads);
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
        clientChannelFuture = boot.connect(host, port);

        clientChannelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info(String.format("success connect %s %s", host, port));
                    channel = clientChannelFuture.channel();
                    isConnected = true;
                    addCloseFuture();
                    onConnectSuccess();
                    if(sucCallBack!=null){
                        sucCallBack.accept(channel);
                    }

                } else {
                    log.info(String.format("fail connect %s %s", host, port));
                    onConnectFail();
                    if(failCallBack!=null){
                        failCallBack.accept(channel);
                    }
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
        log.info("client closed connection");
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
                log.info(String.format(" over connect %s %s", host, port));
                if (future.isSuccess()) {
                    log.info(channel+"is closed");
                    // work.shutdownGracefully();
                    isConnected = false;
                    onClosed();
                }
            }
        });
    }

    public ChannelFuture sendMsg(Object msg) {
        if(!isConnected){
            log.info("host not connected");
            return null;
        }
//        return this.channel.writeAndFlush(msg).addListener(new GenericFutureListener<ChannelFuture>() {
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if(future.isSuccess()){
//                    // System.out.println("send msg success");
//                } else {
//                    log.info("send msg fail::" + future);
//                }
//            }
//        });
        return ChannelUtil.writeAndFlush(channel,msg);
    }


    public Channel getChannel() {
        return channel;
    }
}
