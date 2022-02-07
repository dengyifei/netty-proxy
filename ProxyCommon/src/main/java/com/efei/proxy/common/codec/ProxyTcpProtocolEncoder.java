package com.efei.proxy.common.codec;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
public class ProxyTcpProtocolEncoder extends ChannelOutboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyTcpProtocolEncoder.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof ProxyTcpProtocolBean){
            ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
            ByteBuf buf = ctx.alloc().buffer();
            msg2.toByteBuf(buf);
            super.write(ctx, buf, promise);
        }else {
            super.write(ctx, msg, promise);
        }
    }
}
