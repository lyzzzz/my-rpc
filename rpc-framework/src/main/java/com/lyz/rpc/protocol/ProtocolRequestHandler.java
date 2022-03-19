package com.lyz.rpc.protocol;

import com.lyz.rpc.provider.RpcServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class ProtocolRequestHandler extends SimpleChannelInboundHandler<Protocol<Protocol.Request>> {
    private final RpcServiceRegistry rpcServiceRegistry;

    public ProtocolRequestHandler(RpcServiceRegistry rpcServiceRegistry) {
        this.rpcServiceRegistry = rpcServiceRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol<Protocol.Request> msg) throws Exception {
        Protocol<Protocol.Response> responseProtocol = new Protocol<>();
        Protocol.Header responseHeader = msg.getHeader();
        Protocol.Response responseBody = new Protocol.Response();
        responseProtocol.setHeader(responseHeader);
        responseProtocol.setBody(responseBody);

        responseHeader.setType((byte) Protocol.Type.RESPONSE.getType());
        try {
            Object data = handle(msg.getBody());
            responseBody.setData(data);
            responseHeader.setStatus((byte) Protocol.Status.SUCCESS.getCode());
        } catch (Exception e) {
            responseHeader.setStatus((byte) Protocol.Status.FAIL.getCode());
            responseBody.setMessage(e.toString());

            log.error("处理请求信息错误", e);
        }

        ctx.writeAndFlush(responseProtocol);
    }

    private Object handle(Protocol.Request request) throws InvocationTargetException {
        Object rpcService = rpcServiceRegistry.getRpcService(request.getClassName(), request.getVersion())
                .orElseThrow(() -> new IllegalArgumentException("找不到对应的 rpc 服务"));

        FastClass fastClass = FastClass.create(rpcService.getClass());
        int methodIndex = fastClass.getIndex(request.getMethodName(), request.getParamTypes());
        return fastClass.invoke(methodIndex, rpcService, request.getParams());
    }
}
