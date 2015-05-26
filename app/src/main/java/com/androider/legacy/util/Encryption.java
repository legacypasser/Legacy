package com.androider.legacy.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Think on 2015/5/23.
 */
public class Encryption {
    public static String encrypt(String origin){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(origin.getBytes("UTF-8"));
            byte[] md = md5.digest();
            StringBuilder sb = new StringBuilder();
            for(byte item: md){
                sb.append(Integer.toHexString(item&0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException |UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String hmac(String origin, String bigali){
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(bigali.getBytes(), mac.getAlgorithm());
            mac.init(keySpec);
            byte[] result = mac.doFinal(origin.getBytes());
            return Base64.encodeToString(result,Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException|InvalidKeyException e) {
            return null;
        }
    }
}
