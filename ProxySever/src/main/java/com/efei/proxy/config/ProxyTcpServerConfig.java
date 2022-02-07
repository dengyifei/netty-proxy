package com.efei.proxy.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProxyTcpServerConfig extends ServerConfig{

    private String userName;
    private String targetHost;
    private int targetPort;
    private int maxContentLength;
}
