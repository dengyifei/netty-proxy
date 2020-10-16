package com.efei.proxy.channelHandler;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 响应数据到用户channel
 */
@ChannelHandler.Sharable
public class ProxyReponseDataHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyReponseDataHandler.class);
    private static ProxyReponseDataHandler self = null;

    public synchronized  static ProxyReponseDataHandler getSelf(){
        return self == null ? self = new ProxyReponseDataHandler() : self;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {
        // super.channelRead(ctx, msg);
        if(! (msg instanceof ProxyTcpProtocolBean)) {
            super.channelRead(ctx, msg);
        }
        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        if(msg2.getType() == 1){
            transmitToUserChannel(ctx,msg2);
            return;
        }
    }

    /**
     * 数据转发用户channel
     * @param ctx
     * @param msg
     */
    private void transmitToUserChannel(ChannelHandlerContext ctx, ProxyTcpProtocolBean msg){
        logger.debug(msg.toStr());
        String key = msg.getKey();
        Channel userChannel = Cache.get(key);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(msg.getContent());
        userChannel.writeAndFlush(buf);
    }
}
