package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProxyTransmitClientConfig extends ClientConfig{

    @Value("${proxyTransmitClient.host}")
    private String host;

    @Value("${proxyTransmitClient.port}")
    private int port;

    @Value("${proxyTransmitClient.loginName}")
    private String loginName;
}
