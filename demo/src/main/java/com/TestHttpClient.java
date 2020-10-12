package com;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

public class TestHttpClient extends Client{


    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                //pip.addLast(new HttpClientCodec());
                pip.addLast(new HttpRequestEncoder());
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
        //HttpMessage msg = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,  "/post");
        String content ="{\"xxx\":\"xx\"}";
        //FullHttpRequest msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,  "/get?p=oooo", Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)));

        HttpMessage msg = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/get?p=oooo");
        msg.headers().add("Accept", "*/*");
        //msg.headers().add("Accept-Encoding","gzip, deflate, br");
        //msg.headers().add("Accept-Language","zh-CN,zh;q=0.9");
        msg.headers().add("Cache-Control", "no-cache");
        msg.headers().add("Host", "127.0.0.1:8081");
        msg.headers().set("Content-Type", "application/json");
        //msg.headers().set(HttpHeaders.Names.CONTENT_LENGTH,msg.content().readableBytes());
        msg.headers().set(HttpHeaders.Names.CONTENT_LENGTH, 0);
        msg.headers().add("Pragma", "no-cache");
        //msg.headers().add("Connection", "close");
        msg.headers().add("Connection", "keep-alive");
        msg.headers().add("User-Agent", "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36 acrabsoft.rpc.1.1");
        System.out.println("-------------------------");

        sendMsg(msg);

    }

    public static void main(String[] args) throws InterruptedException {
        new TestHttpClient().connect("127.0.0.1",8081);
    }
}
