package com;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;

public class TimeClient {
    private final static String host = "127.0.0.1";
    private final static int port = 9090;


    public static void main(String[] args) {
        EventLoopGroup work = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(work)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, 128)
                .option(ChannelOption.SO_SNDBUF, 128)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pip = ch.pipeline();
                        pip.addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf m = (ByteBuf) msg; // (1)
                                try {
                                    long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
                                    System.out.println(new Date(currentTimeMillis));
                                    ctx.close();
                                } finally {
                                    m.release();
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                                ctx.close();
                            }
                        });
                    }
                });

        try{
            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // 等待连接关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            work.shutdownGracefully();
        }

    }
}
