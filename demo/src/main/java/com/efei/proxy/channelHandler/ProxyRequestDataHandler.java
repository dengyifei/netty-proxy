package com.efei.proxy.channelHandler;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.MathUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;


/**
 * 转发服务端用来接收用户端发来的数据并转发给转发客户端
 */
@ChannelHandler.Sharable
public class ProxyRequestDataHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyRequestDataHandler.class);

    private AttributeKey<String> userchannelkey = AttributeKey.valueOf("key");

    private static ProxyRequestDataHandler self = null;

    public synchronized static ProxyRequestDataHandler getSelf(){
        return self == null ? self = new ProxyRequestDataHandler() : self;
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
        String key = ctx.attr(userchannelkey).get();
        if(key == null){
            key = MathUtil.getRandomString(6);
            ctx.channel().attr(userchannelkey).set(key);
            Cache.put(key,ctx.channel());
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        logger.info( ctx.channel() +"断开连接");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] content = new byte[in.readableBytes()];
        in.readBytes(content);
        String key = ctx.attr(userchannelkey).get();
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)1,key,content.length,content);
        logger.debug(b.toStr());
        Channel c = Cache.get("efei");
        if(c!=null){
            c.writeAndFlush(b.toByteBuf());
        } else {
            logger.error("xefeia client is not line");
        }
        ReferenceCountUtil.release(msg);
    }
}
