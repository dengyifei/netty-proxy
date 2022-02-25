package com.efei.proxy.channelHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.ChannelUtil;
import com.efei.proxy.common.util.MathUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * http 转发处理
 * @author xefei
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class ProxyRequestHttpDataHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel() + "创建连接");
        String key = ctx.attr(Constant.KEY_USERCHANNEL).get();
        if(key == null){
            key = MathUtil.getRandomString(6);
            ctx.channel().attr(Constant.KEY_USERCHANNEL).set(key);
            Cache.put(key,ctx.channel());
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.info( ctx.channel() +"断开连接");
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
        String key = ctx.attr(Constant.KEY_USERCHANNEL).get();

        StringBuilder httpReqLine = new StringBuilder();
        //http.append("GET /service/testGet?p=1213 HTTP/1.1\r\n");
        // 请求行
        HttpMethod method = req.method();
        String methodName = method.name();
        httpReqLine.append(methodName).append(" ");

        String uri = req.uri();
        httpReqLine.append(uri).append(" ");

        HttpVersion httpVersion = req.protocolVersion();
        String httpVersionText = httpVersion.text();
        httpReqLine.append(httpVersionText).append("\r\n");

        //请求头
        HttpHeaders headers = req.headers();
        headers.add("key",key);
        StringBuilder httpHeader = new StringBuilder();

        headers.forEach(e->{
            httpHeader.append(e.getKey()).append(": ").append(e.getValue()).append("\r\n");
        });
        httpHeader.append("\r\n");
        //请求体
        if(c!=null){
            httpReqLine.append(httpHeader);
            byte[] requestLineByte  = httpReqLine.toString().getBytes();
            ProxyTcpProtocolBean requestLine = new ProxyTcpProtocolBean(Constant.MSG_HTTP_PACKAGE_REQ_LINE,Constant.MSG_RQ,key,requestLineByte.length,requestLineByte);
            ChannelUtil.writeAndFlush(c,requestLine);

//            byte[] headersByte = httpHeader.toString().getBytes();
//            ProxyTcpProtocolBean header = new ProxyTcpProtocolBean(Constant.MSG_HTTP_PACKAGE_REQ_HEADER,Constant.MSG_RQ,key,headersByte.length,headersByte);
//            ChannelUtil.writeAndFlush(c,header);

            ByteBuf contentBuf = req.content();
            byte[] contentByte = new byte[contentBuf.readableBytes()];
            contentBuf.readBytes(contentByte);

            ProxyTcpProtocolBean content = new ProxyTcpProtocolBean(Constant.MSG_HTTP_PACKAGE_REQ_BODY,Constant.MSG_RQ,key,contentByte.length,contentByte);
            ChannelUtil.writeAndFlush(c,content);

        } else {
            log.info("{} client is not line",domain1);
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
