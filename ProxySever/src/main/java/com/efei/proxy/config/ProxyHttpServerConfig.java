package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProxyHttpServerConfig extends ServerConfig{
    @Value("${proxyHttpServer.port}")
    int port;

    @Value("${proxyHttpServer.maxContentLength}")
    int maxFrameLength;
}
