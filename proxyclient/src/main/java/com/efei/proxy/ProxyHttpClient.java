package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 与目标服务连接的客户端
 */
public class ProxyHttpClient extends Client {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyHttpClient.class);
    private String key; //

    // 存放请求数据
    private BlockingQueue<ByteBuf> basket = new LinkedBlockingQueue<ByteBuf>(3);

    AtomicBoolean isRun = new AtomicBoolean(true);

    //private AttributeKey<Integer> numreadsKey = AttributeKey.valueOf("numreadsKey");

    private ChannelInboundHandlerAdapter proxyReponseDataInboundHandler = new ChannelInboundHandlerAdapter(){
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            byte[] content =null;
            ByteBuf in = (ByteBuf) msg;
            content = new byte[in.readableBytes()];
            in.readBytes(content);
            //System.out.println("xxx:"+content);
            if(content!=null){
                ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)2,key,content.length,content);
                logger.debug(b.toStr());
                //Client c = Cache.get(ProxyTransmitClient.class.getSimpleName());
                Client c = SpringConfigTool.getBean(ProxyTransmitClient.class);
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
    };
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

                pip.addLast(proxyReponseDataInboundHandler);
            }
        };
    }

    @Override
    public void onConnectSuccess() {

        Thread t1 = new Thread(()->{
            while(isRun.get()) {
                try {
                    //ByteBuf msg = this.basket.take();
                    ByteBuf msg = this.basket.poll(60, TimeUnit.SECONDS);
                    if(msg!=null){
                        sendMsg(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.setName("sendQueueData");
        t1.start();
    }

    @Override
    public void onClosed() {
        isRun.set(false); // 停止发数据线程
        Client c = Cache.remove(key);
        logger.info(c.getChannel() + "cache removed");
    }

    @Override
    public ClientConfig getClientConfig() {
        return SpringConfigTool.getBean(ProxyConfig.ProxyHttpClientConfig.class);
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
