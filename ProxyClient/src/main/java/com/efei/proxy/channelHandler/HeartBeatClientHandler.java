package com.efei.proxy.channelHandler;

import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@ChannelHandler.Sharable
@Component
@Slf4j
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    private volatile  int time = 0;
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                ProxyTcpProtocolBean heartMsg = new ProxyTcpProtocolBean(Constant.MSG_HEART,Constant.MSG_RQ,"123456",1,Constant.CONTENT_HEART);
                ByteBuf buf = ctx.alloc().buffer();
                heartMsg.toByteBuf(buf);
                ctx.writeAndFlush(buf);
            } else if(event.state() == IdleState.READER_IDLE) {
                if(time <= 2){
                    ProxyTcpProtocolBean heartMsg = new ProxyTcpProtocolBean(Constant.MSG_HEART,Constant.MSG_RQ,"123456",1,Constant.CONTENT_HEART);
                    ByteBuf buf = ctx.alloc().buffer();
                    heartMsg.toByteBuf(buf);
                    ctx.writeAndFlush(buf);
                    time++;
                } else {
                    time = 0;
                    ctx.close();
                }
            }
        } else {
            super.userEventTriggered(ctx,evt);
        }
    }
}
