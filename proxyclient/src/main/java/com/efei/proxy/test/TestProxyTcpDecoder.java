package com.efei.proxy.test;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.channelHandler.ProxyRequestDataInboundHandler;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;

import java.io.UnsupportedEncodingException;

/**
 * 测试解码
 */
public class TestProxyTcpDecoder {

    public void test(){
        EmbeddedChannel e = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {

            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {

                ChannelPipeline pip = ch.pipeline();
                pip.addLast(ProxyTcpProtocolDecoder.getSelf());
                pip.addLast(ProxyRequestDataInboundHandler.getSelf());
            }
        });
        byte[] content = null;
        try {
            content = "xxx你好".getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)1,"123456",content.length,content);

        ByteBuf buf = Unpooled.buffer();
//        for(int i=0;i<100;i++){
            buf.writeByte(b.getType());
            buf.writeByte(b.getFlag());
            buf.writeBytes(b.getKeyBytes());
            buf.writeInt(b.getLength());
            buf.writeBytes(b.getContent());
            e.writeInbound(buf);
//        }

    }

    /**
     * 测试转发客户端到目标服务
     */
    public void test2(){
        EmbeddedChannel e = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {

            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {

                ChannelPipeline pip = ch.pipeline();
                //pip.addLast(new HttpRequestEncoder());
                pip.addLast(ProxyTcpProtocolDecoder.getSelf());
                pip.addLast(ProxyRequestDataInboundHandler.getSelf());
            }
        });
        byte[] content = null;
//        try {
//            FileInputStream file = new FileInputStream(new File("/data/xdata/Bcode/netty-proxy/req-head.txt"));
//            int size = file.available();
//            content = new byte[size];
//            file.read(content);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

//        try {
//            content = "xxx你好".getBytes("UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        }
        StringBuffer sb = new StringBuffer();
        sb.append("GET /service/testGet?p=1213 HTTP/1.1\r\n");
        sb.append("Connection: keep-alive\r\n");
        sb.append("Content-Type: application/json\r\n");
        sb.append("Content-Length: 0\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("Host: 192.168.50.3:8788\r\n");
        sb.append("User-Agent: AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36 acrabsoft.rpc.1.1\r\n");
        sb.append("\r\n");
        try {
            content = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        ProxyTcpProtocolBean b = new ProxyTcpProtocolBean((byte)1,(byte)1,"123456",content.length,content);

        ByteBuf buf = Unpooled.buffer();
        //for(int i=0;i<100;i++){
            buf.writeByte(b.getType());
            buf.writeByte(b.getFlag());
            buf.writeBytes(b.getKeyBytes());
            buf.writeInt(b.getLength());
            buf.writeBytes(b.getContent());
            e.writeInbound(buf);
        //}

    }

    public static void main(String[] args) {
        new TestProxyTcpDecoder().test2();
    }

}
