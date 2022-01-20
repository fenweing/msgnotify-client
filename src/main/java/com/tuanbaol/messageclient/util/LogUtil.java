package com.tuanbaol.messageclient.util;


/**
 * @version 1.0
 */
public class LogUtil {

    private LogUtil() {
    }

    public static String addTime(String ori) {
        return StringUtil.addSquareEn(DateUtils.formatNowYmdhms()) + StringUtil.orDefault(ori);
    }

    public static String format(String ori, Object... params) {
        return StringUtil.formatByRegex(ori, params);
    }

    public static String addTimeFormat(String ori, Object... params) {
        return addTime(format(ori, params));
    }

    public static void main(String[] args) {
        System.out.println(addTimeFormat("xx-{},yy-{}", 1, 2));
    }
}
