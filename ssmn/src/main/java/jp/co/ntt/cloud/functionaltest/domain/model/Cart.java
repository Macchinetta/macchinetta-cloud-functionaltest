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
package jp.co.ntt.cloud.functionaltest.domain.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

/**
 * カート
 * @author NTT 電電太郎
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Cart implements Serializable {

    private static final long serialVersionUID = 4506028948649707674L;

    /**
     * カートに入っている商品
     */
    private final Map<String, CartItem> cartItems = Collections.synchronizedMap(
            new LinkedHashMap<>());

    /**
     * カートに入っているアイテムを取得する。
     * @return カートに入っているアイテム
     */
    public Collection<CartItem> getCartItems() {
        return cartItems.values();
    }

    /**
     * カートにアイテムを追加する。
     * @param cartItem カートアイテム
     * @return カートクラス
     */
    public Cart add(CartItem cartItem) {

        String productId = cartItem.getProduct().getId();

        // すでに対象の商品がカートにある場合、数量を取得する
        int nowQuantity = 0;
        CartItem cartItemInCart = cartItems.get(productId);
        if (Objects.nonNull(cartItemInCart)) {
            nowQuantity = cartItemInCart.getQuantity();
        }

        // すでに対象の商品がカートにある場合、その数量を加算して再登録する
        int totalQuantity = cartItem.getQuantity() + nowQuantity;
        cartItem.setQuantity(totalQuantity);
        cartItems.put(productId, cartItem);

        return this;
    }

    /**
     * カートに入っているアイテムをクリアする。
     * @return カートクラス
     */
    public Cart clear() {
        cartItems.clear();
        return this;
    }

    /**
     * カートに入っているアイテムを複数削除する。
     * @param removedItemsIds アイテムID
     * @return カートクラス
     */
    public Cart remove(Set<String> removedItemsIds) {
        for (String key : removedItemsIds) {
            cartItems.remove(key);
        }
        return this;
    }

    /**
     * カートに入っているアイテムを一つ削除する。
     * @param removedItemsId アイテムID
     * @return カートクラス
     */
    public Cart remove(String removedItemsId) {
        cartItems.remove(removedItemsId);
        return this;
    }

    /**
     * カートに入っているアイテムの数量を設定する。
     * @param cartItem カートアイテム
     * @return カートクラス
     */
    public Cart setQuantity(CartItem cartItem) {
        String productId = cartItem.getProduct().getId();
        CartItem dist = cartItems.get(productId);
        dist.setQuantity(cartItem.getQuantity());
        cartItems.put(productId, dist);

        return this;
    }

    /**
     * カートが空かどうかを返す。空なら{@link true}、それ以外は{@link false}。
     * @return {@link boolean}
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    /**
     * カートに入っているアイテムの合計金額を返す。
     * @return 合計金額
     */
    public int getTotalAmount() {
        int amount = 0;
        for (CartItem cartItem : cartItems.values()) {
            amount += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        return amount;
    }

    /**
     * カートの状態を表すハッシュ値を作成する
     * @param cart
     * @return
     */
    public String calcSignature() {
        byte[] serialized = SerializationUtils.serialize(this);
        byte[] signature = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            signature = messageDigest.digest(serialized);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return new String(Base64.encode(signature));
    }
}
