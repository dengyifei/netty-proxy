package com;


import com.efei.proxy.config.ServerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class TestStringDecodeServer extends Server {

    @Override
    public ServerConfig getServerConfig() {
        return null;
    }

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>(){

            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pip.addLast(new SimpleChannelInboundHandler<String>() {

                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                        //System.out.println(this);
                        //System.out.println(ctx.channel());
                        System.out.println(msg);
                    }
                });
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        new TestStringDecodeServer().start(9090);
    }
}
