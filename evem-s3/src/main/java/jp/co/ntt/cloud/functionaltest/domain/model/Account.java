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

/**
 * ユーザアカウント。
 * @author NTT 電電太郎
 */
public class Account implements Serializable {

    /**
     * シリアルバージョンUID。
     */
    private static final long serialVersionUID = 1L;

    /**
     * ユーザID。
     */
    private String userId;

    /**
     * パスワード。
     */
    private String password;

    /**
     * 英字名。
     */
    private String firstName;

    /**
     * 英字姓。
     */
    private String lastName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Account [userId=" + userId + ", password=" + password
                + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}
