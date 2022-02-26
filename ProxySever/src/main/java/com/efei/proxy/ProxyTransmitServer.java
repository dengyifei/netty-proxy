package com.efei.proxy;

import com.Server;
import com.efei.proxy.channelHandler.HeartBeatServerHandler;
import com.efei.proxy.channelHandler.LoginChannelHandler;
import com.efei.proxy.channelHandler.ProxyReponseDataHandler;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import com.efei.proxy.common.codec.ProxyTcpProtocolEncoder;
import com.efei.proxy.config.ProxyTransmitServerConfig;
import com.efei.proxy.config.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 代理转发服务，一般转发给客户端
 */

@Component
@Slf4j
public class ProxyTransmitServer extends Server{

    @Autowired
    private ProxyTransmitServerConfig proxyTransmitServerConfig;

    @Autowired
    private HeartBeatServerHandler heartBeatServerHandler;

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new IdleStateHandler(10,0,0));
                pip.addLast(heartBeatServerHandler);
                pip.addLast(ProxyTcpProtocolDecoder.getSelf());
                pip.addLast(new LoginChannelHandler());
                pip.addLast(new ProxyReponseDataHandler());

                //数据传出去
                //pip.addLast(new HttpRequestTransmitEncoder());
                pip.addLast(new ProxyTcpProtocolEncoder());


//                pip.addLast(new ChannelOutboundHandlerAdapter(){
//                    @Override
//                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//                        //super.write(ctx, msg, promise);
//                        if (msg instanceof ByteBuf) {
//                            ByteBuf in = (ByteBuf) msg;
//                            byte[] content = new byte[in.readableBytes()];
//                            in.readBytes(content);
//                            String key = ctx.attr(userchannelkey).get();
//                            ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)1,key,content.length,content);
//                            logger.debug(b.toStr());
//                        } else {
//                            super.write(ctx, msg, promise);
//                        }
//                    }
//                });
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

    @Override
    public ServerConfig getServerConfig() {
        return proxyTransmitServerConfig;
    }

    public  void start() throws InterruptedException {
        start(proxyTransmitServerConfig.getPort());
    }
}
