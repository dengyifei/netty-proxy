package com.efei.proxy.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 打印Spring容器所有的Bean名称
 *
 * @author syj
 */
@Component
public class ApplicationContextBean implements ApplicationContextAware, InitializingBean {

    public static ApplicationContext applicationContext;

    /**
     * 获取 ApplicationContext
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextBean.applicationContext = applicationContext;
    }

    /**
     * 打印IOC容器中所有的Bean名称
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println(">>>>>>" + name);
        }
        System.out.println("------\nBean 总计:" + applicationContext.getBeanDefinitionCount());
    }
}