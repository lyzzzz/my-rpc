package com.lyz.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 协议编码器
 *
 * magic 2bytes | version 1bytes | type 1bytes | status 1bytes | requestId 8bytes | dataLength 4bytes
 * body...
 *
 * @author liyizhen
 */
public class ProtocolEncoder extends MessageToByteEncoder<Protocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol<Object> msg, ByteBuf out) throws Exception {
        Protocol.Header header = msg.getHeader();
        out.writeShort(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeByte(header.getType());
        out.writeByte(header.getStatus());
        out.writeLong(header.getRequestId());

        byte[] body = JsonUtils.getObjectMapper().writeValueAsBytes(msg.getBody());
        out.writeInt(body.length);
        out.writeBytes(body);
    }
}
