package com.tuanbaol.messageclient.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.management.relation.Relation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Description 字符串操作工具类
 * @ClassName StringUtil
 */
public class StringUtil {
    private final static String DEFAULT_DELIMITER = "\\{\\}";
    private final static Pattern PATTERN = Pattern.compile(DEFAULT_DELIMITER);

    /**
     * 判断多个字符串是否全部为blank
     *
     * @param params
     * @return
     */
    public static Boolean isBlankBoth(String... params) {
        if ((null == params) || (params.length == 0)) {
            return true;
        }
        for (String item : params) {
            if (!StringUtils.isBlank(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断多个字符串是否至少一个为blank
     *
     * @param params
     * @return
     */
    public static Boolean isBlankLeastone(String... params) {

        if (ArrayUtils.isEmpty(params)) {
            return true;
        }
        for (String item : params) {
            if (StringUtils.isBlank(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断多个字符串是否全部为empty
     *
     * @param params
     * @return
     */
    public static Boolean isEmptyBoth(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return true;
        }
        for (String item : params) {
            if (!StringUtils.isEmpty(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断多个字符串是否至少一个为empty
     *
     * @param params
     * @return
     */
    public static Boolean isEmptyLeastone(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return true;
        }
        for (String item : params) {
            if (StringUtils.isEmpty(item)) {
                return true;
            }
        }
        return false;
    }


    public static String format(String pattern, Object... params) {
        if (StringUtils.isBlank(pattern) || ArrayUtils.isEmpty(params)) {
            return pattern;
        }
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == '{') {
                if (i < pattern.length() - 1 && pattern.charAt(i + 1) == '}') {
                    sb.append(params[j++]);
                    i++;
                    continue;
                }
            }
            sb.append(pattern.charAt(i));
        }
        return sb.toString();
    }

    public static String formatByRegex(String pattern, Object... params) {
        int len;
        if (StringUtils.isBlank(pattern) || null == params || (len = params.length) <= 0) {
            return pattern;
        }
        Matcher matcher = PATTERN.matcher(pattern);
        for (int i = 0; matcher.find(); i++) {
            if (i > len - 1) {
                return pattern;
            }
            String param = params[i] == null ? "null" : params[i].toString();
            param = "【" + param + "】";
            pattern = PATTERN.matcher(pattern).replaceFirst(param);
        }
        return pattern;
    }

    public static String swapFirstAndEnd(String ori) {
        if (StringUtils.isBlank(ori) || ori.length() == 1) {
            return ori;
        }
        int length = ori.length();
        if (length == 2) {
            return ori.substring(1) + ori.substring(0, 1);
        }
        return ori.substring(length - 1) + ori.substring(1, length - 1) + ori.substring(0, 1);
    }

    public static String startWithReplace(String ori, String start, String repl) {
        if (isBlankLeastone(ori, start)) {
            return ori;
        }
        if (ori.startsWith(start)) {
            return orDefault(repl) + ori.substring(start.length());
        }
        return ori;
    }

    public static String endWithReplace(String ori, String end, String repl) {
        if (isBlankLeastone(ori, end)) {
            return ori;
        }
        if (ori.endsWith(end)) {
            return ori.substring(0, ori.length() - end.length()) + orDefault(repl);
        }
        return ori;
    }

    public static String orDefault(String ori) {
        return StringUtils.isBlank(ori) ? "" : ori;
    }

    //+++++++++++++++++++add symbolic around begin++++++++++++++++++
    public static String addSquare(String ori) {
        return "【" + orDefault(ori) + "】";
    }

    public static String addSquareEn(String ori) {
        return "[" + orDefault(ori) + "]";
    }

    public static String addParenthese(String ori) {
        return "（" + orDefault(ori) + "）";
    }

    public static String addParentheseEn(String ori) {
        return "(" + orDefault(ori) + ")";
    }

    public static String addAngle(String ori) {
        return "《" + orDefault(ori) + "》";
    }

    public static String addAngleEn(String ori) {
        return "<" + orDefault(ori) + ">";
    }
    //+++++++++++++++++++add symbolic around end++++++++++++++++++

    public static void main(String[] args) {
        System.out.println(endWithReplace("xx12","12","tt"));
        System.out.println(startWithReplace("xx12","xx","tt"));
    }
}
