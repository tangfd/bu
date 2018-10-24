package com.tfd.base.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;

/**
 * 对于java的加密解密操作类
 *
 * @author TangFD@HF 2018/2/24
 */
public class CryptoUtils {
    public static final Log LOG = LogFactory.getLog(CryptoUtils.class);
    private static final String CHARSET = "UTF-8";
    private static final String DIGEST_MD5 = "MD5";
    private static final String DIGEST_SHA_256 = "SHA-256";
    private static final String DIGEST_SHA_512 = "SHA-512";
    private static final String AES_TYPE = "AES/ECB/PKCS5Padding";
    private static final String DEFAULT_SALT = "~!@#$%^&*()_+=-,";
    private static final int BASE64_LOOP_COUNT = 5;
    private static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String aesEncrypt(String express) {
        return aesEncrypt(DEFAULT_SALT, express);
    }

    public static String aesEncrypt(String salt, String express) {
        try {
            Key key = generateKey(salt);
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypt = cipher.doFinal(express.getBytes());
            return parseByte2HexStr(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String aesDecrypt(String express) {
        return aesDecrypt(DEFAULT_SALT, express);
    }

    public static String aesDecrypt(String salt, String express) {
        try {
            Key key = generateKey(salt);
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypt = cipher.doFinal(parseHexStr2Byte(express));
            return new String(decrypt).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Key generateKey(String key) throws Exception {
        return new SecretKeySpec(key.getBytes(CHARSET), "AES");
    }

    /**
     * 将二进制转换成16进制
     */
    public static String parseByte2HexStr(byte bytes[]) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (StringUtils.isEmpty(hexStr)) {
            return new byte[0];
        }

        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String sha256Encrypt(String express) {
        return shaEncrypt(express, DIGEST_SHA_256);
    }

    public static String sha512Encrypt(String express) {
        return shaEncrypt(express, DIGEST_SHA_512);
    }

    public static String shaEncrypt(String express, String algorithm) {
        if (StringUtils.isEmpty(express) || StringUtils.isEmpty(algorithm)) {
            return express;
        }

        try {
            MessageDigest messageDigest = getMessageDigest(express, algorithm);
            byte[] byteBuffer = messageDigest.digest();
            return parseByte2HexStr(byteBuffer);
        } catch (Exception e) {
            LOG.error("Can't create message encoder!", e);
        }

        return null;
    }

    public static String base64Encrypt(String express) {
        if (StringUtils.isEmpty(express)) {
            return null;
        }

        try {
            BASE64Encoder encoder = new BASE64Encoder();
            String result = encoder.encode(express.getBytes(CHARSET));
            for (int i = 0; i < BASE64_LOOP_COUNT; i++) {
                result = encoder.encode(result.getBytes(CHARSET));
            }
            return result.replaceAll("\r\n", "");
        } catch (Exception e) {
            LOG.error("Can't create message encoder!", e);
        }

        return null;
    }

    public static String base64Decrypt(String express) {
        if (StringUtils.isEmpty(express)) {
            return null;
        }

        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(express);
            for (int i = 0; i < BASE64_LOOP_COUNT; i++) {
                bytes = decoder.decodeBuffer(new String(bytes, CHARSET));
            }
            return new String(bytes, CHARSET);
        } catch (Exception e) {
            LOG.error("Can't create message encoder!", e);
        }

        return null;
    }

    public static String md5Encrypt(String express) {
        if (StringUtils.isEmpty(express)) {
            return null;
        }

        try {
            MessageDigest md = getMessageDigest(express, DIGEST_MD5);
            byte[] byteDigest = md.digest();
            return hexDigest(byteDigest).toLowerCase();
        } catch (Exception e) {
            LOG.error("Can't create message digest!", e);
        }

        return null;
    }

    /**
     * 创建加密对象
     */
    private static MessageDigest getMessageDigest(String express, String algorithm) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(express.getBytes(CHARSET));
        return messageDigest;
    }

    public static String hexDigest(byte[] byteDigest) {
        char[] chars = new char[byteDigest.length * 2];
        for (int i = 0; i < byteDigest.length; i++) {
            chars[i * 2] = HEX_DIGITS[byteDigest[i] >> 4 & 0x0F];
            chars[i * 2 + 1] = HEX_DIGITS[byteDigest[i] & 0x0F];
        }

        return new String(chars);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("-------------MD5---------------------------");
        System.out.println(md5Encrypt("000000"));
        System.out.println("-------------SHA256---------------------------");
        System.out.println(sha256Encrypt("hello crypto"));
        System.out.println("-------------SHA512---------------------------");
        System.out.println(sha512Encrypt("hello crypto"));
        System.out.println("-------------BASE64---------------------------");
        System.out.println(base64Encrypt("abcdefgabcdefg12"));
        System.out.println(base64Decrypt(base64Encrypt("abcdefgabcdefg12")));
        System.out.println("-------------AES---------------------------");
        System.out.println(aesEncrypt("VmpGYVYyRXhXWGxVV0d4VVlUSm9VVlZxU2xOWlZsSlZVVzVhVGxadGRETlpWVnBQWVRGd05rMUVhejA9"));
        System.out.println(aesDecrypt(aesEncrypt("VmpGYVYyRXhXWGxVV0d4VVlUSm9VVlZxU2xOWlZsSlZVVzVhVGxadGRETlpWVnBQWVRGd05rMUVhejA9")));
    }
}
