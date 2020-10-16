package com.efei.proxy.common.codec;

import com.efei.proxy.common.bean.ProxyTcpProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 将数据包解析成数据对象ProxyTcpProtocolBean
 */
// @ChannelHandler.Sharable 不能添加固件线程不安全吧
public class ProxyTcpProtocolDecoder extends LengthFieldBasedFrameDecoder {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;  //最大长度
    private static final int LENGTH_FIELD_LENGTH = 4;  //长度字段所占的字节数
    private static final int LENGTH_FIELD_OFFSET = 8;  //长度偏移
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private static ProxyTcpProtocolDecoder self = null;

    public static synchronized ProxyTcpProtocolDecoder getSelf() {
        if(self==null){
            self = new ProxyTcpProtocolDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false);
        }
        return self;
    }

//    public static  ProxyTcpDecoder getSelf() {
//          return  snew ProxyTcpDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false);
//    }

    public ProxyTcpProtocolDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public ProxyTcpProtocolDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast){
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        in = (ByteBuf) super.decode(ctx,in);
        if(in == null)
            return null;
        if(in.readableBytes()<12){
            System.err.println("字节数不足");
            throw new IllegalArgumentException("字节数不足");
        }
        // 消息类型
        byte type = in.readByte();
        byte flag = in.readByte();
        // 消息key
        byte[] keyBytes = new byte[6];
        in.readBytes(keyBytes);
        String key = new String(keyBytes,"UTF-8");
        // 消息体长度
        int length = in.readInt();

        //读取body
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);

        ProxyTcpProtocolBean bean = new ProxyTcpProtocolBean(type,flag,key,length,bytes);
        return bean;
        //return new MyProtocolBean(type,flag,length,new String(bytes,"UTF-8"));
        //return super.decode(ctx, in);
    }
}
