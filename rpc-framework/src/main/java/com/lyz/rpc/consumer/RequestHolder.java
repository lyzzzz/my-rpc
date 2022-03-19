package com.lyz.rpc.consumer;

import com.lyz.rpc.protocol.Protocol;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RequestHolder {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final ConcurrentHashMap<Long, Promise<Protocol.Response>> PROMISES = new ConcurrentHashMap<>();

    public static long generateId() {
        return ID_GENERATOR.incrementAndGet();
    }

    public static void putRequest(long requestId, Promise<Protocol.Response> promise) {
        PROMISES.put(requestId, promise);
    }

    public static void successRequest(long requestId, Protocol.Response result) {
        Promise<Protocol.Response> promise = PROMISES.get(requestId);
        if (promise == null) {
            throw new IllegalArgumentException("promise 为空 requestId:" + requestId);
        }
        promise.setSuccess(result);
        PROMISES.remove(requestId);
    }
}
