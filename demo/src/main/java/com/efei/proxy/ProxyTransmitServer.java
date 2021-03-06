package com.efei.proxy;

import com.Server;
import com.efei.proxy.channelHandler.HeartBeatServerHandler;
import com.efei.proxy.channelHandler.LoginChannelHandler;
import com.efei.proxy.channelHandler.ProxyReponseDataHandler;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.codec.HttpRequestTransmitEncoder;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import com.efei.proxy.common.codec.TcpRequestTransmitEncoder;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ProxyConfig;
import com.efei.proxy.config.ServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 代理转发服务，一般转发给客户端
 */

@Component
public class ProxyTransmitServer extends Server{

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyTransmitServer.class);
    @Autowired
    private ProxyConfig.ProxyTransmitServerConfig proxyTransmitServerConfig;

    //private IdleStateHandler idleStateHandler =  new IdleStateHandler(5,0,0);

    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new IdleStateHandler(10,0,0));
                pip.addLast(SpringConfigTool.getBean(HeartBeatServerHandler.class));
                pip.addLast(new ProxyTcpProtocolDecoder(1048576, 8, 4, 0, 0, false));
                pip.addLast(SpringConfigTool.getBean(LoginChannelHandler.class));
                pip.addLast(SpringConfigTool.getBean(ProxyReponseDataHandler.class));

                //数据传出去
                pip.addLast(SpringConfigTool.getBean(TcpRequestTransmitEncoder.class));
                pip.addLast(SpringConfigTool.getBean(HttpRequestTransmitEncoder.class));

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
