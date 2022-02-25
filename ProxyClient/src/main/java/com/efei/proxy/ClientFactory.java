package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.bean.ProxyTcpServerConfigBean;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.face.CallBack;
import com.efei.proxy.config.ProxyHttpClientConfig;
import io.netty.channel.Channel;

/**
 *
 */
public class ClientFactory {



    public static Client buildHttpClient(){
        return new ProxyHttpClient();
    }

    /**
     * 创建的key放入缓存
     * @param key
     * @return
     */
    public static synchronized Client buildCacheProxyHttpClient(String key, ProxyHttpClientConfig proxyHttpClientConfig){
        ProxyHttpClient c = Cache.get(key);
        if(c==null){
            c = new ProxyHttpClient();
            c.setKey(key);
            c.bulidBootstrap(1);
            c.doConnect(proxyHttpClientConfig.getHost(), proxyHttpClientConfig.getPort());
            Cache.put(key, c);
        }
        return c;
    }

    public static synchronized Client buildCacheProxyTcpClient(String key, ProxyTcpServerConfigBean config){
        ProxyTcpClient c = Cache.get(key);
        if(c==null){
            c = new ProxyTcpClient(null,null);
            c.setKey(key);
            c.bulidBootstrap(1);
            c.doConnect(config.getTargetHost(), config.getTargetPort());
            Cache.put(key, c);
        }
        return c;
    }

//    public static ProxyTransmitClient buildProxyTransmitClient(){
//        ProxyTransmitClient  c = new ProxyTransmitClient();
//        return c;
//    }

    public static ProxyHttpClient buildProxyHttpClient(){
        ProxyHttpClient  c = new ProxyHttpClient();
        return c;
    }
}
