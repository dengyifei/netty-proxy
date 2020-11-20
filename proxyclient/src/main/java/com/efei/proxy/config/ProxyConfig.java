package com.efei.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Timer;

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

    @Bean
    public Timer getTimer(){
        Timer timer = new Timer(true);
        return timer;
    }

    @Component
    public final static class ProxyTcpClientConfig extends ClientConfig{
        @Value("${proxyHttpClient.soBacklog}")
        int soBacklog;

        @Value("${proxyHttpClient.soSendBuf}")
        int soSendBuf;

        @Value("${proxyHttpClient.soRcvbuf}")
        int soRcvbuf;

        @Value("${proxyHttpClient.tcpNodeLay}")
        boolean tcpNodeLay;

        @Value("${proxyHttpClient.connectTimeout:5000}")
        int connectTimeout;

        @Value("${proxyHttpClient.nThreads:5}")
        int nThreads;

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
        public int getConnectTimeout() {
            return connectTimeout;
        }

        public int getNthreads() {
            return nThreads;
        }
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

        @Value("${proxyHttpClient.connectTimeout:5000}")
        int connectTimeout;

        @Value("${proxyHttpClient.nThreads:5}")
        int nThreads;

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

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }
        public int getConnectTimeout() {
            return connectTimeout;
        }

        public int getNthreads() {
            return nThreads;
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

        @Value("${proxyTransmitClient.connectTimeout:5000}")
        int connectTimeout;

        @Value("${proxyTransmitClient.nThreads:5}")
        int nThreads;

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

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }
        public int getNthreads() {
            return nThreads;
        }
    }
}
