# My-RPC 框架

基于 Java + Netty + Spring 实现的一个最基础的 RPC 框架，满足 RPC 框架的最低要求，实现了以下功能：

* 集成注册中心
* 基于 Netty 实现节点之间的网络传输，实现自定义通讯协议
* 实现服务提供者、服务消费者的架构
* 使用动态代理，自动生成通信类
* 与 Spring Boot 集成，开箱即用



## 架构

```mermaid
graph BT
  consumer[服务消费者 Consumer]
  provider[服务提供者 Provider]
  interface[服务接口 Facade]
  registry[注册中心]
  consumer -.直接连接.-> provider
  consumer -.注册--> registry
  provider -.注册--> registry
  provider -.实现.-> interface
  consumer -.引用.-> interface
```

1. 服务消费者 Consumer 引用相关的服务接口，服务提供者 Provider 实现相关的服务接口
2. 服务消费者 Consumer、服务提供者 Provider 向注册中心注册
3. 服务消费者 Consumer 需要消费服务提供者 Provider 对应的服务接口
4. 服务端消费者 Consumer 从注册中心中找到对应的服务提供者 Provider，并直接连接，调用相关业务



## 模块说明

rpc-framework：核心的框架部分，耦合 Spring Boot、Netty 等基础组件，提供 RPC 框架服务。

memory-registry：一个 Demo 性质的内存版本注册中心，免得再引用其他第三方注册中心了。

demo-provider：使用 RPC 框架构建的服务提供者。

demo-consumer：使用 RPC 框架构建的服务消费者。

demo-facade：业务接口。



## 传输协议

```
Header (17bytes): magic 2bytes | version 1bytes | type 1bytes | status 1bytes | requestId 8bytes | dataLength 4bytes
Body: ? bytes
```

Header 部分：

用于声明一些元信息。

* magic（2 bytes）：魔法数，用于协议校验与防止粘包，固定为 0x10
* version（1 bytes）：协议版本，用于标记服务版本，目前为 0x1，没有作处理
* type（1 bytes）：包类型，目前有请求、返回两种
* status（1 bytes）：标记这个数据包是否发生了成功与错误
* requestId（8 bytes）：用于追踪和标记该次请求的唯一 ID
* dataLength（4 bytes）：Body 数据长度



Body 部分：

用于存放真正的数据，大小不定。



## 使用

### 定义服务接口

定义供给服务提供者和服务消费者的接口：

```java
public interface HelloService {
    String demo(String hello);
}
```



### 开启服务提供者

1. 在 Spring Boot 配置 application.yml 中增加如下配置：

```yml
rpc:
  registry:
    enabled: true
    type: MEMORY # 注册中心类型，目前只支持 MEMORY
    config:
      host: 127.0.0.1 # 注册中心地址
      port: 8070 # 注册中心端口
  provider:
    enabled: true
    host: 127.0.0.1 # 服务提供者的地址
    port: 8071 # 服务提供者的端口
    instance-id: test-provider # 服务提供者的 id
```

2. 实现相关服务，并用 @RpcService 注解来声明实现：

```java
@RpcService(interfaceClass = HelloService.class, version = "1.0.0") // 标记服务接口，服务版本
public class HelloServiceImpl implements HelloService { // 实现服务接口
    @Override
    public String demo(String hello) {
        return new Random().nextInt() + hello;
    }
}
```



### 开启服务消费者

1. 在 Spring Boot 配置 application.yml 中增加如下配置：

```yml
rpc:
  registry:
    enabled: true
    type: MEMORY # 注册中心类型，目前只支持 MEMORY
    config:
      host: 127.0.0.1 # 注册中心地址
      port: 8070 # 注册中心端口
  consumer:
    enabled: true # 开启服务消费者功能
```

2. 在业务类中引用 Rpc 服务，使用 @RpcReference 注解：

```java
@RestController
public class HelloController {
    @RpcReference // 引用 RPC 服务
    private HelloService helloService;

    @GetMapping({ "/", "" })
    public Mono<?> demo(@RequestParam String hello) {
        return Mono.fromCallable(() -> helloService.demo(hello));
    }
}
```



## TODO

* 负载均衡算法
* 支持多种序列化协议
* 支持多种第三方注册中心
* 注册中心服务发现
* 注册中心服务掉线
* Netty Channel 复用
* 更好的容错处理



## 原理分析

### 注册中心

使用 Spring Boot 的条件注入定义注册中心的开关。

注册中心的要点：

* 要设计足够的抽象，并且能够提供统一的调用门面。

流程：

1. 使用 FactoryBean，利用工厂模式创建注册中心服务
2. 抽象注册中心接口，并内置多实现
3. 创建注册中心时，使用 Binder 类，进行多种种类的注册中心配置读取



### 服务提供者

使用 Spring Boot 的条件注入定义服务提供者的开关。

服务提供者的要点：

* 将相关的 RPC 服务注册进 RPC 框架。
* 启动一个 Netty 服务器，Netty 服务器可以处理对方发送过来的协议，并找到调用方法，并给出符合协议的响应。

流程：

1. 处理 @RpcService 的 Bean
   1. 使用 BeanPostProcessor 处理每一个 Bean，如果有相关注解，则在 RPC 框架中添加相关的 RPC 服务（可能是一个 Map，以便后续能够进行 RPC 服务的查找）
2. 启用 RPC 服务提供服务者
   1. 启动时，往注册中心注册
   2. 启动 Netty 服务器
   3. Netty 服务需要包括三个关键的处理逻辑：
      1. 使用协议 Decoder 将网络上的协议转换为 Java 类，以便请求处理器处理
      2. 请求处理器找到相关的 RPC 服务业务方法进行调用
      3. 使用协议 Encoder 将步骤 2 中获得的结果返回成网络上的协议



### 服务消费者

使用 Spring Boot 的条件注入定义服务消费者的开关。

服务消费者的要点：

* 读取到相关的 RPC 服务引用，使用动态代理技术创建相关的代理类，代理类的逻辑负责真正调用远程方法。
* 调用远程方法时，需要将内存中的 Java 类转换为网络上的协议，然后收到相关协议，转换为响应的类。

流程：

1. 读取相关 RPC 服务引用，处理有 @RpcReference 的 Bean
   1. 使用 BeanFactoryPostProcessor 读取初始化完的 Bean，如果有相关注解，创建相关的代理类
   2. 利用 Spring FactoryBean 工厂模式，创建动态代理类
   3. 创建真正的代理类，代理类需要可以访问注册中心，并且可以调用 Netty 方面的逻辑
2. 服务消费者的 Netty 调用端，需要包括三个关键的处理逻辑：
   1. 使用协议 Encoder 将 Java 类转换为网络上的协议，以便服务提供者处理
   2. 使用协议 Decoder 接受网络上的响应协议，将其转换为 Java 类
   3. 使用 Netty 自带的 Promise 类维持每个请求 ID 的请求状态，超过时间则进行超时



## 联系我

如有问题，可以提出 issue，或私信我个人主页上的邮箱。
