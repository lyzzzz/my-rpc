package com.lyz.rpc.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // TODO json 序列化目前还有问题，需要对对象类型进行 1:1 转换，不然的话在远程调用中没法接受
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
