package com.efei.proxy.channelHandler;

import com.Client;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.ClientFactory;
import com.efei.proxy.ProxyHttpClient;
import com.efei.proxy.ProxyTcpClient;
import com.efei.proxy.ProxyTransmitClient;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.bean.ProxyTcpServerConfigBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.ChannelUtil;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ProxyHttpClientConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 处理转发服务端转过来的数据,数据转发到目标服务
 */
@ChannelHandler.Sharable
public class ProxyRequestDataInboundHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyRequestDataInboundHandler.class);

    private Lock lock = new ReentrantLock();

    @Autowired
    private ProxyHttpClientConfig proxyHttpClientConfig;

    private  static ProxyRequestDataInboundHandler self = null;

    public static synchronized ProxyRequestDataInboundHandler getSelf(){
        if(self==null){
            self = new ProxyRequestDataInboundHandler();
        }
        return self;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {

        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        if(msg2.getType() == Constant.MSG_HEART){

        } else if(msg2.getType() == Constant.MSG_CONNECT) {
            // 连接目标服务端
            logger.info("连接目标服务端");
            connectTargetServer(ctx,msg);
        } else if (msg2.getType() == Constant.MSG_HTTPPACKAGE){
            transmitTotargetHttpServer(ctx,msg);
        } else if(msg2.getType() == Constant.MSG_TCPPACKAGE){
            transmitTotargetTcpServer(ctx,msg);
        } else if(msg2.getType() == Constant.MSG_LOGIN){
            logger.info("登陆响应");
        }

    }

    // 测试
    public void testChannelRead(ChannelHandlerContext ctx, final Object msg) throws Exception{
        System.out.println(this);
        ProxyTcpProtocolBean b = (ProxyTcpProtocolBean)msg;
        System.out.println(new String(b.getContent(),"UTF-8"));
    }


    /**
     * 连接目标服务端。
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void connectTargetServer(ChannelHandlerContext ctx, final Object msg) throws Exception{

        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        byte[] content = msg2.getContent();
        ProxyTcpServerConfigBean config = JSONObject.parseObject(content, ProxyTcpServerConfigBean.class);
        logger.info("缓存目标配置信息 {} {}",config.getTargetHost(),config.getTargetPort());
        Cache.put(msg2.getKey()+"_config",config);

        // 响应
        JSONObject jo = new JSONObject();
        jo.put("status",Constant.MSG_SUCCESS);
        byte[] reponse = JSON.toJSONBytes(jo);
        ProxyTcpProtocolBean reponseMsg = new ProxyTcpProtocolBean(Constant.MSG_CONNECT,Constant.MSG_PRP,msg2.getKey(),reponse.length,reponse);
        ChannelUtil.writeAndFlush(ctx.channel(),reponseMsg);
    }
    /**
     * 将数据转发给目标服务
     * 1. 根据数据key
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void transmitTotargetHttpServer(ChannelHandlerContext ctx, final Object msg) throws Exception{
        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        logger.debug(msg2.toStr());
        ProxyHttpClient c = Cache.get(msg2.getKey());
        if(c==null){
            lock.lock();
            try{
                c = Cache.get(msg2.getKey());
                if(c==null){
                    c = (ProxyHttpClient) ClientFactory.buildCacheProxyHttpClient(msg2.getKey(),0);
                    c.setKey(msg2.getKey());
                    c.bulidBootstrap();
                    c.doConnect(proxyHttpClientConfig.getHost(),proxyHttpClientConfig.getPort());
                }
            }finally {
                lock.unlock();
            }
        }
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(msg2.getContent());
        c.addMsg(buf);
    }


    public void transmitTotargetTcpServer(ChannelHandlerContext ctx, final Object msg) throws Exception{
        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        logger.debug(msg2.toStr());
        ProxyTcpClient c = Cache.get(msg2.getKey());
        ProxyTcpServerConfigBean config = Cache.get(msg2.getKey()+"_config");
        if(c==null){
            logger.info("与目标服务端连接");
            c = Cache.get(msg2.getKey());
            if(c==null){
                c = (ProxyTcpClient) ClientFactory.buildCacheProxyTcpClient(msg2.getKey());
                c.setKey(msg2.getKey());
                c.bulidBootstrap();
                c.doConnect(config.getTargetHost(),config.getTargetPort());
            }
        }
        ByteBuf buf = c.getChannel().alloc().buffer();
        buf.writeBytes(msg2.getContent());
        c.sendMsg(buf);
    }
}
