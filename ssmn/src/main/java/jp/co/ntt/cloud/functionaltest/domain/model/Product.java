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
package jp.co.ntt.cloud.functionaltest.domain.model;

import java.io.Serializable;

/**
 * 商品
 *
 * @author NTT 電電太郎
 *
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 3167164729645544653L;

    /**
     * 商品ID
     */
    private String id;

    /**
     * 商品名
     */
    private String name;

    /**
     * 価格
     */
    private int price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Product)) {
            return false;
        }

        return id.equals(((Product) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
