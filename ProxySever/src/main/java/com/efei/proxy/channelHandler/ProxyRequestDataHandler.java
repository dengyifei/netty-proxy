package com.efei.proxy.channelHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.bean.ProxyTcpServerConfigBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.MathUtil;
import com.efei.proxy.config.ProxyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 转发服务端用来接收用户端发来的数据并转发给转发客户端,目前是当成4层tcp处理
 */
//@Component
@ChannelHandler.Sharable
public class ProxyRequestDataHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyRequestDataHandler.class);


    private static ProxyRequestDataHandler self = null;

    private ProxyConfig.ProxyTcpServerConfig proxyTcpServerConfig;

    public synchronized static ProxyRequestDataHandler getSelf(){
        return self == null ? self = new ProxyRequestDataHandler() : self;
        //return new ProxyRequestDataHandler();
    }

    public synchronized static ProxyRequestDataHandler getSelfByconfig(ProxyConfig.ProxyTcpServerConfig config){
        //return self == null ? self = new ProxyRequestDataHandler() : self;
        ProxyRequestDataHandler handler = new ProxyRequestDataHandler();
        handler.proxyTcpServerConfig = config;
        return handler;
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

        // 下发数据到客户端去连接
        ProxyTcpServerConfigBean proxyTcpServerConfigBean= proxyTcpServerConfig.getProxyTcpServerConfigBean();
        String userName = proxyTcpServerConfigBean.getUserName();
        Channel c = Cache.get(userName);
        if(c==null){
            logger.info("{} 客户端没有登陆",userName);
            ctx.close();
            return;
        }
        byte[] content = JSON.toJSONBytes(proxyTcpServerConfigBean);
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_CONNECT,Constant.MSG_PRQ,key,content.length,content);
//        ByteBuf buf = ctx.alloc().buffer();
//        b.toByteBuf(buf);
        c.writeAndFlush(b);
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

        Boolean isConnect = ctx.channel().attr(Constant.KEY_CONNECT).get();
        while (isConnect != Boolean.TRUE){
            Thread.sleep(1000*1);
            isConnect = ctx.channel().attr(Constant.KEY_CONNECT).get();
        }
//        if(isConnect != Boolean.TRUE){
//            logger.info("客户端与目标主机连接失败");
//            return;
//        }
        ByteBuf in = (ByteBuf) msg;
        byte[] content = new byte[in.readableBytes()];
        in.readBytes(content);
        String key = ctx.channel().attr(Constant.KEY_USERCHANNEL).get();
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_TCPPACKAGE,Constant.MSG_RQ,key,content.length,content);
        logger.debug(b.toStr());
        // Channel c = Cache.get("efei");
        ProxyTcpServerConfigBean proxyTcpServerConfigBean= proxyTcpServerConfig.getProxyTcpServerConfigBean();
        String userName = proxyTcpServerConfigBean.getUserName();
        Channel c = Cache.get(userName);
        if(c!=null){
            c.writeAndFlush(b);
        } else {
            logger.error("{} client is not line",userName);
        }
        ReferenceCountUtil.release(msg);
    }
}
