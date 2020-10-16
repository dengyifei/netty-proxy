package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 与目标服务连接的客户端
 */
public class ProxyHttpClient extends Client {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyHttpClient.class);
    private String key; //

    // 存放请求数据
    private BlockingQueue<ByteBuf> basket = new LinkedBlockingQueue<ByteBuf>(10);

    private AttributeKey<Integer> numreadsKey = AttributeKey.valueOf("numreadsKey");

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();

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
                        System.out.println("xxx");
                        int frameLength = 500*1000;
                        Integer numreads = ctx.channel().attr(numreadsKey).get();
                        if(numreads==null){
                            numreads = 0;
                        } else {
                            numreads = numreads+1;
                        }
                        ctx.channel().attr(numreadsKey).set(numreads);

                        byte[] content =null;
                        ByteBuf in = (ByteBuf) msg;
                        if (in.readableBytes() < frameLength) {
                            if(numreads>10){
                                content = new byte[in.readableBytes()];
                                in.readBytes(content);
                            }
                        } else {
                            content = new byte[in.readableBytes()];
                            in.readBytes(content);
                        }
                        System.out.println("xxx:"+numreads);
                        if(content!=null){
                            ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)2,key,content.length,content);
                            logger.debug(b.toStr());
                            Client c = Cache.get(ProxyTransmitClient.class.getSimpleName());
                            c.sendMsg(b.toByteBuf());
                            ReferenceCountUtil.release(msg);
                        } else {
                            ctx.fireChannelRead(msg);
                        }

                        //ReferenceCountUtil.release(msg);
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        super.channelReadComplete(ctx);
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

    @Override
    public void onClosed() {
       Cache.remove(key);
    }

    public void addMsg(ByteBuf msg){
        this.basket.add(msg);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
