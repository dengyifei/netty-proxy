package com;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.Buffer;

public class TimerServer {

    private final static int port = 9090;
    public static void main(String[] args) {
        new TimerServer().start();
    }


    public void start(){
        EventLoopGroup  boss = new NioEventLoopGroup();
        EventLoopGroup  work = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.SO_RCVBUF, 128)
//                .option(ChannelOption.SO_SNDBUF, 128)
//                .option(ChannelOption.TCP_NODELAY,true)
                .childHandler(
                new ChannelInitializer<SocketChannel>(){

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pip = ch.pipeline();
                        System.out.println(this);
                        pip.addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(final ChannelHandlerContext ctx) { // (1)
                                final ByteBuf time = ctx.alloc().buffer(4); // (2)
                                time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
                                final ChannelFuture f = ctx.writeAndFlush(time); // (3)
                                f.addListener(new ChannelFutureListener() {
                                    public void operationComplete(ChannelFuture future) {
                                        assert f == future;
                                        System.out.println("发送完成");
                                        //ctx.close();
                                    }
                                }); // (4)
                            }


                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                cause.printStackTrace();
                                ctx.close();
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf in = (ByteBuf) msg;
                                System.out.println(in.toString(CharsetUtil.US_ASCII));
                                in.release();
                                try{
                                    while(in.isReadable()){//(1)
                                        System.out.print((char) in.readByte());
                                    }
                                }finally {
                                    //ReferenceCountUtil.release(msg);
                                }

                            }
                        });
                    }
                }
        );
        try {
            ChannelFuture f = bootstrap.bind(port).sync();
            System.out.println("TimeServer Started on 8080...");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
