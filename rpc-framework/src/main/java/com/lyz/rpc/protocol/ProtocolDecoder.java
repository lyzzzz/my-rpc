package com.lyz.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 协议解码器
 *
 *  magic 2bytes | version 1bytes | type 1bytes | status 1bytes | requestId 8bytes | dataLength 4bytes
 *  body...
 *
 * @author liyizhen
 */
@Slf4j
public class ProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();

        short magic = in.readShort();
        byte version = in.readByte();
        byte type = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();
        int dataLength = in.readInt();

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            log.info("剩余数据不足读取 body，返回");
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Protocol.Header header = new Protocol.Header();
        header.setMagic(magic);
        header.setVersion(version);
        header.setType(type);
        header.setStatus(status);
        header.setRequestId(requestId);

        Protocol.Type typeEnum = Protocol.Type.findByByte(type);
        switch (typeEnum) {
            case REQUEST:
                Protocol.Request request = JsonUtils.getObjectMapper().readValue(data, Protocol.Request.class);
                if (request != null) {
                    Protocol<Protocol.Request> protocol = new Protocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);

                    out.add(protocol);
                }
                break;
            case RESPONSE:
                Protocol.Response response = JsonUtils.getObjectMapper().readValue(data, Protocol.Response.class);
                if (response != null) {
                    Protocol<Protocol.Response> protocol = new Protocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);

                    out.add(protocol);
                }
                break;
        }
    }
}
