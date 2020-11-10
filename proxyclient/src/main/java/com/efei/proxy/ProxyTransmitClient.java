package com.efei.proxy;

import com.Client;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.channelHandler.ProxyRequestDataInboundHandler;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import com.efei.proxy.common.util.MathUtil;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyConfig;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 传输数据客户端。
 *  与服务端建立连接channel用来传输数据。此channel我们叫 TransmitChannel
 */
@Component
public class ProxyTransmitClient extends Client {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyTransmitClient.class);
    //private ProxyTcpProtocolDecoder proxyTcpDecoder = ProxyTcpProtocolDecoder.getSelf();



    @Autowired
    private ProxyConfig.ProxyTransmitClientConfig proxyTransmitClientConfig;
    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast("decode", ProxyTcpProtocolDecoder.getSelf()); // 解析出对象
                pip.addLast("handler", SpringConfigTool.getBean(ProxyRequestDataInboundHandler.class)); // 处理对象

//                pip.addLast("encode",new ChannelOutboundHandlerAdapter(){
//                    @Override
//                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//                        super.write(ctx, msg, promise);
//                    }
//                });
            }
        };
    }

    @Override
    public void onConnectSuccess() {
        // 连接成功--执行登陆认证
        JSONObject jo = new JSONObject();
        jo.put("username",this.proxyTransmitClientConfig.getLoginName());
        String loginStr = jo.toJSONString();
        byte[] content = loginStr.getBytes(CharsetUtil.UTF_8);
        String key = MathUtil.getRandomString(6);
        ProxyTcpProtocolBean loginMsg = new ProxyTcpProtocolBean((byte)2,(byte)1,key,content.length,content);
        sendMsg(loginMsg.toByteBuf());
    }

    @Override
    public void onClosed() {

    }

    @Override
    public ClientConfig getClientConfig() {
        return proxyTransmitClientConfig;
    }
    //    public static void start(String[] args) throws InterruptedException {
//        ProxyTransmitClient c = new ProxyTransmitClient();
//        c.connect("127.0.0.1",5000);
//        Cache.put(c.getClass().getSimpleName(),c);
//    }
}
