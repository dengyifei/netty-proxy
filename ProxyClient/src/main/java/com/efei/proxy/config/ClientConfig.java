package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public abstract  class ClientConfig {
    protected int soBacklog;

    protected int soSendBuf;

    protected int soRcvbuf;

    protected boolean tcpNodeLay;

    protected int connectTimeout;

    protected int nThreads;
}
