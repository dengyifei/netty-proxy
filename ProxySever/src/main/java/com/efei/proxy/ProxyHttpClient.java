package com.efei.proxy;

import com.Client;
import com.testproxyservertoserver.TestConnectEventListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

public class ProxyHttpClient extends Client {



    private Channel proxyserverChannel;

    private TestConnectEventListener listener;

    public ProxyHttpClient(Channel proxyserverChannel){
        this.proxyserverChannel = proxyserverChannel;
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                //pip.addLast(new HttpClientCodec());
//                pip.addLast(new HttpRequestEncoder());
//                pip.addLast(new HttpResponseDecoder());
//                pip.addLast(new HttpObjectAggregator(2*1024));
//                pip.addLast(new MessageToMessageDecoder<HttpObject>(){
//
//                    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
//                        FullHttpResponse msg2 = (FullHttpResponse)msg;
//                        String head = msg2.headers().toString();
//                        System.out.println(String.format("head:%s",head));
//                        String sb = msg2.content().toString(CharsetUtil.UTF_8);
//                        System.out.println(String.format("body:%s",sb));
//                    }
//                });
                pip.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        //super.channelRead(ctx, msg);
                        ByteBuf in = (ByteBuf) msg;
                        System.out.println(in.readableBytes());
                        proxyserverChannel.writeAndFlush(in).addListener(new GenericFutureListener<ChannelFuture>() {
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(future.isSuccess()){
                                    System.out.println(Thread.currentThread().getName());
                                    System.out.println("send msg success2");
                                } else {
                                    System.err.println("send msg error::" + future);
                                }
                            }
                        });
                        //close();
                        //ctx.fireChannelActive();
                        //ReferenceCountUtil.release(msg);
                        System.out.println("121212");
                    }
                });
            }
        };
    }

    @Override
    public void onConnectSuccess() {
        listener.onConnectSuccess(this);
    }


    public ProxyHttpClient addListener(TestConnectEventListener listener){
        this.listener = listener;
        return this;
    }
}
