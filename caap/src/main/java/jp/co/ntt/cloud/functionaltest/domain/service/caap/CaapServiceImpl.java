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
package jp.co.ntt.cloud.functionaltest.domain.service.caap;

import com.amazonaws.services.elasticache.AmazonElastiCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CaapServiceImpl implements CaapService {

    private static final Logger logger = LoggerFactory.getLogger(CaapServiceImpl.class);

    @Inject
    ApplicationContext applicationContext;

    @Override
    public CaapProjectInfo inspectElastiCache() {

        final CaapProjectInfo caapProjectInfo = new CaapProjectInfo();

        final Class<AmazonElastiCache> elastiCacheClass = AmazonElastiCache.class;
        caapProjectInfo.setExistFQCNClasspath(
                isLoadableClass(elastiCacheClass.getCanonicalName()));

        caapProjectInfo.setExistInApplicationContext(containsInApplicationContext(elastiCacheClass));

        return caapProjectInfo;
    }

    private boolean containsInApplicationContext(Class<?> clazz) {
        return applicationContext.getBeanNamesForType(clazz).length > 0;
    }

    private boolean isLoadableClass(String fqcn) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(fqcn);
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("Can not find class.", e);
        }
        return false;
    }
}
