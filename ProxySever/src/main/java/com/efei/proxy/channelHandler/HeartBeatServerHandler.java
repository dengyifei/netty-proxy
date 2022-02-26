package com.efei.proxy.channelHandler;

import com.efei.proxy.common.Constant;
import com.efei.proxy.common.cache.Cache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ChannelHandler.Sharable
@Component
@Slf4j
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("已经好久未收到客户端的消息了！");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                log.info(ctx.channel() +"关闭这个不活跃通道！");
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        String username = ctx.channel().attr(Constant.KEY_USERNAME).get();
        Cache.remove(username);
        log.info(ctx.channel()+"is closed");
    }
}
