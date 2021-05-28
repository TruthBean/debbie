package com.truthbean.debbie.httpclient.test;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.io.StreamHelper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {
    public static String md5WithBase64(String str) {
        if (str == null) {
            return "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //加密后的字符串
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            byte[] digest = md5.digest(bytes);
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("", e);
        }
        return "";
    }

    public static String md5WithBase64(InputStream is) {
        if (is == null) {
            return "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //加密后的字符串
            byte[] bytes = StreamHelper.toByteArray(is);
            byte[] digest = md5.digest(bytes);
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("", e);
        }
        return "";
    }

    public static String md5WithBase64(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //加密后的字符串
            byte[] digest = md5.digest(bytes);
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("", e);
        }
        return "";
    }

    public static String encrypt(final String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-512");
            m.update(dataStr.getBytes());
            byte[] s = m.digest();
            StringBuilder result = new StringBuilder();
            for (byte b : s) {
                result.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return "";
    }

    public static String hash512(final String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-512");
            m.update(dataStr.getBytes());
            byte[] s = m.digest();
            StringBuilder result = new StringBuilder();
            for (byte b : s) {
                result.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return "";
    }

    public static String hash512(InputStream is) {
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-512");
            byte[] data = StreamHelper.toByteArray(is);
            m.update(data);
            byte[] s = m.digest();
            StringBuilder result = new StringBuilder();
            for (byte b : s) {
                result.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return "";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HashUtils.class);
}