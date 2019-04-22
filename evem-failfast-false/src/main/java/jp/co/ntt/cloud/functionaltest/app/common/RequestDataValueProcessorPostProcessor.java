/*
 * Copyright(c) 2017 NTT Corporation.
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.terasoluna.gfw.web.mvc.support.CompositeRequestDataValueProcessor;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenRequestDataValueProcessor;

/**
 * トランザクショントークンチェック有効化用PostProcessor
 * @author NTT 電電太郎
 */
public class RequestDataValueProcessorPostProcessor implements
                                                    BeanDefinitionRegistryPostProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanDefinitionRegistry(
            BeanDefinitionRegistry registry) throws BeansException {
        ConstructorArgumentValues cav = new ConstructorArgumentValues();
        List<RequestDataValueProcessor> values = new ArrayList<RequestDataValueProcessor>();
        values.add(new TransactionTokenRequestDataValueProcessor());
        values.add(new CsrfRequestDataValueProcessor());
        cav.addGenericArgumentValue(values);
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(CompositeRequestDataValueProcessor.class, cav, null);

        registry.removeBeanDefinition("requestDataValueProcessor");
        registry.registerBeanDefinition("requestDataValueProcessor",
                rootBeanDefinition);
    }
}
