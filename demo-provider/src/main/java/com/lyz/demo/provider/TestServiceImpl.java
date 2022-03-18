package com.lyz.demo.provider;

import com.lyz.demo.facade.TestService;
import com.lyz.rpc.provider.RpcService;

/**
 * @author liyizhen
 * @date 2022/3/17
 */
@RpcService(interfaceClass = TestService.class, version = "1.0.0")
public class TestServiceImpl implements TestService {
}
