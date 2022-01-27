package com.efei.proxy.config;

public abstract  class ClientConfig {

    public abstract int getSoBacklog();

    public abstract int getSoSendBuf();

    public abstract int getSoRcvbuf();

    public abstract boolean isTcpNodeLay();

    public abstract int getConnectTimeout();

    public abstract int getNthreads();
}
