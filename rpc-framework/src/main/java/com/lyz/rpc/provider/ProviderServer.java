package com.lyz.rpc.provider;

import com.lyz.rpc.core.InstanceInfo;
import com.lyz.rpc.protocol.ProtocolDecoder;
import com.lyz.rpc.protocol.ProtocolEncoder;
import com.lyz.rpc.protocol.ProtocolRequestHandler;
import com.lyz.rpc.registry.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供者服务器类
 * @author liyizhen
 * @date 2022/3/16
 */
@Slf4j
public class ProviderServer implements InitializingBean {
    private String host;
    private Integer port;
    private String instanceId;
    private String version;
    private Map<String, String> metadata;
    private RegistryService registryService;
    private RpcServiceRegistry rpcServiceRegistry;

    public ProviderServer(RegistryService registryService,
                          RpcServiceRegistry rpcServiceRegistry,
                          String host,
                          Integer port,
                          String instanceId,
                          String version,
                          Map<String, String> metadata) {
        this.registryService = registryService;
        this.rpcServiceRegistry = rpcServiceRegistry;
        this.host = host;
        this.port = port;
        this.instanceId = instanceId;
        this.version = version;
        this.metadata = metadata;
    }

    public void start() {
        register();
        startNettyService();

        System.out.println("start provider server");
    }

    private void register() {
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setHost(host);
        instanceInfo.setPort(port);
        instanceInfo.setInstanceId(instanceId);
        instanceInfo.setVersion(version);
        if (metadata == null) {
            instanceInfo.setMetadata(new HashMap<>());
        } else {
            instanceInfo.setMetadata(new HashMap<>(metadata));
        }

        try {
            registryService.register(instanceInfo);
        } catch (Exception e) {
            throw new RuntimeException("往注册中心注册错误", e);
        }
    }

    private void startNettyService() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolEncoder());
                            ch.pipeline().addLast(new ProtocolDecoder());
                            ch.pipeline().addLast(new ProtocolRequestHandler(rpcServiceRegistry));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(host, port)
                    .sync();
            log.info("netty 服务器启动成功！" + host + ":" + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException("netty 服务器错误", e);
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::start,"provider-server")
                .start();
    }
}
