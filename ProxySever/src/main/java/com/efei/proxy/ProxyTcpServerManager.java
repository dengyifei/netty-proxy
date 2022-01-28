package com.efei.proxy;

import com.Server;
import com.alibaba.fastjson.JSON;
import com.efei.proxy.channelHandler.ProxyRequestDataHandler;
import com.efei.proxy.channelHandler.ProxyRequestHttpDataHandler;
import com.efei.proxy.common.bean.ProxyTcpServerConfigBean;
import com.efei.proxy.common.codec.HttpResponseTransmitEncoder;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ProxyConfig;
import com.efei.proxy.config.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;

import java.util.List;

/**
 * tcp 服务转发入口
 */
public class ProxyTcpServerManager {


    public List<ProxyTcpServerConfigBean> listProxyTcpServerConfigBean;


    public void setListProxyTcpServerConfigBean(List<ProxyTcpServerConfigBean> listProxyTcpServerConfigBean) {
        this.listProxyTcpServerConfigBean = listProxyTcpServerConfigBean;
    }

    public  void  start(){

        for(int i=0;i<listProxyTcpServerConfigBean.size();i++){
            ProxyTcpServerConfigBean c = listProxyTcpServerConfigBean.get(i);
            ProxyConfig.ProxyTcpServerConfig proxyTcpServerConfig = new ProxyConfig.ProxyTcpServerConfig(c);
            ProxyTcpServer p = new ProxyTcpServer(proxyTcpServerConfig);
            new Thread(()->{
                try {
                    p.start(c.getPort());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"PorxyTcpServer"+c.getPort()).start();
        }
    }

    public static class ProxyTcpServer extends Server {



        ProxyConfig.ProxyTcpServerConfig proxyTcpServerConfig;

        public ProxyTcpServer(ProxyConfig.ProxyTcpServerConfig proxyTcpServerConfig) {
            this.proxyTcpServerConfig = proxyTcpServerConfig;
        }

        @Override
        public ChannelInitializer<SocketChannel> getChannelInitializer() {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pip = ch.pipeline();
                pip.addLast(ProxyRequestDataHandler.getSelfByconfig(proxyTcpServerConfig)); // 以4层数据处理

                    //pip.addLast(new HttpClientCodec());
//                pip.addLast(new HttpRequestEncoder());
//                pip.addLast(new HttpResponseDecoder());
//                pip.addLast(new HttpObjectAggregator(2*1024));

                //pip.addLast(new HttpResponseEncoder());
                pip.addLast(SpringConfigTool.getBean(HttpResponseTransmitEncoder.class));
                }
            };
        }

        @Override
        public ServerConfig getServerConfig() {
            return proxyTcpServerConfig;
        }
    }
}
