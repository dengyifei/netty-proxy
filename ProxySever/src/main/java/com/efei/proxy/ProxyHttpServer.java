package com.efei.proxy;

import com.Server;
import com.efei.proxy.channelHandler.ProxyRequestHttpDataHandler;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ProxyHttpServerConfig;
import com.efei.proxy.config.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 对外访问入口的http代理服务
 */
@Component
public class ProxyHttpServer extends Server {


    @Autowired
    private ProxyHttpServerConfig proxyHttpServerConfig;

    public ChannelInitializer<SocketChannel> getChannelInitializer() {

        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
//                pip.addLast(ProxyRequestDataHandler.getSelf()); // 以4层数据处理

                //pip.addLast(new HttpClientCodec());
//                pip.addLast(new HttpRequestEncoder());
//                pip.addLast(new HttpResponseDecoder());
//                pip.addLast(new HttpObjectAggregator(2*1024));

                pip.addLast(new HttpRequestDecoder());
                pip.addLast(new HttpObjectAggregator(proxyHttpServerConfig.getMaxFrameLength()));

                pip.addLast(new ProxyRequestHttpDataHandler());
                //pip.addLast(new HttpResponseEncoder());
                //pip.addLast(SpringConfigTool.getBean(HttpResponseTransmitEncoder.class));

;
            }
        };
    }

    @Override
    public ServerConfig getServerConfig() {
        return proxyHttpServerConfig;
    }

    public  void start() throws InterruptedException {
        start(proxyHttpServerConfig.getPort());
    }
}
