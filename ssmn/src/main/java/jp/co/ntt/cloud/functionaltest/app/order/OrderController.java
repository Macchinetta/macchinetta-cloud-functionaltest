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
package jp.co.ntt.cloud.functionaltest.app.order;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;

import jp.co.ntt.cloud.functionaltest.domain.model.Cart;
import jp.co.ntt.cloud.functionaltest.domain.service.product.ProductService;

/**
 * 商品注文用のコントローラ
 * @author NTT 電電太郎
 */
@Controller
@TransactionTokenCheck("order")
@RequestMapping("order")
public class OrderController {

    @Inject
    Cart cart;

    @Inject
    private ProductService prodcutService;

    /**
     * 注文ページ表示
     * @param model
     * @return
     */
    @RequestMapping(value = "form", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String form(Model model) {

        model.addAttribute("products", prodcutService.findAll());

        return "order/orderForm";
    }

    /**
     * 確認ページ表示
     * @param model
     * @return
     */
    @TransactionTokenCheck
    @RequestMapping(value = "confirm", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String confirm(Model model) {

        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalAmount", cart.getTotalAmount());

        return "order/orderConfirm";
    }

    /**
     * 注文完了
     * @param model
     * @param sessionStatus
     * @return
     */
    @RequestMapping(value = "finish", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String complete(Model model, SessionStatus sessionStatus) {
        cart.clear();
        return "order/orderFinish";
    }

}
