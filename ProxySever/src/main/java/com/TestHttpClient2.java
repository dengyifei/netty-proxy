package com;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class TestHttpClient2 extends Client{


    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                //pip.addLast(new HttpClientCodec());
                //pip.addLast(new HttpRequestEncoder());
                pip.addLast(new HttpResponseDecoder());
                pip.addLast(new HttpObjectAggregator(2*1024));
                pip.addLast(new MessageToMessageDecoder<HttpObject>(){

                    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
                        FullHttpResponse msg2 = (FullHttpResponse)msg;
                        String head = msg2.headers().toString();
                        System.out.println(String.format("head:%s",head));
                        String sb = msg2.content().toString(CharsetUtil.UTF_8);
                        System.out.println(String.format("body:%s",sb));
                    }
                });
            }
        };
    }

    public void onConnectSuccess() {
        byte[] content = null;
        try {
            FileInputStream file = new FileInputStream(new File("/data/ecode/netty-proxy/req-head2.txt"));
            int size = file.available();
            content = new byte[size];
            file.read(content);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        try {
//            content = "xxx你好".getBytes("UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        }
//        StringBuffer sb = new StringBuffer();
//        sb.append("GET /api/attendance/sysquery/query/getList?queryid=19 HTTP/1.1\r\n");
//        sb.append("Connection: keep-alive\r\n");
//        sb.append("Content-Type: application/json\r\n");
//        sb.append("Content-Length: 0\r\n");
//        sb.append("Accept: */*\r\n");
//        sb.append("Host: 192.168.50.3:8788\r\n");
//        sb.append("User-Agent: AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36 acrabsoft.rpc.1.1\r\n");
//        sb.append("\r\n");
        //System.out.println(sb);
        try {
            //content = sb.toString().getBytes("UTF-8");

            ByteBuf buf = getChannel().alloc().buffer();
            buf.writeBytes(content);
            System.out.println(new String(content));
            sendMsg(buf);

            ByteBuf buf2 = getChannel().alloc().buffer();
            byte[] bytes = new String("{\"search\":{\"empCalendarId\":1}}").getBytes();
            buf2.writeBytes(bytes);
            System.out.println(new String(bytes));
            sendMsg(buf2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static void main(String[] args) throws InterruptedException {
        new TestHttpClient2().connect("127.0.0.1",8085);
    }
}
