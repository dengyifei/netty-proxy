package com.efei.proxy;

import com.Server;
import com.efei.proxy.channelHandler.ProxyRequestDataHandler;
import com.efei.proxy.config.ProxyConfig;
import com.efei.proxy.config.ServerConfig;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 对外访问入口的http代理服务
 */
@Component
public class ProxyHttpServer extends Server {


    @Autowired
    private ProxyConfig.ProxyHttpServerConfig proxyHttpServerConfig;

    public ChannelInitializer<SocketChannel> getChannelInitializer() {

        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(ProxyRequestDataHandler.getSelf());
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
