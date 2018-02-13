/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.app.common;

import javax.servlet.Filter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

/**
 * Bean登録(フィルター)クラス。
 * 本APではフィルターをweb.xmlを使用して登録しており、フィルターの２重登録を防止する為の暫定対応。
 * @author NTT 電電太郎
 */
public class DefaultFiltersBeanFactoryPostProcessor implements
                                                   BeanFactoryPostProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) bf;

        String[] beanNames = beanFactory.getBeanNamesForType(Filter.class);
        for (String beanName : beanNames) {
            BeanDefinition definition = BeanDefinitionBuilder
                    .genericBeanDefinition(FilterRegistrationBean.class)
                    .setScope(BeanDefinition.SCOPE_SINGLETON)
                    .addConstructorArgReference(beanName)
                    .addConstructorArgValue(new ServletRegistrationBean[] {})
                    .addPropertyValue("enabled", false).getBeanDefinition();

            beanFactory.registerBeanDefinition(beanName
                    + "FilterRegistrationBean", definition);
        }
    }
}
