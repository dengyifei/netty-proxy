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
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 放在ProxyTransmitServer 中
 * 将用户端的http请求实体(FullHttpRequest),转化成ProxyTcpProtocolBean
 */
@Component
@ChannelHandler.Sharable
public class HttpRequestTransmitEncoder extends HttpRequestEncoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpRequestTransmitEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
        if(msg instanceof HttpRequest){
            String key = ((HttpRequest) msg).headers().get("key");
            int size = out.size();
            for(int i=0;i<size;i++){
                if (out.get(i) instanceof ByteBuf) {
                    ByteBuf in = (ByteBuf) out.get(i);
                    byte[] content = new byte[in.readableBytes()];
                    in.readBytes(content);
                    ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_HTTPPACKAGE,Constant.MSG_PRQ,key,content.length,content);
                    logger.debug(b.toStr());
                    ByteBuf buf = ctx.alloc().buffer();
                    b.toByteBuf(buf);
                    out.set(i,buf);
                    ReferenceCountUtil.release(in);
                }
            }
        }
    }
}
