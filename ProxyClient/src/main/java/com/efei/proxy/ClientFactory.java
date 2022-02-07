package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.cache.Cache;

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
    public static Client buildCacheProxyHttpClient(String key, long expire){
        ProxyHttpClient p = Cache.get(key);
        if(p==null){
            p = new ProxyHttpClient();
            Cache.put(key,p,expire); //默认两分钟
        }
        return p;
    }

    public static Client buildCacheProxyTcpClient(String key, long expire){
        ProxyTcpClient p = Cache.get(key);
        if(p==null){
            p = new ProxyTcpClient();
            Cache.put(key,p,expire); //默认两分钟
        }
        return p;
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
