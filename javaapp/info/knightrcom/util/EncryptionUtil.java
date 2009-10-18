package info.knightrcom.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.apache.mina.util.Base64;

/**
 * Encryption Utility
 */
public class EncryptionUtil {

    /**
     * @param str
     * @return
     */
    public static String Base64Encode(String str) {
        return Base64Encode(getBytes(str));
    }

    /**
     * @param bytes
     * @return
     */
    public static String Base64Encode(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * @param str
     * @return
     */
    public static String Base64Decode(String str) {
        return Base64Decode(getBytes(str));
    }

    /**
     * @param bytes
     * @return
     */
    public static String Base64Decode(byte[] bytes) {
        return new String(Base64.decodeBase64(bytes));
    }

    /**
     * @param source
     * @return
     */
    public static String encryptSHA(String source) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA");
			digest.update(getBytes(source));
			return new String(EncryptionUtil.Base64Encode(digest.digest()));
		} catch (Exception e) {
			return null;
		}
	}

    /**
     * @param source
     * @return
     */
    public static String encryptMD5(String source) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(getBytes(source));
			return new String(EncryptionUtil.Base64Encode(digest.digest()));
		} catch (Exception e) {
			return null;
		}
	}

    /**
     * @param obj
     * @return
     */
    private static byte[] getBytes(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        try {
            return obj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return obj.toString().getBytes();
        }
    }
}
