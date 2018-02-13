/*
 * Copyright(c) 2017 NTT Corporation.
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
