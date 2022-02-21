package com.efei.proxy;

import com.Client;
import com.efei.proxy.common.cache.Cache;
import com.efei.proxy.common.face.CallBack;
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
    public static Client buildCacheProxyHttpClient(String key, long expire){
        ProxyHttpClient p = Cache.get(key);
        if(p==null){
            p = new ProxyHttpClient();
        }
        return p;
    }

    public static synchronized Client buildCacheProxyTcpClient(String key, CallBack<Channel> success, CallBack<Channel> fail){
        ProxyTcpClient c = Cache.get(key);
        if(c==null){
            c = new ProxyTcpClient(success,fail);
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
