package com.testproxyservertoserver;

import com.Client;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestProxyHttpClient extends Client {


    private BlockingQueue<ByteBuf> basket = new ArrayBlockingQueue<ByteBuf>(3);


    private Channel proxyserverChannel;

    private AttributeKey<Client> attrClient = AttributeKey.valueOf("Client");


//    public TestProxyHttpClient(Channel proxyserverChannel){
//        this.proxyserverChannel = proxyserverChannel;
//    }

    public void setProxyserverChannel(Channel proxyserverChannel) {
        this.proxyserverChannel = proxyserverChannel;
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                //pip.addLast(new HttpClientCodec());
//                pip.addLast(new HttpRequestEncoder());
//                pip.addLast(new HttpResponseDecoder());
//                pip.addLast(new HttpObjectAggregator(2*1024));
//                pip.addLast(new MessageToMessageDecoder<HttpObject>(){
//
//                    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
//                        FullHttpResponse msg2 = (FullHttpResponse)msg;
//                        String head = msg2.headers().toString();
//                        System.out.println(String.format("head:%s",head));
//                        String sb = msg2.content().toString(CharsetUtil.UTF_8);
//                        System.out.println(String.format("body:%s",sb));
//                    }
//                });
                pip.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        //super.channelRead(ctx, msg);
                        ByteBuf in = (ByteBuf) msg;
                        proxyserverChannel.writeAndFlush(in).addListener(new GenericFutureListener<ChannelFuture>() {
                            public void operationComplete(ChannelFuture future) throws Exception {
//                                if(future.isSuccess()){
//                                    System.out.println(Thread.currentThread().getName());
//                                    System.out.println("send msg success2");
//                                } else {
//                                    System.err.println("send msg error::" + future);
//                                }
                                Client c = proxyserverChannel.attr(attrClient).get();
                                proxyserverChannel.attr(attrClient).set(null);
                                //ReferenceCountUtil.release(msg);
                            }
                        });
                        //close();
                        //ctx.fireChannelActive();
                        //ReferenceCountUtil.release(msg);
                    }
                });
            }
        };
    }


    @Override
    public void onConnectSuccess() {
        new Thread(()->{
            while(true) {
                try {
                    ByteBuf msg = this.basket.take();
                    sendMsg(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addMsg(ByteBuf msg){
        this.basket.add(msg);
    }

}
