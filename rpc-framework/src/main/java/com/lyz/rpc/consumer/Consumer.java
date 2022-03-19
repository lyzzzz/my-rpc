package com.lyz.rpc.consumer;

import com.lyz.rpc.core.InstanceInfo;
import com.lyz.rpc.protocol.Protocol;
import com.lyz.rpc.protocol.ProtocolDecoder;
import com.lyz.rpc.protocol.ProtocolEncoder;
import com.lyz.rpc.protocol.ProtocolResponseHandler;
import com.lyz.rpc.registry.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 消费器，利用 netty 对目标进行连接
 * @author liyizhen
 * @date 2022/3/18
 */
@Slf4j
public class Consumer {
    private final RegistryService registryService;
    private final Bootstrap bootstrap;
    private final NioEventLoopGroup nioEventLoopGroup;

    public Consumer(RegistryService registryService) {
        this.registryService = registryService;

        bootstrap = new Bootstrap();
        nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolDecoder());
                        ch.pipeline().addLast(new ProtocolEncoder());
                        ch.pipeline().addLast(new ProtocolResponseHandler());
                    }
                });
    }

    public void request(Protocol<Protocol.Request> protocol) throws InterruptedException {
        // 发现服务
        InstanceInfo instanceInfo = registryService.discovery();

        // 发起调用
        ChannelFuture channelFuture = bootstrap.connect(instanceInfo.getHost(), instanceInfo.getPort()).sync();
        channelFuture.addListener(arg0 -> {
            if (channelFuture.isSuccess()) {
                log.info("成功调用目标 host:{},port:{}", instanceInfo.getHost(), instanceInfo.getPort());
            } else {
                log.info("调用目标失败 host:{},port:{}", instanceInfo.getHost(), instanceInfo.getPort());
                channelFuture.cause().printStackTrace();
                nioEventLoopGroup.shutdownGracefully();
            }
        });
        channelFuture.channel().writeAndFlush(protocol);
    }
}
