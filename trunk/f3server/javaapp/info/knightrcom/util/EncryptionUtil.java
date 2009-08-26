package info.knightrcom.util;

import java.io.UnsupportedEncodingException;

import org.apache.mina.util.Base64;

/**
 *
 */
public class EncryptionUtil {

    /**
     * @param str
     * @return
     */
    public static String Base64Encode(String str) {
        return new String(Base64.encodeBase64(getBytes(str)));
    }

    /**
     * @param str
     * @return
     */
    public static String Base64Decode(String str) {
        return new String(Base64.decodeBase64(getBytes(str)));
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
