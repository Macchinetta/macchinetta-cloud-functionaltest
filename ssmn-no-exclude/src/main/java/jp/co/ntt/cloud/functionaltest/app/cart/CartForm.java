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
package jp.co.ntt.cloud.functionaltest.app.cart;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class CartForm {

    public static interface ChangeQuantity {
    };

    /**
     * 製品ID
     */
    @NotEmpty
    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    private String id;

    /**
     * 数量
     */
    @NotNull
    @Max(value = 10)
    @Min(value = 0)
    private int quantity;

    /**
     * 製品IDを取得する。
     *
     * @return 製品ID
     */
    public String getId() {
        return id;
    }

    /**
     * 製品IDを設定する。
     *
     * @param productId
     *            製品ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 数量を取得する。
     *
     * @return 数量
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 数量を設定する。
     *
     * @param quantity
     *            数量
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}