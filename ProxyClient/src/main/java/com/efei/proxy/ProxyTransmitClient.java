package com.efei.proxy;

import com.Client;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.channelHandler.HeartBeatClientHandler;
import com.efei.proxy.channelHandler.ProxyRequestDataInboundHandler;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import com.efei.proxy.common.codec.ProxyTcpProtocolEncoder;
import com.efei.proxy.common.util.MathUtil;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyTransmitClientConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProxyTransmitClient extends Client {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyTransmitClient.class);

    @Autowired
    private HeartBeatClientHandler heartBeatClientHandler;

    public ProxyTransmitClient(){
        super(null,null);
    }


    @Autowired
    private ProxyTransmitClientConfig proxyTransmitClientConfig;

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new IdleStateHandler(10,7,0));
                pip.addLast(heartBeatClientHandler);
                pip.addLast(ProxyTcpProtocolDecoder.getSelf()); // 解析出对象
                pip.addLast(new ProxyRequestDataInboundHandler()); // 处理对象

                pip.addLast(new ProxyTcpProtocolEncoder());

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
        ProxyTcpProtocolBean loginMsg = new ProxyTcpProtocolBean(Constant.MSG_LOGIN,Constant.MSG_RQ,key,content.length,content);
        sendMsg(loginMsg);
    }

    @Override
    public void onClosed() {
        super.onClosed();
    }

    @Override
    public ClientConfig getClientConfig() {
        return proxyTransmitClientConfig;
    }
}
