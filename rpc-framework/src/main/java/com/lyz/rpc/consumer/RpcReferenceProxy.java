package com.lyz.rpc.consumer;

import com.lyz.rpc.protocol.Protocol;
import com.lyz.rpc.registry.RegistryService;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * rpc 引用代理
 * @author liyizhen
 * @date 2022/3/18
 */
public class RpcReferenceProxy implements InvocationHandler {
    private final RegistryService registryService;
    private final Class<?> interfaceClass;
    private final String version;

    public RpcReferenceProxy(RegistryService registryService, Class<?> interfaceClass, String version) {
        this.registryService = registryService;
        this.interfaceClass = interfaceClass;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO 构造协议 发送请求 连接 consumer
        Protocol<Protocol.Request> protocol = new Protocol<>();
        Protocol.Header header = new Protocol.Header();
        Protocol.Request request = new Protocol.Request();
        protocol.setHeader(header);
        protocol.setBody(request);

        header.setMagic((short) 0x10);
        header.setVersion((byte) 0x1);
        header.setType((byte) Protocol.Type.REQUEST.getType());
        header.setStatus((byte)Protocol.Status.SUCCESS.getCode());
        header.setRequestId(RequestHolder.generateId());

        request.setVersion(version);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParams(args);
        request.setParamTypes(method.getParameterTypes());

        Consumer consumer = new Consumer(registryService);
        consumer.request(protocol);
        Promise<Protocol.Response> promise = new DefaultPromise<>(new DefaultEventLoop());
        RequestHolder.putRequest(header.getRequestId(), promise);

        return promise.get(1000, TimeUnit.MILLISECONDS).getData();
    }
}
