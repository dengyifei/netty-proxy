package com.efei.proxy.common.codec;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class ProxyTcpProtocolEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof ProxyTcpProtocolBean){
            ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
            ByteBuf buf = ctx.channel().alloc().buffer();
            //ByteBuf buf = ctx.alloc().buffer();
            msg2.toByteBuf(buf);
            super.write(ctx,buf, promise);
            //ctx.channel().writeAndFlush(buf);
        }else {
            super.write(ctx,msg, promise);
        }
    }
}
