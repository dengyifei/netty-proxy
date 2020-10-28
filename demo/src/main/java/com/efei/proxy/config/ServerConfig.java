package com.efei.proxy.config;

public abstract class ServerConfig {

    public abstract int getPort();

    public abstract int getSoBacklog();

    public abstract int getSoSendBuf();

    public abstract int getSoRcvbuf();

    public abstract boolean isTcpNodeLay();
}
