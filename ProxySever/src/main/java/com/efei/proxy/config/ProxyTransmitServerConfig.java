package com.efei.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProxyTransmitServerConfig extends ServerConfig{
    @Value("${transmitServer.port}")
    int port;

    @Value("${transmitServer.soBacklog}")
    int soBacklog;

    @Value("${transmitServer.soSendBuf}")
    int soSendBuf;

    @Value("${transmitServer.soRcvbuf}")
    int soRcvbuf;

    @Value("${transmitServer.tcpNodeLay}")
    boolean tcpNodeLay;

    @Value("${transmitServer.maxFrameLength}")
    int maxFrameLength;
}
