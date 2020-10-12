package com.efei.proxy.channelHandler;

import com.Client;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;

@ChannelHandler.Sharable
public class TCPInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {
        super.channelRead(ctx, msg);
        Client c = null;
        c.sendMsg(msg, new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("ok");
                } else {
                    System.out.println("fail");
                }
                ReferenceCountUtil.release(msg);
            }
        });
    }
}
