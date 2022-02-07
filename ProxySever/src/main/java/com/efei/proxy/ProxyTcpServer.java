package com.efei.proxy;

import com.Server;
import com.efei.proxy.channelHandler.ProxyRequestDataHandler;
import com.efei.proxy.config.ProxyTcpServerConfig;
import com.efei.proxy.config.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * tcp代理服务，1.负责接收用户客户端的请求数据，并将数据通过TransmitServer 转发到目标客户端。
 * 2. 负责接收后端到客户端的响应数据
 */
public class ProxyTcpServer extends Server {

    ProxyTcpServerConfig proxyTcpServerConfig;

    public ProxyTcpServer(ProxyTcpServerConfig proxyTcpServerConfig) {
        this.proxyTcpServerConfig = proxyTcpServerConfig;
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new ProxyRequestDataHandler(proxyTcpServerConfig)); // 以4层数据处理

                //pip.addLast(new HttpClientCodec());
//                pip.addLast(new HttpRequestEncoder());
//                pip.addLast(new HttpResponseDecoder());
//                pip.addLast(new HttpObjectAggregator(2*1024));

                //pip.addLast(new HttpResponseEncoder());
                //pip.addLast(SpringConfigTool.getBean(HttpResponseTransmitEncoder.class));
            }
        };
    }

    @Override
    public ServerConfig getServerConfig() {
        return proxyTcpServerConfig;
    }
}
