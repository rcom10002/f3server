package info.knightrcom.util;

import java.io.UnsupportedEncodingException;

import org.apache.mina.util.Base64;

/**
 *
 */
public class EncryptionUtil {

// FIXME DROP THIS SECTION
//    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//
//    /**
//     * @param date
//     * @return
//     */
//    public static String toString(Date date) {
//        return dateFormat.format(date);
//    }

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
    
//    public static String stringfy(String style, String ... strings) {
//        return String.format("%1$s~%2$s~%3$s~%4$s", strings[0]);
//    }
}