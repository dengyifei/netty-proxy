package com.efei.proxy.common;

import io.netty.util.AttributeKey;

public class Constant {

    /*>>>>>>>消息类型type*/
    public final static byte MSG_HTTP_PACKAGE =1;
    public final static byte MSG_HTTP_PACKAGE_REQ_LINE =21;
    public final static byte MSG_HTTP_PACKAGE_REQ_HEADER =22;
    public final static byte MSG_HTTP_PACKAGE_REQ_BODY =23;

    public final static byte MSG_TCP_PACKAGE =5;

    public final static byte MSG_LOGIN =2;
    public final static byte MSG_HEART =3;

    public final static byte MSG_CONNECT =4; // 连接客户端
    /*<<<<<<<<<消息类型type*/

    /*>>>>>>>消息标记flag*/
    public final static byte MSG_RQ =1; //请求
    public final static byte MSG_RP =2; //响应

    public final static byte MSG_PRQ =3; //推送请求
    public final static byte MSG_PRP =4; //推送响应
    /*<<<<<<<<<消息标记flag*/

    //心跳包
    public final static byte[] CONTENT_HEART = new byte[]{0};

    //属性
    public final static AttributeKey<String> KEY_USERNAME = AttributeKey.valueOf("username"); // 客户端登陆的username
    public final static AttributeKey<String> KEY_USERCHANNEL = AttributeKey.valueOf("key"); // 浏览器到服务channel
    public final static AttributeKey<Boolean> KEY_CONNECT = AttributeKey.valueOf("connect"); // 客户端与目标服务端连接

    public final static String MSG_SUCCESS ="success";
    public final static String MSG_FAIL ="fail";
}
