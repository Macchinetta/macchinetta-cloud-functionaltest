/*
 * Copyright 2014-2020 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.app.order;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.model.Cart;
import jp.co.ntt.cloud.functionaltest.domain.service.product.ProductService;

/**
 * 商品注文用のコントローラ
 * @author NTT 電電太郎
 */
@Controller
@TransactionTokenCheck("order")
public class OrderController {

    @Inject
    Cart cart;

    @Inject
    private ProductService prodcutService;

    /**
     * 注文ページ表示
     * @param model
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.ORDER_FORM)
    public String form(Model model) {

        model.addAttribute("products", prodcutService.findAll());

        return WebPagePathConstants.ORDER_ORDERFORM;
    }

    /**
     * 確認ページ表示
     * @param model
     * @return View論理名
     */
    @PostMapping(value = WebPagePathConstants.ORDER_CONFIRM)
    public String confirm(Model model) {

        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalAmount", cart.getTotalAmount());

        return WebPagePathConstants.ORDER_ORDERCONFIRM;
    }

    /**
     * 注文完了
     * @param model
     * @param sessionStatus
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.ORDER_FINISH)
    public String complete(Model model, SessionStatus sessionStatus) {
        cart.clear();
        return WebPagePathConstants.ORDER_ORDERFINISH;
    }

}
