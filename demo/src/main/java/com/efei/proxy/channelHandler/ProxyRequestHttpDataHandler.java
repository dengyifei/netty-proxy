package com.efei.proxy.channelHandler;

import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.MathUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.stereotype.Component;

/**
 * http 转发处理其
 */
@Component
@ChannelHandler.Sharable
public class ProxyRequestHttpDataHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyRequestHttpDataHandler.class);

    private AttributeKey<String> userchannelkey = AttributeKey.valueOf("key");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        String host = req.headers().get("Host").split(":")[0];

        boolean b = NetUtil.isValidIpV4Address(host);
        if(b){
            System.out.println("is ip");
            reponse(ctx,"不能使用ip访问");
        } else {
            //System.out.println("transmitByDoamin");
            transmitByDoamin(ctx,req,host);
        }
    }

    private void transmitByDoamin(ChannelHandlerContext ctx, FullHttpRequest req,String host){
        String domain1= host.substring(0,host.indexOf("."));
        Channel c = Cache.get(domain1);
        String key = ctx.attr(userchannelkey).get();
        req.headers().set("key",key);
        //req.duplicate();
        //FullHttpRequest req2 = req.copy();
        req.retain();
        if(c!=null){
            c.writeAndFlush(req);
        } else {
            logger.error("{} client is not line",domain1);
            reponse(ctx,domain1+ " client is not line");
        }

    }
    private void reponse(ChannelHandlerContext ctx,String rep){
        String msg = "<html><head><title>test</title></head><body>消息：" + rep+"</body></html>";
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 将html write到客户端
        //response.content()
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
