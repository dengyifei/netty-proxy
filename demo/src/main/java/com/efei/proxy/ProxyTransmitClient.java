package com.efei.proxy;

import com.Client;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ProxyTransmitClient extends Client {

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return null;
    }

    @Override
    public void onConnectSuccess() {

    }
}
