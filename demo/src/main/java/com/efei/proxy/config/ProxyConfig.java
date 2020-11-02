package com.efei.proxy.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

//import com.alibaba.fastjson.JSON;

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
    //@Import(ProxyConfig.class)
    public static class ProxyHttpServerConfig extends ServerConfig{

        @Value("${proxyHttpServer.maxContentLength}")
        int maxContentLength;

        @Value("${proxyHttpServer.port}")
        int port;

        @Value("${proxyHttpServer.soBacklog}")
        int soBacklog;

        @Value("${proxyHttpServer.soSendBuf}")
        int soSendBuf;

        @Value("${proxyHttpServer.soRcvbuf}")
        int soRcvbuf;

        @Value("${proxyHttpServer.tcpNodeLay}")
        boolean tcpNodeLay;

        public int getPort() {
            return port;
        }

        public int getSoBacklog() {
            return soBacklog;
        }

        public int getSoSendBuf() {
            return soSendBuf;
        }

        public int getSoRcvbuf() {
            return soRcvbuf;
        }

        public boolean isTcpNodeLay() {
            return tcpNodeLay;
        }

        public int getMaxContentLength() {
            return maxContentLength;
        }
    }

    @Component
    //@Import(ProxyConfig.class)
    public static class ProxyTransmitServerConfig extends ServerConfig{
        @Value("${transmitServer.port}")
        int port;

        @Value("${transmitServer.soBacklog}")
        int soBacklog;

        @Value("${transmitServer.soSendBuf}")
        int soSendBuf;

        @Value("${transmitServer.soRcvbuf}")
        int soRcvbuf;

        @Value("${transmitServer.tcpNodeLay}")
        boolean tcpNodeLay;

        @Value("${transmitServer.maxFrameLength}")
        int maxFrameLength;

        public int getPort() {
            return port;
        }

        public int getSoBacklog() {
            return soBacklog;
        }

        public int getSoSendBuf() {
            return soSendBuf;
        }

        public int getSoRcvbuf() {
            return soRcvbuf;
        }

        public boolean isTcpNodeLay() {
            return tcpNodeLay;
        }
    }

//    @Bean
//    public Map<String, List<Object>> getServerPort2RealServer() {
//        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
//        yaml.setResources(new ClassPathResource(config_file));
//        Map<String, Object> m = yaml.getObject();
//        Map<String, List<Object>> m2 = new LinkedHashMap<String, List<Object>>();
//        Map<String, List<Object>> proxy = (Map<String, List<Object>>) m.getOrDefault("proxy", m2);
//        return proxy;
//
//    }
}
