package com.lyz.rpc.registry;

import com.lyz.rpc.core.InstanceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 内存类型注册中心服务
 * @author liyizhen
 * @date 2022/3/14
 */
public class MemoryRegistryService implements RegistryService {
    private String host;
    private Integer port;

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public MemoryRegistryService(MemoryRegistryConfig config) {
        this.host = config.getHost();
        this.port = config.getPort();
    }

    @Override
    public void register(InstanceInfo instanceInfo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        REST_TEMPLATE.postForObject("http://" + host + ":" + port + "/register",
                new HttpEntity<>(new RegisterInstance(instanceInfo.getInstanceId(),
                        instanceInfo.getVersion(),
                        instanceInfo.getVersion(),
                        instanceInfo.getPort(),
                        instanceInfo.getMetadata()),
                        httpHeaders),
                Void.class);
    }

    @Override
    public void unregister(InstanceInfo instanceInfo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        REST_TEMPLATE.postForObject("http://" + host + ":" + port + "/unregister",
                new HttpEntity<>(new UnregisterInstance(instanceInfo.getInstanceId()),
                        httpHeaders),
                Void.class);
    }

    @Override
    public void destroy() {

    }

    @Override
    public InstanceInfo discovery() {
        // TODO 需要根据服务调用信息进行实例发现
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setHost("127.0.0.1");
        instanceInfo.setPort(8071);
        return instanceInfo;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RegisterInstance {
        private String instanceId;
        private String version;
        private String host;
        private Integer port;
        private Map<String, String> metadata;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UnregisterInstance {
        private String instanceId;
    }
}
