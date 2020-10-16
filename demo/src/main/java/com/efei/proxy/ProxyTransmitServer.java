package com.efei.proxy;

import com.Server;
import com.efei.proxy.channelHandler.LoginChannelHandler;
import com.efei.proxy.channelHandler.ProxyReponseDataHandler;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * 代理转发服务，一般转发给客户端
 */
public class ProxyTransmitServer extends Server{

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new ProxyTcpProtocolDecoder(1048576, 8, 4, 0, 0, false));
                pip.addLast(LoginChannelHandler.getSelf());
                pip.addLast(new ProxyReponseDataHandler());
//                pip.addLast(new HttpResponseDecoder());
//                pip.addLast(new HttpObjectAggregator(2*1024));
//                pip.addLast(new MessageToMessageDecoder<HttpObject>(){
//
//                    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
//                        FullHttpResponse msg2 = (FullHttpResponse)msg;
//                        String head = msg2.headers().toString();
//                        System.out.println(String.format("head:%s",head));
//                        String sb = msg2.content().toString(CharsetUtil.UTF_8);
//                        System.out.println(String.format("body:%s",sb));
//                    }
//                });
            }
        };
    }

    public static void start(String[] args) throws InterruptedException {
        new ProxyTransmitServer().start(5000);
    }
}
