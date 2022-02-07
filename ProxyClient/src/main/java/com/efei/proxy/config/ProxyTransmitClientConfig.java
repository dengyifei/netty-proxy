package com.efei.proxy.config;

import lombok.Data;

@Data
public class ProxyTransmitClientConfig extends ClientConfig{
    private String host;
    private int port;
    private String loginName;
}
