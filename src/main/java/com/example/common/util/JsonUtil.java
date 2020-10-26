package com.example.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : JsonUtil
 * @Description : jackson工具类
 * @Author : zhangh
 * @Date: 2020-07-22 11:18
 */
@Slf4j
@Configuration
public class JsonUtil {

    private static ObjectMapper mapper;

    private static final String EMPTY = "";

    @Bean
    public ObjectMapper objectMapper() {
        return mapper;
    }

    /**
     * 设置一些通用的属性
     */
    static {
        mapper = new ObjectMapper();
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略空值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //如果是空对象的时候,不抛异常
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 允许key有单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许整数以0开头
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        // 允许字符串中存在回车换行控制符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        ModelFieldNamer modelFieldNamer = new ModelFieldNamer();
        mapper.setAnnotationIntrospector(modelFieldNamer);
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(timeModule);
    }


    public static String toJSONString(Object obj) {
        return toJSONString(obj, false);
    }

    public static String toFormatJSONString(Object obj) {
        return toJSONString(obj, true);
    }

    private static String toJSONString(Object obj, boolean format) {
        try {
            if (obj == null) {
                return EMPTY;
            }
            if (obj instanceof String) {
                return obj.toString();
            }
            if (obj instanceof Number) {
                return obj.toString();
            }
            if (format) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("JsonUtil toJSONString Exception,Param:{}", obj.toString(), e);
            RuntimeException runtimeException = new RuntimeException("JsonUtil toJSONString Exception");
            runtimeException.initCause(e);
            throw runtimeException;

        }
    }


    public static <T> T parseObject(String value, Class<T> tClass) {
        try {
            if (StringUtils.isEmpty(value) || tClass == null) {
                return null;
            }
            return tClass.equals(String.class) ? (T) value : mapper.readValue(value, tClass);
        } catch (Exception e) {
            log.error("JsonUtil parseObject Exception,Param:{}", value, e);
            RuntimeException runtimeException = new RuntimeException("JsonUtil parseObject Exception");
            runtimeException.initCause(e);
            throw runtimeException;
        }
    }

    public static <T> T parseList(String value, Class<T> tClass) {
        try {
            if (StringUtils.isEmpty(value) || tClass == null) {
                return null;
            }
            return tClass.equals(String.class) ? (T) value : mapper.readValue(value, new TypeReference<List<T>>() {
            });
        } catch (Exception e) {
            log.error("JsonUtil parseObject Exception,Param:{}", value, e);
            RuntimeException runtimeException = new RuntimeException("JsonUtil parseList Exception");
            runtimeException.initCause(e);
            throw runtimeException;
        }
    }

    public static Map<String, Object> parseMap(String value) {
        try {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return mapper.readValue(value, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("JsonUtil parseObject Exception,Param:{}", value, e);
            RuntimeException runtimeException = new RuntimeException("JsonUtil parseObject Exception");
            runtimeException.initCause(e);
            throw runtimeException;
        }
    }

    public static <T> List<T> mapList2BeanList(List<Map<String, Object>> mapList, Class<T> tClass) {
        if (mapList == null || tClass == null) {
            return null;
        }
        if (mapList.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<T> resultList = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            resultList.add(mapper.convertValue(map, tClass));
        }
        return resultList;
    }
}