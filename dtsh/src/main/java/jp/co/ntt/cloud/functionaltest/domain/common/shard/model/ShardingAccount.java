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
/**
 *
 */
package jp.co.ntt.cloud.functionaltest.domain.common.shard.model;

import java.io.Serializable;

/**
 * DynamoDBのシャーディングアカウントモデル。
 * @author NTT 電電太郎
 */
public class ShardingAccount implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ユーザID */
    private String id;

    /** データソースのキー */
    private String dataSourceKey;

    /**
     * ユーザIDを取得する。
     * @return userId
     */

    public String getId() {
        return id;
    }

    /**
     * ユーザIDを設定する。
     * @param userId セットする userId
     */
    public void setId(String userId) {
        this.id = userId;
    }

    /**
     * データソースのキーを取得する。
     * @return dataSourceKey
     */
    public String getDataSourceKey() {
        return dataSourceKey;
    }

    /**
     * データソースのキーを設定する。
     * @param dataSourceKey セットする dataSourceKey
     */
    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

    /*
     * (非 Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (dataSourceKey != null ? dataSourceKey.hashCode()
                : 0);
        return result;
    }

    /*
     * (非 Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ShardingAccount)) {
            return false;
        }

        ShardingAccount sa = (ShardingAccount) obj;
        // ユーザIDがnullでない、かつユーザIDがシャーディングアカウントが保有するユーザIDと一致しないまたはシャーディングアカウントが保有するユーザIDがnull出ない場合はfalseを返却する
        if ((id != null && !id.equals(sa.id)) || (id != null
                && sa.id != null)) {
            return false;
        } else {
            if (dataSourceKey == null) {
                return sa.dataSourceKey == null;
            } else {
                return dataSourceKey.equals(sa.dataSourceKey);
            }
        }

    }

}
