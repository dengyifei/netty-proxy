package com.efei.proxy.config;

import com.alibaba.fastjson.JSON;
import com.efei.proxy.ProxyTcpServerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//import com.alibaba.fastjson.JSON;

@Configuration
@Slf4j
public class ProxyConfiguration {
    private final static String config_file = "proxy.yml";

    @Bean
    public  static PropertySourcesPlaceholderConfigurer createProperties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        String configPath = System.getProperty("configPath");
        log.info("configPath={}",configPath);
        if(StringUtils.isEmpty(configPath)){
            yaml.setResources(new ClassPathResource(config_file));
        }else{
            yaml.setResources(new FileSystemResource(configPath));
        }
        Properties p = yaml.getObject();
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public ProxyTcpServerManager getServerPort2RealServer() {
        ProxyTcpServerManager proxyTcpServerManager = new ProxyTcpServerManager();
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        String configPath = System.getProperty("configPath");
        log.info("configPath={}",configPath);
        if(StringUtils.isEmpty(configPath)){
            yaml.setResources(new ClassPathResource(config_file));
        }else{
            yaml.setResources(new FileSystemResource(configPath));
        }
        Map<String, Object> m = yaml.getObject();
        List<Map<String,Object>> m2 = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> proxy = (List<Map<String,Object>>) m.getOrDefault("proxyTcpServer", m2);
        String str = JSON.toJSONString(proxy);
        log.info(str);
        List<ProxyTcpServerConfig> proxy2 = JSON.parseArray(str,ProxyTcpServerConfig.class);
        proxyTcpServerManager.setProxyTcpServerConfig(proxy2);
        return proxyTcpServerManager;
    }
}
