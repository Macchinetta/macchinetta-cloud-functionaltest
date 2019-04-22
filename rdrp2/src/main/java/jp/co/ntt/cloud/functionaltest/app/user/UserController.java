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
package jp.co.ntt.cloud.functionaltest.app.user;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.ntt.cloud.functionaltest.domain.model.User;
import jp.co.ntt.cloud.functionaltest.domain.model.UserInfo;
import jp.co.ntt.cloud.functionaltest.domain.service.userregister.UserRegisterService;
import jp.co.ntt.cloud.functionaltest.domain.service.usersearch.UserSearchService;

@Controller
public class UserController {

    @ModelAttribute
    public UserForm setUpForm() {
        UserForm form = new UserForm();
        return form;
    }

    @Inject
    UserSearchService userSearchService;

    @Inject
    UserRegisterService userRegiserService;

    @RequestMapping(value = "/")
    public String userlist(Model model) {

        List<User> userList = userSearchService.userSearchAll();

        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        for (User user : userList) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getUserId());
            userInfo.setFirstName(user.getFirstName());
            userInfo.setLastName(user.getLastName());
            userInfoList.add(userInfo);
        }
        model.addAttribute("userInfoList", userInfoList);

        return "user/userManagement";
    }

    @RequestMapping(value = "register")
    public String userRegister(UserForm form) {
        UserInfo userInfo = new UserInfo();
        userInfo.setLastName(form.getInputLastName());
        userInfo.setFirstName(form.getInputFirstName());

        userRegiserService.registerUser(userInfo);
        return "redirect:/";
    }

}
