package com.efei.proxy.common.codec;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 放在proxyHttpServer
 * 将转化成ProxyTcpProtocolBean 中数据解码出
 */
@Component
@ChannelHandler.Sharable
public class HttpResponseTransmitEncoder extends HttpResponseEncoder {

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) || (msg instanceof ProxyTcpProtocolBean);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if(msg instanceof ProxyTcpProtocolBean){
            ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
            ByteBuf buf = ctx.alloc().buffer();
            buf.writeBytes(msg2.getContent());
            out.add(buf);
        } else {
            super.encode(ctx, msg, out);
        }
//        if(msg instanceof ByteBuf) {
//            ByteBuf potentialEmptyBuf = (ByteBuf) msg;
//            potentialEmptyBuf.retain();
//            out.add(msg);
//        } else {
//            super.encode(ctx, msg, out);
//        }
    }
}
