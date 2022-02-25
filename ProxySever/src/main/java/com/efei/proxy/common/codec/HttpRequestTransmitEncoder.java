package com.efei.proxy.common.codec;

import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 放在ProxyTransmitServer 中
 * 将用户端的http请求实体(FullHttpRequest),转化成ProxyTcpProtocolBean
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpRequestTransmitEncoder extends HttpRequestEncoder {

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof ProxyTcpProtocolBean;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ProxyTcpProtocolBean p = (ProxyTcpProtocolBean)msg;



    }
}
