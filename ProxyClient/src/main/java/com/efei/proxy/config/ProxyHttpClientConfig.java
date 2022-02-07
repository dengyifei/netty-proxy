package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class ProxyHttpClientConfig extends ClientConfig{
    @Value("${proxyHttpClient.host}")
    String host;

    @Value("${proxyHttpClient.port}")
    int port;
}
