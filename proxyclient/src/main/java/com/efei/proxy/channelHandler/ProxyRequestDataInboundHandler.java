package com.efei.proxy.channelHandler;

import com.efei.proxy.ClientFacetory;
import com.efei.proxy.ProxyHttpClient;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.config.ProxyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
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
        transmitTotarget(ctx,msg);

    }

    // 测试
    public void testChannelRead(ChannelHandlerContext ctx, final Object msg) throws Exception{
        System.out.println(this);
        ProxyTcpProtocolBean b = (ProxyTcpProtocolBean)msg;
        System.out.println(new String(b.getContent(),"UTF-8"));
    }


    /**
     * 将数据转发给目标服务
     * 1. 根据数据key
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void transmitTotarget(ChannelHandlerContext ctx, final Object msg) throws Exception{
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
                    c.connect(proxyHttpClientConfig.getHost(),proxyHttpClientConfig.getPort());
                }
            }finally {
                lock.unlock();
            }
        }
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(msg2.getContent());
        c.addMsg(buf);
    }
}
