/*
 * Copyright 2014-2017 NTT Corporation.
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
 *
 */
package jp.co.ntt.cloud.functionaltest.domain.service.product;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessages;

import jp.co.ntt.cloud.functionaltest.domain.model.Product;
import jp.co.ntt.cloud.functionaltest.domain.repository.product.ProductRepository;

@Service
public class ProductService {

    @Inject
    ProductRepository productRepository;

    /**
     * 全商品を取得する。
     *
     * @param categoryId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.selectAll();
    }

    /**
     * 指定した商品を取得し、CartItemにして返す。
     *
     * @param productId
     * @return
     */
    @Transactional(readOnly=true)
    public Product findOne(String productId) {
        Product product = productRepository.findOne(productId);

        if (Objects.isNull(product)) {
            ResultMessages messages = ResultMessages.error();
            messages.add("e.xx.fw.8001", productId);
            throw new BusinessException(messages);
        }
        return product;
    }

}
