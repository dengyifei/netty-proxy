package com.efei.proxy.common.util;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelUtil {

    public static ChannelFuture writeAndFlush(Channel channel, Object content){
        if(content==null){
            log.info("发送消息失败 channel={},content is null");
            return null;
        }

        if(channel==null){
            log.info("发送消息失败,channl is null");
            return null;
        }

        return channel.writeAndFlush(content).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(!future.isSuccess()){
                    log.info("发送消息失败,{}",channel.remoteAddress());
                    ReferenceCountUtil.release(content);
                }
            }
        });
    }
}
