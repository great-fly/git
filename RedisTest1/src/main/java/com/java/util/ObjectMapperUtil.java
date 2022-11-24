package com.java.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 该工具类,主要的功能实现对象与JSON串的互相转化.
 * 1.对象转化为JSON
 * 2.JSON转化为对象
 */
public class ObjectMapperUtil {

    private static final ObjectMapper om = new ObjectMapper();

    //1.对象转化为JSON
    public static String toJSON(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //2.JSON转化为对象 要求用户传递什么类型就返回什么对象??
    public static <T> T toObj(String json,Class<T> target) {
        try {
            return om.readValue(json,target);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
