package com;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class TestStringDecodeClient extends Client{
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new StringEncoder(CharsetUtil.UTF_8));
            }
        };
    }

    public void onConnectSuccess() {
        System.out.println("发送消息");
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<1000;i++){
            sb.append("你好的中欧冠妹妹");
            if(i==999){
                sb.append("\n");
            }
        }
        for (int j=0;j<10;j++){
            sendMsg(sb.toString());
        }
        System.out.println("发送完成");

    }

    public static void main(String[] args) throws InterruptedException {
        TestStringDecodeClient Client = new TestStringDecodeClient();
        Client.connect("127.0.0.1",9090);

    }


}
