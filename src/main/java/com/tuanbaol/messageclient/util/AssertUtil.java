package com.tuanbaol.messageclient.util;

import com.tuanbaol.messageclient.exception.ServiceException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 断言器
 * @ClassName AssertUtil
 */
public final class AssertUtil {
    private final static String MSG_DEFAULT_SERVER_ERROR = "服务错误";

    private AssertUtil() {
    }

    /**
     * @param object
     * @param template
     * @param params
     * @Description: 对象为null断言器
     * @return: void
     */
    public static void notNull(Object object, String template, Object... params) {
        if (null == object) {
            String message = StringUtil.formatByRegex(template, params);
            throw new ServiceException(getShowMessage(message));
        }
    }

    /**
     * @param object
     * @param template
     * @param params
     * @Description: empty断言器
     * @return: void
     */
    public static void notEmpty(Object object, String template, Object... params) {
        notNull(object, template, params);
        boolean isEmpty = (object.getClass() == String.class && StringUtils.isBlank((CharSequence) object)) || ObjectUtils.isEmpty(object);
        if (isEmpty) {
            String message = StringUtil.formatByRegex(template, params);
            throw new ServiceException(getShowMessage(message));
        }
    }

    private static String getLogMessage(String msg) {
        String[] splits = msg.split("::");
        return splits.length > 1 ? splits[1] : splits[0];
    }

    private static String getShowMessage(String msg) {
        String[] splits = msg.split("::");
        return splits.length > 1 ? splits[0] : MSG_DEFAULT_SERVER_ERROR;
    }

    /**
     * 断言真（兼容方法）
     *
     * @param boo
     * @param template
     * @param params
     */
    public static void isTrue(Boolean boo, String template, Object... params) {
        if (!boo) {
            String message = StringUtil.formatByRegex(template, params);
            throw new ServiceException(getShowMessage(message));
        }
    }

    /**
     * 断言假
     *
     * @param boo
     * @param template
     * @param params
     */
    public static void isFalse(Boolean boo, String template, Object... params) {
        isTrue(!boo, template, params);
    }

}
