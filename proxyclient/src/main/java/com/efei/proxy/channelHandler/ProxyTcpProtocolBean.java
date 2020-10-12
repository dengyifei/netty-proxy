package com.efei.proxy.channelHandler;

public class ProxyTcpProtocolBean {
    private byte type;
    private byte flag;
    private String  key;
    private int length;
    private byte[] content;

    public  ProxyTcpProtocolBean(){

    }

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

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }
}
