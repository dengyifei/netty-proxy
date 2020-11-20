package com.efei.proxy.common.bean;

public class ProxyTcpServerConfigBean {
    private int port;
    private String userName;
    private String targetHost;
    private int targetPort;
    private int maxContentLength;
    private int soBacklog;
    private int soSendBuf;
    private int soRcvbuf;
    private boolean tcpNodeLay;

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public int getMaxContentLength() {
        return maxContentLength;
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

    public boolean getTcpNodeLay() {
        return tcpNodeLay;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

    public void setSoSendBuf(int soSendBuf) {
        this.soSendBuf = soSendBuf;
    }

    public void setSoRcvbuf(int soRcvbuf) {
        this.soRcvbuf = soRcvbuf;
    }

    public void setTcpNodeLay(boolean tcpNodeLay) {
        this.tcpNodeLay = tcpNodeLay;
    }
}
