package com.efei.proxy;

import com.Client;
import com.Server;
import com.efei.proxy.channelHandler.ProxyRequestDataHandler;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.MathUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

/**
 * 对外访问入口的http代理服务
 */
public class ProxyHttpServer extends Server {



    public ChannelInitializer<SocketChannel> getChannelInitializer() {

        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(ProxyRequestDataHandler.getSelf());
            }
        };
    }

    public static void start(String[] args) throws InterruptedException {
        new ProxyHttpServer().start(9000);
    }
}
