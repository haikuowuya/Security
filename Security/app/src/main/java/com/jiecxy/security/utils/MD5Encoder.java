package com.jiecxy.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by apple on 15-1-28.
 */

public class MD5Encoder
{
    public static String encode(String pwd)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");//拿到MD5加密的对象
            byte[] bytes = messageDigest.digest(pwd.getBytes());//返回一个加密后的字节数组

            StringBuffer sb = new StringBuffer();
            String tmp;
            for(int i = 0; i < bytes.length; i++)
            {
                tmp = Integer.toHexString(0xff & bytes[i]);//把字节转换为16进制的字符串
                if(tmp.length() == 1)        //如果这个字符串，只有一个字符，就要补0
                {
                    sb.append("0" + tmp);
                }
                else
                {
                    sb.append(tmp);
                }
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("没有这个加密算法" + e);
        }
    }

}