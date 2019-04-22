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
package jp.co.ntt.cloud.functionaltest.infra.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;

/**
 * Joda-Time用のTypeHandlerの実装
 * @author NTT 電電太郎
 */
public class DateTimeTypeHandler extends BaseTypeHandler<DateTime> {

    /**
     * DateTimeをTimestampに変換し、PreparedStatementに設定する処理
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
            DateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
    }

    /**
     * ResultSet又はCallableStatementから取得したTimestampをDateTimeに変換し、返り値として返却する。
     */
    @Override
    public DateTime getNullableResult(ResultSet rs,
            String columnName) throws SQLException {
        return toDateTime(rs.getTimestamp(columnName));
    }

    /**
     * ResultSet又はCallableStatementから取得したTimestampをDateTimeに変換し、返り値として返却する。
     */
    @Override
    public DateTime getNullableResult(ResultSet rs,
            int columnIndex) throws SQLException {
        return toDateTime(rs.getTimestamp(columnIndex));
    }

    /**
     * ResultSet又はCallableStatementから取得したTimestampをDateTimeに変換し、返り値として返却する。
     */
    @Override
    public DateTime getNullableResult(CallableStatement cs,
            int columnIndex) throws SQLException {
        return toDateTime(cs.getTimestamp(columnIndex));
    }

    /**
     * nullを許可するカラムの場合、Timestampがnullになる可能性があるため、 nullチェックを行ってからDateTimeに変換するメソッド。
     * @param timestamp
     * @return
     */
    private DateTime toDateTime(Timestamp timestamp) {
        if (Objects.isNull(timestamp)) {
            return null;
        } else {
            return new DateTime(timestamp.getTime());
        }
    }

}
