package com.efei.proxy;

import com.Client;
import com.alibaba.fastjson.JSONObject;
import com.efei.proxy.common.Constant;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.util.SpringConfigTool;
import com.efei.proxy.config.ClientConfig;
import com.efei.proxy.config.ProxyTcpClientConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * tcp连接
 */
public class ProxyTcpClient extends Client {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyTcpClient.class);

    private String key; //

    public void setKey(String key) {
        this.key = key;
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
                ProxyTcpProtocolBean b = new ProxyTcpProtocolBean(Constant.MSG_TCPPACKAGE,Constant.MSG_PRP,key,content.length,content);
                logger.debug(b.toStr());
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
            logger.error("异常退出",cause);
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
        JSONObject jo = new JSONObject();
        jo.put("status",Constant.MSG_SUCCESS);
        String reponse = jo.toJSONString();
        byte[] content = reponse.getBytes(CharsetUtil.UTF_8);
        ProxyTcpProtocolBean reponseMsg = new ProxyTcpProtocolBean(Constant.MSG_CONNECT,Constant.MSG_PRP,key,content.length,content);
        Client c = SpringConfigTool.getBean(ProxyTransmitClient.class);
        c.sendMsg(reponseMsg);
    }

    @Override
    public void onConnectFail() {
        // 响应已经连接
        JSONObject jo = new JSONObject();
        jo.put("status",Constant.MSG_FAIL);
        String reponse = jo.toJSONString();
        byte[] content = reponse.getBytes(CharsetUtil.UTF_8);
        ProxyTcpProtocolBean reponseMsg = new ProxyTcpProtocolBean(Constant.MSG_CONNECT,Constant.MSG_PRP,key,content.length,content);
        Client c = SpringConfigTool.getBean(ProxyTransmitClient.class);
        c.sendMsg(reponseMsg);
    }

    @Override
    public void onClosed() {
        Client c = Cache.remove(key);
        logger.info(c.getChannel() + "cache removed");
    }
}
