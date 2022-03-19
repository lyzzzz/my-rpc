package com.lyz.demo.consumer;

import com.lyz.demo.rpc.service.HelloService;
import com.lyz.rpc.consumer.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {
    @RpcReference
    private HelloService helloService;

    @GetMapping({ "/", "" })
    public Mono<?> demo(@RequestParam String hello) {
        return Mono.fromCallable(() -> helloService.demo(hello));
    }
}
