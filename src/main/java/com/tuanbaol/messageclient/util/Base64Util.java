package com.tuanbaol.messageclient.util;

import com.tuanbaol.messageclient.constant.CommonConstant;
import com.tuanbaol.messageclient.exception.ServiceException;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Util {
    private final static String UTF8 = CommonConstant.CHARSET_UTF8;
    private final static Base64 BASE64 = new Base64();

    public static String encode(String src) {
        return src == null ? null : BASE64.encodeAsString(getBytes(src));
    }

    public static String decode(String encoded) {
        try {
            return encoded == null ? null : new String(BASE64.decode(encoded), UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("decode base64 failed,encoded:" + encoded, e);
        }
    }

    private static byte[] getBytes(String src) {
        try {
            return src == null ? null : src.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("get bytes failed,src:" + src, e);
        }

    }
}
