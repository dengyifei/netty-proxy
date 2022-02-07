package com.efei.proxy;

import com.efei.proxy.config.ProxyTcpServerConfig;
import lombok.Data;

import java.util.List;

/**
 * tcp 服务转发入口
 */
@Data
public class ProxyTcpServerManager {


    public List<ProxyTcpServerConfig> proxyTcpServerConfig;

    public  void  start(){

        for(int i=0;i<proxyTcpServerConfig.size();i++){
            ProxyTcpServerConfig c = proxyTcpServerConfig.get(i);
            ProxyTcpServer p = new ProxyTcpServer(c);
            new Thread(()->{
                try {
                    p.start(c.getPort());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"PorxyTcpServer"+c.getPort()).start();
        }
    }
}
