// src/main/java/com/capstone/pickIt/global/config/SpringDocQuerydslDisableConfig.java

package com.capstone.pickIt.global.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocQuerydslDisableConfig implements BeanDefinitionRegistryPostProcessor {

    private static final String SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME =
            "queryDslQuerydslPredicateOperationCustomizer";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (registry.containsBeanDefinition(SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME)) {
            registry.removeBeanDefinition(SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
