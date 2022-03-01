package com.efei.proxy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.util.Properties;
import java.util.Timer;

@Configuration
@Slf4j
public class ProxyConfig {

    private final static String config_file = "proxy.yml";

    @Bean
    public static PropertySourcesPlaceholderConfigurer createProperties() {
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
}
