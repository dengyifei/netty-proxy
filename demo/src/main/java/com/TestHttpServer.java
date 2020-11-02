package com;

import com.Server;
import com.efei.proxy.config.ServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class TestHttpServer extends Server {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        //new TestHttpServer().start(9001);

        String host = "efei";
        boolean b = NetUtil.isValidIpV4Address(host);
        if(b){
            System.out.println("ip");
        } else {
            System.out.println("domain1");
            String domain1 = host.substring(0,host.indexOf("."));
            System.out.println(domain1);
        }


    }
    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>(){

            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                //pip.addLast(new HttpServerCodec());// http 编解码
                pip.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf in = (ByteBuf) msg;
                        System.out.println(in.readableBytes());
                        super.channelRead(ctx, msg);
                    }
                });
                pip.addLast(new HttpRequestDecoder());
                //pip.addLast(new HttpResponseEncoder());
                pip.addLast("httpAggregator",new HttpObjectAggregator(512*1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
                pip.addLast(new HttpRequestHandler());// 请求处理器
            }
        };
    }

    @Override
    public ServerConfig getServerConfig() {
        return new ServerConfig() {
            @Override
            public int getPort() {
                return 9001;
            }

            @Override
            public int getSoBacklog() {
                return 128;
            }

            @Override
            public int getSoSendBuf() {
                return 10240;
            }

            @Override
            public int getSoRcvbuf() {
                return 102400;
            }

            @Override
            public boolean isTcpNodeLay() {
                return true;
            }
        };
    }

    @Override
    public void start(int port) throws InterruptedException {
        super.start(port);
    }

    @Override
    public void stop() {
        super.stop();
    }

    private final class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
            // 获取请求的uri
            String uri = req.uri();
            //System.out.println(req);
            //ByteBuf in = req.content();
            //System.out.println(in.readableBytes());

            System.out.println();

//            String host = req.headers().get("Host");
//            InetAddress address = InetAddress.getByName(host);
//            if(host.equalsIgnoreCase(address.getHostAddress())){
//                System.out.println("ip");
//            } else {
//                System.out.println("domain");
//            }

            Map<String,String> resMap = new HashMap<>();
            resMap.put("method",req.method().name());
            resMap.put("uri",uri);
            String msg = "<html><head><title>test</title></head><body>你请求uri为：" + uri+"</body></html>";
            // 创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
