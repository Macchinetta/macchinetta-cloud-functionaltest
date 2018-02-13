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

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.terasoluna.gfw.common.message.ResultMessages;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

import jp.co.ntt.cloud.functionaltest.domain.model.Cart;
import jp.co.ntt.cloud.functionaltest.domain.model.CartItem;
import jp.co.ntt.cloud.functionaltest.domain.model.Product;
import jp.co.ntt.cloud.functionaltest.domain.service.product.ProductService;

@Controller
@TransactionTokenCheck("order")
@RequestMapping("cart")
public class CartController {

    @Inject
    Cart cart;

    @Inject
    ProductService productService;

    @ModelAttribute
    private CartForm setUpForm() {
        return new CartForm();
    }

    /**
     * カートの内容を表示する。
     *
     * @param model
     * @return
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    private String viewCart(Model model, HttpSession session) {
        model.addAttribute("sessionId", session.getId());
        return "cart/viewCart";
    }

    /**
     * カートにアイテムを1個追加する。
     *
     * @param cartForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addItemToCart(@Validated CartForm cartForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            ResultMessages messages = ResultMessages.error().add("e.st.ca.5001");
            model.addAttribute(messages);
            return "redirect:/cart";
        }
        Product product = productService.findOne(cartForm.getId());
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        cart.add(cartItem);

        return "redirect:/order/form";
    }

    /**
     * カートに入っている製品の個数を変更する。
     *
     * @param cartForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "change", method = RequestMethod.POST)
    public String changeItemQuantityFromCart(@Validated CartForm cartForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            ResultMessages messages = ResultMessages.error().add("e.st.ca.5001");
            model.addAttribute(messages);
            return "redirect:/cart";
        }
        Product product = productService.findOne(cartForm.getId());
        CartItem cartItem = new CartItem();

        cartItem.setProduct(product);
        cartItem.setQuantity(cartForm.getQuantity());

        cart.setQuantity(cartItem);

        return "redirect:/cart";
    }

    /**
     * カートからアイテムを削除する。
     *
     * @param cartForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String deleteItemFormCart(@Validated CartForm cartForm, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            ResultMessages messages = ResultMessages.error().add("e.st.ca.5001");
            model.addAttribute(messages);
            return "redirect:/cart";
        }

        cart.remove(cartForm.getId());

        return "redirect:/cart";
    }
}