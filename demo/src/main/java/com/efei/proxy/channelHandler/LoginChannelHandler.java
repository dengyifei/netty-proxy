package com.efei.proxy.channelHandler;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 接收客户端连接，保存与转发客户端的channel.
 */
@ChannelHandler.Sharable
public class LoginChannelHandler extends ChannelInboundHandlerAdapter {

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
        super.channelRead(ctx, msg);
    }

    private void login(ChannelHandlerContext ctx, ProxyTcpProtocolBean msg){
        String key = msg.getKey();
        System.out.println("login key="+key);
        Cache.put(key,ctx.channel());
    }
}
