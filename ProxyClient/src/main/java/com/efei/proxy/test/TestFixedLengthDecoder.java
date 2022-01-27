package com.efei.proxy.test;

import com.efei.proxy.channelHandler.ProxyRequestDataInboundHandler;
import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import com.efei.proxy.common.codec.ProxyTcpProtocolDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TestFixedLengthDecoder {
    public void test() throws InterruptedException {
        EmbeddedChannel e = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {

            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {

                ChannelPipeline pip = ch.pipeline();
                pip.addLast(new ByteToMessageDecoder(){

                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        System.out.println("xxx:"+in.readableBytes());
                        Object decoded = decode(ctx, in);
                        if (decoded != null) {
                            out.add(decoded);
                        }
                    }

                    protected Object decode(
                            @SuppressWarnings("UnusedParameters") ChannelHandlerContext ctx, ByteBuf in) throws Exception {
                        if (in.readableBytes() < 20) {
                            return null;
                        } else {
                            return in.readRetainedSlice(in.readableBytes());
                        }
                    }
                });
                pip.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println(msg);
                        ByteBuf in = (ByteBuf) msg;
                        System.out.println(in.readableBytes());
                        //super.channelRead(ctx, msg);
                    }
                });
            }
        });



        byte a = 65;
        for(int k=0;k<19;k++){
            ByteBuf buf = Unpooled.buffer();
            for(int i=0;i<1;i++){
                buf.writeByte(a);
            }
            e.writeInbound(buf);
            Thread.sleep(2000);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new TestFixedLengthDecoder().test();
    }
}
