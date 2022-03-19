package com.lyz.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Protocol<E> implements Serializable {
    private Header header;
    private E body;

    @Data
    public static class Header implements Serializable {
        private short magic;
        private byte version;
        private byte type;
        private byte status;
        private long requestId;
    }

    @Data
    public static class Request implements Serializable {
        private String version;
        private String className;
        private String methodName;
        private Object[] params;
        private Class<?>[] paramTypes;
    }

    @Data
    public static class Response implements Serializable {
        private Object data;
        private String message;
    }

    public enum Type implements Serializable {
        REQUEST(1), RESPONSE(2);

        @Getter
        private int type;

        Type(int type) {
            this.type = type;
        }

        public static Type findByByte(byte type) {
            Type[] values = Type.values();
            for (Type value : values) {
                if (value.getType() == (int) type) {
                    return value;
                }
            }
            throw new IllegalArgumentException("错误的消息 type");
        }
    }

    public enum Status implements Serializable {
        SUCCESS(0), FAIL(1);

        @Getter
        private int code;

        Status(int code) {
            this.code = code;
        }
    }
}
