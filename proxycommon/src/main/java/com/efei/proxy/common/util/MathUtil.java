package com.efei.proxy.common.util;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class MathUtil {
    //protected final static Logger logger = Logger.getLogger(MathUtil.class.getSimpleName());

    /**
     * 生成一个安全码（UUID）
     *
     * @return
     */
    public static String getSecurityCode() {
        return UUID.randomUUID().toString();
    }

    /**
     * 随机生成字符串
     *
     * @param length    想要生成的长度
     * @return
     */
    public static String getRandomString(int length) {
        String base = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取6-10 的随机位数数字
     * @param length    想要生成的长度
     * @return result
     */
    public static String getRandom620(Integer length) {
        String result = "";
        Random rand = new Random();
        int n = 20;
        if (null != length && length > 0) {
            n = length;
        }
        int randInt = 0;
        for (int i = 0; i < n; i++) {
            randInt = rand.nextInt(10);

            result += randInt;
        }
        return result;
    }

    /**
     * MD5
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String getMD5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("MD5转换异常！message：" + e.getMessage());
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
}