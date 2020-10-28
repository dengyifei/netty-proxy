package com.efei.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Configuration
public class ProxyConfig {

    private final static String config_file = "proxy.yml";

    @Bean
    public static PropertySourcesPlaceholderConfigurer createProperties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(config_file));
        Properties p = yaml.getObject();
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    @Component
    public final static class ProxyHttpClientConfig extends ClientConfig{

        @Value("${proxyHttpClient.soBacklog}")
        int soBacklog;

        @Value("${proxyHttpClient.soSendBuf}")
        int soSendBuf;

        @Value("${proxyHttpClient.soRcvbuf}")
        int soRcvbuf;

        @Value("${proxyHttpClient.tcpNodeLay}")
        boolean tcpNodeLay;

        @Value("${proxyHttpClient.host}")
        String host;

        @Value("${proxyHttpClient.port}")
        int port;

        @Override
        public int getSoBacklog() {
            return soBacklog;
        }

        @Override
        public int getSoSendBuf() {
            return soSendBuf;
        }

        @Override
        public int getSoRcvbuf() {
            return soRcvbuf;
        }

        @Override
        public boolean isTcpNodeLay() {
            return tcpNodeLay;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }

    @Component
    public final static class  ProxyTransmitClientConfig extends ClientConfig{

        @Value("${proxyTransmitClient.host}")
        String host;

        @Value("${proxyTransmitClient.port}")
        int port;

        @Value("${proxyTransmitClient.soBacklog}")
        int soBacklog;

        @Value("${proxyTransmitClient.soSendBuf}")
        int soSendBuf;

        @Value("${proxyTransmitClient.soRcvbuf}")
        int soRcvbuf;

        @Value("${proxyTransmitClient.tcpNodeLay}")
        boolean tcpNodeLay;

        @Value("${proxyTransmitClient.loginName}")
        String loginName;

        @Override
        public int getSoBacklog() {
            return soBacklog;
        }

        @Override
        public int getSoSendBuf() {
            return soSendBuf;
        }

        @Override
        public int getSoRcvbuf() {
            return soRcvbuf;
        }

        @Override
        public boolean isTcpNodeLay() {
            return tcpNodeLay;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getLoginName() {
            return loginName;
        }
    }
}
