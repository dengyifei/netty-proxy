package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyConfig;
import com.efei.proxy.config.ProxyHttpClientConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 与目标服务连接的客户端
 */
@Slf4j
public class ProxyHttpClient extends Client {
    private String key;

    public ProxyHttpClient(){
        super(null,null);
    }
    private ChannelInboundHandlerAdapter reponseDataInboundHandler = new ChannelInboundHandlerAdapter(){
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            byte[] content =null;
            ByteBuf in = (ByteBuf) msg;
            content = new byte[in.readableBytes()];
            in.readBytes(content);
            //System.out.println("xxx:"+content);
            if(content!=null){
                ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_HTTP_PACKAGE,Constant.MSG_PRP,key,content.length,content);
                log.debug(b.toStr());
                //Client c = Cache.get(ProxyTransmitClient.class.getSimpleName());
                Client c = SpringConfigTool.getBean(ProxyTransmitClient.class);
                c.sendMsg(b);
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

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.close();
            Client c = Cache.remove(key);
            super.channelInactive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.info("异常退出");
            ctx.close();
            Client c = Cache.remove(key);
            super.exceptionCaught(ctx, cause);
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

                //pip.addLast(new HttpRequestEncoder());
                pip.addLast(reponseDataInboundHandler);
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
            }
        };
    }

    @Override
    public void onConnectSuccess() {
    }

    @Override
    public void onClosed() {
        Client c = Cache.remove(key);
        log.info("cache removed:{}",key);
    }

    @Override
    public void onConnectFail() {
        // 连接失败
        Client c = Cache.remove(key);
        log.info("cache removed:{}",key);
    }

    @Override
    public ClientConfig getClientConfig() {
        return SpringConfigTool.getBean(ProxyHttpClientConfig.class);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
