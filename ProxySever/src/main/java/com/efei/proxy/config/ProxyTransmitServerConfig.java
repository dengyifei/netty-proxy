package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProxyTransmitServerConfig extends ServerConfig{
    @Value("${transmitServer.port}")
    int port;

    @Value("${transmitServer.maxFrameLength}")
    int maxFrameLength;
}
