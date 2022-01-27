package com.efei.proxy.channelHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ProxyReponseDataInboundHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf in = (ByteBuf) msg;
//        byte[] content = new byte[in.readableBytes()];
//        in.readBytes(content);
//        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)2,key,content.length,content);
//        Client c = Cache.get(ProxyTransmitClient.class.getSimpleName());
//        c.sendMsg(b.toByteBuf());
//        ReferenceCountUtil.release(msg);
//        //ReferenceCountUtil.release(msg);
    }
}
