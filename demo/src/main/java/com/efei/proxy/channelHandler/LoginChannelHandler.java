package com.efei.proxy.channelHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 接收客户端连接，保存与转发客户端的channel.
 */
@ChannelHandler.Sharable
public class LoginChannelHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(LoginChannelHandler.class);
    private static LoginChannelHandler self = null;

    public synchronized  static LoginChannelHandler getSelf(){
        return self == null ? self = new LoginChannelHandler() : self;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(! (msg instanceof ProxyTcpProtocolBean)) {
            super.channelRead(ctx, msg);
        }
        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        if(msg2.getType() == 2){
            login(ctx,msg2);
            return;
        }
        //super.channelRead(ctx, msg);
        ctx.fireChannelRead(msg);
    }

    private void login(ChannelHandlerContext ctx, ProxyTcpProtocolBean msg){
        //String key = msg.getKey();
        String content = msg.getContentStr();
        logger.debug("login str:{}",content);
        JSONObject jo = JSON.parseObject(content);
        String username = jo.getString("username");
        Cache.put(username,ctx.channel());
    }
}
