package com.lyz.rpc.consumer;

import com.lyz.rpc.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 对 rpc 引用的 beanFactory 处理器
 * 自动创建代理类
 * @author liyizhen
 * @date 2022/3/18
 */
@Slf4j
public class RpcReferenceBeanFactoryPostProcessor implements BeanFactoryPostProcessor, BeanClassLoaderAware, ApplicationContextAware {
    private final RegistryService registryService;
    private ClassLoader classLoader;
    private ApplicationContext applicationContext;
    private Map<String, BeanDefinition> rpcReferenceBeanDefinitions = new HashMap<>();

    public RpcReferenceBeanFactoryPostProcessor(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] beanDefinitionNames = configurableListableBeanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                ReflectionUtils.doWithFields(clazz, this::parseField);
            }
        }

        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableListableBeanFactory;
        rpcReferenceBeanDefinitions.forEach((k, v) -> {
            if (applicationContext.containsBean(k)) {
                throw new RuntimeException("已存在 RpcReference Bean:" + k);
            }

            beanDefinitionRegistry.registerBeanDefinition(k, v);
            log.info("成功注册 RpcReference 类 {}", k);
        });
    }

    private void parseField(Field field) {
        RpcReference annotation = field.getAnnotation(RpcReference.class);
        if (annotation != null) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(RpcReferenceProxyFactoryBean.class);
            beanDefinitionBuilder.setInitMethodName("init");
            beanDefinitionBuilder.addPropertyValue("interfaceClass", field.getType());
            beanDefinitionBuilder.addPropertyValue("version", annotation.version());

            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            rpcReferenceBeanDefinitions.put(field.getName(), beanDefinition);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
