package com.lyz.demo.provider;

import com.lyz.demo.rpc.service.HelloService;
import com.lyz.rpc.provider.RpcService;

import java.util.Random;

/**
 * @author liyizhen
 * @date 2022/3/17
 */
@RpcService(interfaceClass = HelloService.class, version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String demo(String hello) {
        return new Random().nextInt() + hello;
    }
}
