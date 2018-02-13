/*
 * Copyright 2014-2018 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
