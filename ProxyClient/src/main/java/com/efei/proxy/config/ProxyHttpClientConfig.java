package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProxyHttpClientConfig extends ClientConfig{
    @Value("${proxyHttpClient.host}")
    String host;

    @Value("${proxyHttpClient.port}")
    int port;
}
