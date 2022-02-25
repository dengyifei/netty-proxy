package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyTcpClientConfig;
import com.efei.proxy.common.face.CallBack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;



/**
 * tcp连接
 */
@Slf4j
public class ProxyTcpClient extends Client {


    private String key; //

    public void setKey(String key) {
        this.key = key;
    }

    public ProxyTcpClient(CallBack<Channel> succ, CallBack<Channel> fail){
        super(succ,fail);
    }

    private ChannelInboundHandlerAdapter reponseDataInboundHandler = new ChannelInboundHandlerAdapter(){
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            byte[] content =null;
            ByteBuf in = (ByteBuf) msg;
            content = new byte[in.readableBytes()];
            in.readBytes(content);
            //System.out.println("xxx:"+content);
            if(content!=null){
                ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_TCP_PACKAGE,Constant.MSG_PRP,key,content.length,content);
                log.debug(b.toStr());
                //Client c = Cache.get(ProxyTransmitClient.class.getSimpleName());
                Client c = SpringConfigTool.getBean(ProxyTransmitClient.class);
                c.sendMsg(b);
                ReferenceCountUtil.release(msg);
            } else {
                ctx.fireChannelRead(msg);
            }
            //ReferenceCountUtil.release(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.close();
            Client c = Cache.remove(key);
            super.channelInactive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("异常退出",cause);
            ctx.close();
            Client c = Cache.remove(key);
            super.exceptionCaught(ctx, cause);
        }
    };

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(reponseDataInboundHandler);
            }
        };
    }

    @Override
    public ClientConfig getClientConfig() {
        return SpringConfigTool.getBean(ProxyTcpClientConfig.class);
    }

    @Override
    public void onConnectSuccess() {
        // 响应已经连接

    }

    @Override
    public void onConnectFail() {
        // 连接失败
        Client c = Cache.remove(key);
        log.info("cache removed:{}",key);
    }

    @Override
    public void onClosed() {
        Client c = Cache.remove(key);
        log.info("cache removed:{}",key);
    }
}
