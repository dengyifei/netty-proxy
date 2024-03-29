package com.efei.proxy.channelHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.ChannelUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 接收客户端连接，保存与转发客户端的channel.
 */
@ChannelHandler.Sharable
@Slf4j
public class LoginChannelHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(! (msg instanceof ProxyTcpProtocolBean)) {
            super.channelRead(ctx, msg);
        }
        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        if(msg2.getType() == Constant.MSG_LOGIN){
            login(ctx,msg2);
            return;
        } else if(msg2.getType() == Constant.MSG_HEART) {
            // System.out.println(msg2.getContentStr());
            ProxyTcpProtocolBean heartMsg = new ProxyTcpProtocolBean(Constant.MSG_HEART,Constant.MSG_RP,"654321",1,Constant.CONTENT_HEART);
            ByteBuf buf = ctx.alloc().buffer();
            heartMsg.toByteBuf(buf);
            ctx.writeAndFlush(buf);
            return;
        }
        //super.channelRead(ctx, msg);
        ctx.fireChannelRead(msg);
    }

    private void login(ChannelHandlerContext ctx, ProxyTcpProtocolBean msg){
        //String key = msg.getKey();
        String content = msg.getContentStr();
        log.info("login str:{}",content);
        JSONObject jo = JSON.parseObject(content);
        String username = jo.getString("username");
        ctx.channel().attr(Constant.KEY_USERNAME).set(username);
        Cache.put(username,ctx.channel());
        JSONObject rp = new JSONObject();
        rp.put("msg","ok");
        byte[] cc = JSON.toJSONBytes(rp);
        ProxyTcpProtocolBean rpMsg = new ProxyTcpProtocolBean(Constant.MSG_LOGIN,Constant.MSG_RP,"654321",cc.length,cc);
        ChannelUtil.writeAndFlush(ctx.channel(),rpMsg);
    }
}
