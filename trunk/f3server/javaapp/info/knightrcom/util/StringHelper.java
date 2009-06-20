package info.knightrcom.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;

public class StringHelper {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String NUMBER_FORMAT = "###,###,###,###,###,##0.00";

    private static final SimpleDateFormat formatForDate;

    private static final SimpleDateFormat formatForTimeStamp;

    private static final DecimalFormat formatForDecimal;

    static {
        formatForDate = new SimpleDateFormat(DATE_FORMAT);
        formatForTimeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT);
        formatForDecimal = new DecimalFormat(NUMBER_FORMAT);

        formatForDate.setLenient(false);
        formatForTimeStamp.setLenient(false);
    }

    /**
     * 获取字符串字节长度
     * 
     * @param str
     * @return 字符串字节长度
     */
    public static int getByteLength(String str) {
        if (str == null) {
            return 0;
        }
        try {
            int length = str.getBytes().length;
            return length;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.length() == 0;
    }

    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 日期有效性校验
     * 
     * @param str
     * @return
     */
    public static boolean isDate(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            sdf.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 日期有效性校验
     * 
     * @param str
     * @return
     */
    public static boolean isTimeStamp(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
            sdf.setLenient(false);
            sdf.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 半角字符串校验
     * 
     * @param str
     * @return
     */
    public static boolean isSingleByte(String str) {
        if (str == null) {
            return false;
        }
        return str.length() == getByteLength(str);
    }

    /**
     * 半角数字校验
     * 
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        return str.matches("^[0-9]+$");
    }

    /**
     * 半角字母数字校验
     * 
     * @param str
     * @return
     */
    public static boolean isAlphaNumber(String str) {
        if (str == null) {
            return false;
        }
        if (isSingleByte(str)) {
            return str.matches("^\\w+$");
        } else {
            return false;
        }
    }

    /**
     * 空白字符串校验
     * 
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return str.matches("^\\s+$");
    }

    /**
     * 电子邮件地址校验
     * 
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        if (str == null) {
            return false;
        }
        return str.matches("^(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
    }

    /**
     * @param str
     *            日期格式字符串
     * @return
     */
    public static Calendar toDate(String str) {
        if (!isDate(str)) {
            return null;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatForDate.parse(str));
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param str
     *            日期格式字符串
     * @param format
     *            日期格式
     * @return
     */
    public static Calendar toDate(String str, String format) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatForDate.parse(str));
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param str
     *            时间戳格式字符串
     * @return
     */
    public static Timestamp toTimeStamp(String str) {
        if (!isTimeStamp(str)) {
            return null;
        }
        try {
            return new Timestamp(formatForTimeStamp.parse(str).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param str
     *            时间戳格式字符串
     * @param format
     *            时间戳格式
     * @return
     */
    public static Timestamp toTimeStamp(String str, String format) {
        try {
            return new Timestamp(new SimpleDateFormat(format).parse(str).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将Date类型的对象以"yyyy-MM-dd"形式，格式化输出
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * 将Date类型的对象以"yyyy-MM-dd HH:mm:ss"形式格式化输出
     * 
     * @param date
     * @return
     */
    public static String formatTimeStamp(Date date) {
        return formatForTimeStamp.format(date);
    }

    /**
     * 将数字格式化为"###,###,###,###,###,##0.00"形式。<br>
     * "0"代表数字，数字缺失时显示零；"#"在数字缺失时不显示内容。
     * 
     * @param number
     * @return
     */
    public static String formatNumber(BigDecimal number) {
        return formatForDecimal.format(number);
    }

    /**
     * 将数字格式化为"###,###,###,###,###,##0.00"形式。<br>
     * "0"代表数字，数字缺失时显示零；"#"在数字缺失时不显示内容。
     * 
     * @param number
     * @return
     */
    public static String formatNumber(Double number) {
        return formatForDecimal.format(number);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String numberChars = "1234567890";
        String example = "xy5dew_dp6n";
        int offset = 0;
        int copyTimes = 0;
        for (int i = 0; i < example.length(); i++) {
            if (numberChars.indexOf(example.charAt(i)) > -1) {
                // 计算数字偏移量
                offset++;
                System.out.print(example.charAt(i));
                continue;
            } else if (offset > 0) {
                // 计算重复次数
                copyTimes = new Integer(example.substring(i - offset, i)).intValue();
            }
            if (copyTimes > 0) {
                // 按照指定次数进行字符输出
                for (int x = 0; x < copyTimes; x++) {
                    System.out.print(String.valueOf(example.charAt(i)));
                }
                // 重置计数变量
                copyTimes = 0;
                offset = 0;
            } else if ('_' == example.charAt(i)) {
                // _ 替换 @
                System.out.print('@');
            } else {
                // 以常规方法输出字符
                System.out.print(example.charAt(i));
            }
        }
    }

    /**
     * 模糊查询中条件为通配符的字符进行处理
     * 
     * @param str
     * @return
     */
    public static String escapeSQL(String str) {
        if (!isEmpty(str)) {
            return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
        }
        return null;
    }

    /**
     * 四舍五入
     * 
     * @param dbl
     * @param scale
     * @return
     */
    public static double round(double dbl, int scale) {
        long temp = 1;
        for (int i = scale; i > 0; i--) {
            temp *= 10;
        }
        dbl *= temp;
        long dl = Math.round(dbl);
        return (double) (dl) / temp;
    }
}
