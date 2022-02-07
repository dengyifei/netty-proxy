package com.efei.proxy.channelHandler;

import com.alibaba.fastjson.JSON;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.ChannelUtil;
import com.efei.proxy.common.util.MathUtil;
import com.efei.proxy.config.ProxyTcpServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;


/**
 * 用来接收用户端发来的数据并转发给转发客户端,目前是当成4层tcp处理
 */
//@Component
@ChannelHandler.Sharable
public class ProxyRequestDataHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyRequestDataHandler.class);

    private ProxyTcpServerConfig proxyTcpServerConfig;

    public ProxyRequestDataHandler(ProxyTcpServerConfig proxyTcpServerConfig){
        this.proxyTcpServerConfig = proxyTcpServerConfig;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug(ctx.channel()+"is channelActive");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info(ctx.channel() + "创建连接");
        String key = ctx.channel().attr(Constant.KEY_USERCHANNEL).get();
        if(key == null){
            key = MathUtil.getRandomString(6);
            ctx.channel().attr(Constant.KEY_USERCHANNEL).set(key);
            Cache.put(key,ctx.channel());
        }


        String userName = proxyTcpServerConfig.getUserName();
        Channel c = Cache.get(userName);
        if(c==null){
            logger.info(" 客户端{}没有登陆,通道关闭",userName);
            ctx.close();
            return;
        }
        /**
         *  通知客户端去连接
         */
        byte[] content = JSON.toJSONBytes(proxyTcpServerConfig);
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_CONNECT,Constant.MSG_PRQ,key,content.length,content);
//        ByteBuf buf = ctx.alloc().buffer();
//        b.toByteBuf(buf);

        ChannelUtil.writeAndFlush(c,b).await(3*1000);

        Boolean isConnect = ctx.channel().attr(Constant.KEY_CONNECT).get();
        int timeout =1;
        while (!isConnect){
            Thread.sleep(1000*1);
            isConnect = ctx.channel().attr(Constant.KEY_CONNECT).get();
            if(!isConnect && timeout>10){
                logger.info("连接目标客户端超时");
                ctx.close();
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        Cache.remove(ctx.channel().attr(Constant.KEY_USERCHANNEL).get());
        logger.info( ctx.channel() +" channelUnregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Cache.remove(ctx.channel().attr(Constant.KEY_USERCHANNEL).get());
        logger.info( ctx.channel() +" channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] content = new byte[in.readableBytes()];
        in.readBytes(content);
        String key = ctx.channel().attr(Constant.KEY_USERCHANNEL).get();
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_TCPPACKAGE,Constant.MSG_RQ,key,content.length,content);
        String userName = proxyTcpServerConfig.getUserName();
        Channel c = Cache.get(userName);
        if(c!=null){
            ChannelUtil.writeAndFlush(c,b);
        } else {
            logger.info("{} client is not line",userName);
        }
        ReferenceCountUtil.release(msg);
    }
}
