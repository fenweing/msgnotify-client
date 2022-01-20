package com.tuanbaol.messageclient.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuanbaol.messageclient.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description Json反序列化和序列化工具类
 * @ClassName JsonUtil
 */
public class JsonUtil {
    private JsonUtil() {
    }

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static <T, G> T toObjectGeneric(String jsonString, Class<T> mainClazz, Class<G> genericClazz) {
        if (StringUtils.isEmpty(jsonString)) {
            throw new ServiceException("待反序列化json字符串为空");
        }
        try {
            ObjectMapper genericMapper = new ObjectMapper();
            JavaType type = genericMapper.getTypeFactory().constructParametricType(mainClazz, genericClazz);
            return genericMapper.readValue(jsonString, type);
        } catch (Exception e) {
            throw new ServiceException(e, "json字符串反序列化失败，jsonString-【{}】", jsonString);
        }
    }

    public static <T> T toObject(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            throw new ServiceException("待反序列化json字符串为空");
        }
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new ServiceException(e, "json字符串反序列化失败，jsonString-【{}】", jsonString);
        }
    }

    public static String toString(Object sourceObject) {
        AssertUtil.notNull(sourceObject, "待序列化对象为空");
        try {
            return objectMapper.writeValueAsString(sourceObject);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e, "对象序列化失败，sourceObject-[%s]", sourceObject.toString());
        }
    }

}
