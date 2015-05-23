package com.androider.legacy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Think on 2015/5/23.
 */
public class Encryption {
    public static String encrypt(String origin){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(origin.getBytes());
            byte[] md = md5.digest();
            StringBuilder sb = new StringBuilder();
            for(byte item: md){
                sb.append(Integer.toHexString(item&0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
