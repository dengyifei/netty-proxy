package com.efei.proxy.common.bean;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 传输数据的封装对象
 */
@Slf4j
public class ProxyTcpProtocolBean {
    private byte type;
    private byte flag;
    private String  key;
    private int length;
    private byte[] content;

    public  ProxyTcpProtocolBean(){

    }

    /**
     *
     * @param type 数据业务类型：可以自定义
     * @param flag 标识数据是请求、响应 推请求 推响应。 推一般指后端数据到用户
     * @param key  数据唯一标识
     * @param length 内容长度
     * @param content 内容
     */
    public ProxyTcpProtocolBean(byte type, byte flag,String  key, int length, byte[] content) {
        this.type = type;
        this.key = key;
        this.flag = flag;
        this.length = length;
        this.content = content;
    }



    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public byte[] getKeyBytes() {
        return key.getBytes(CharsetUtil.UTF_8);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentStr() {
        return new String(content,CharsetUtil.UTF_8);
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

//    public ByteBuf toByteBuf(){
//        ByteBuf buf = Unpooled.buffer();
//        buf.writeByte(this.getType());
//        buf.writeByte(this.getFlag());
//        buf.writeBytes(this.getKeyBytes());
//        buf.writeInt(this.getLength());
//        buf.writeBytes(this.getContent());
//        return buf;
//    }

    public void toByteBuf(ByteBuf buf){
        buf.writeByte(this.getType());
        buf.writeByte(this.getFlag());
        buf.writeBytes(this.getKeyBytes());
        buf.writeInt(this.getLength());
        buf.writeBytes(this.getContent());
    }

    public String toStr(){
        StringBuffer sb = new StringBuffer();
        sb.append("type=");
        sb.append(type);
        sb.append(",flag=");
        sb.append(flag);
        sb.append(",key=");
        sb.append(key);
        sb.append(",length=");
        sb.append(length);
        return sb.toString();
    }

    public String toString(){
        return toStr();
    }
}
