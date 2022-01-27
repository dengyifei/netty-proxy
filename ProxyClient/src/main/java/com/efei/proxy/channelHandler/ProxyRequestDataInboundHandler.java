package com.efei.proxy.channelHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.ClientFacetory;
import com.efei.proxy.ProxyHttpClient;
import com.efei.proxy.ProxyTcpClient;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.bean.ProxyTcpServerConfigBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.MathUtil;
import com.efei.proxy.config.ProxyConfig;
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
@Component
@ChannelHandler.Sharable
public class ProxyRequestDataInboundHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyRequestDataInboundHandler.class);

    private Lock lock = new ReentrantLock();

    @Autowired
    private ProxyConfig.ProxyHttpClientConfig proxyHttpClientConfig;

    private  static ProxyRequestDataInboundHandler self = null;

    public static synchronized ProxyRequestDataInboundHandler getSelf(){
        if(self==null){
            self = new ProxyRequestDataInboundHandler();
        }
        return self;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {
        // super.channelRead(ctx, msg);
        // testChannelRead(ctx,msg);
        ProxyTcpProtocolBean msg2 = (ProxyTcpProtocolBean)msg;
        if(msg2.getType() == Constant.MSG_HEART){

        } else if(msg2.getType() == Constant.MSG_CONNECT) {
            // 连接目标服务端
            connectTargetServer(ctx,msg);
        } else if (msg2.getType() == Constant.MSG_HTTPPACKAGE){
            transmitTotargetHttpServer(ctx,msg);
        } else if(msg2.getType() == Constant.MSG_TCPPACKAGE){
            transmitTotargetTcpServer(ctx,msg);
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
        System.out.println(JSON.toJSONString(config));
        logger.debug(msg2.toStr());
        logger.info("连接目标服务 {} {}",config.getTargetHost(),config.getTargetPort());
        ProxyTcpClient c = Cache.get(msg2.getKey());
        if(c==null){
            lock.lock();
            try{
                c = Cache.get(msg2.getKey());
                if(c==null){
                    c = (ProxyTcpClient)ClientFacetory.buildCacheProxyTcpClient(msg2.getKey(),0);
                    c.setKey(msg2.getKey());
                    c.bulidBootstrap();
                    c.doConnect(config.getTargetHost(),config.getTargetPort());
                }
            }finally {
                lock.unlock();
            }
        }
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
                    c = (ProxyHttpClient)ClientFacetory.buildCacheProxyHttpClient(msg2.getKey(),0);
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
        if(c==null){
            logger.error("与目标连接失败");
            return;
        }
        //ByteBuf buf = Unpooled.buffer();
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msg2.getContent());
        //c.addMsg(buf);
        c.sendMsg(buf);
    }
}
