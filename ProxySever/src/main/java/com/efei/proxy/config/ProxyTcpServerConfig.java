package com.efei.proxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
public class ProxyTcpServerConfig extends ServerConfig{

    private int port;
    private String userName;
    private String targetHost;
    private int targetPort;
    private int maxContentLength;
}
