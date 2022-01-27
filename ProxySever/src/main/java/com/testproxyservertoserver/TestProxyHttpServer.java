package com.testproxyservertoserver;

import com.Client;
import com.Server;
import com.efei.proxy.config.ServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 对外访问入口的http代理服务
 */
public class TestProxyHttpServer extends Server {


    private AttributeKey<Client> attrClient = AttributeKey.valueOf("Client");

    @Override
    public ServerConfig getServerConfig() {
        return null;
    }

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        //ctx.channel();
                        super.channelActive(ctx);
                        //System.out.println(ctx);
                        //System.out.println("channelActive");
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        //super.channelRead(ctx, msg);
                        ByteBuf in = (ByteBuf) msg;
                        Client c = ctx.channel().attr(attrClient).get();
                        if(c==null){
                            c = getClient();
                            ctx.channel().attr(attrClient).set(c);
                        }
                        ((TestProxyHttpClient)c).setProxyserverChannel(ctx.channel());
                        //System.out.println(in.refCnt());
                        ((TestProxyHttpClient) c).addMsg(in);
//                        c.sendMsg(msg, new GenericFutureListener<ChannelFuture>() {
//                            @Override
//                            public void operationComplete(ChannelFuture future) throws Exception {
////                                if(future.isSuccess()){
////                                    System.out.println("send msg success");
////                                } else {
////                                    System.err.println("send msg error::" + future);
////                                }
//                                System.out.println(in.refCnt());
//                                // ReferenceCountUtil.release(msg);
//                            }
//                        });
//                        new Thread(()->{
//                            try {
//                                new TestProxyHttpClient(ctx.channel()).addListener(new TestConnectEventListener<Client>() {
//                                    @Override
//                                    public void onConnectSuccess(Client c) {
//                                        c.sendMsg(msg);
//                                        //ReferenceCountUtil.release(msg);
//                                    }
//                                }).connect("127.0.0.1",8081);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        },"proxyhttprequest").start();

                        //ctx.fireChannelRead(msg);
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        super.channelReadComplete(ctx);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        super.exceptionCaught(ctx, cause);
                    }
                });
            }
        };
    }

    public Client getClient(){
        Client c =  new TestProxyHttpClient();
        //new Thread(()->{
            try {
                c.connect("127.0.0.1",8081);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        //},"client").start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return c;
    }

    public static void start(String[] args) throws InterruptedException {
        TestProxyHttpServer s =  new TestProxyHttpServer();
        s.start(9000);
    }
}
