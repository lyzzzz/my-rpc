package com.lyz.rpc.protocol;

import com.lyz.rpc.consumer.RequestHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProtocolResponseHandler extends SimpleChannelInboundHandler<Protocol<Protocol.Response>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol<Protocol.Response> msg) throws Exception {
        RequestHolder.successRequest(msg.getHeader().getRequestId(), msg.getBody());
    }
}
