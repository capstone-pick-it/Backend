package com.capstone.pickIt.global.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "springdoc.querydsl-customizer-disable.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SpringDocQuerydslDisableConfig implements BeanDefinitionRegistryPostProcessor {

    private static final String SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME =
            "queryDslQuerydslPredicateOperationCustomizer";

    private static final String SPRINGDOC_QUERYDSL_CUSTOMIZER_CLASS_NAME =
            "org.springdoc.core.customizers.QuerydslPredicateOperationCustomizer";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (registry.containsBeanDefinition(SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME)) {
            registry.removeBeanDefinition(SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME);
            return;
        }

        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            String beanClassName = beanDefinition.getBeanClassName();

            if (SPRINGDOC_QUERYDSL_CUSTOMIZER_CLASS_NAME.equals(beanClassName)
                    || (beanClassName != null && beanClassName.endsWith("QuerydslPredicateOperationCustomizer"))) {
                registry.removeBeanDefinition(beanName);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
