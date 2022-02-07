package com.efei.proxy.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

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
}
