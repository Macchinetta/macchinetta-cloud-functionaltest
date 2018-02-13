/*
 * Copyright 2014-2018 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.rest.api.member.MemberResource;
import junit.framework.TestCase;

/**
 * キャッシュとセッションの格納先Redisを別に設定する場合のテスト。
 * 本テストはdefaultプロファイルでは実行されない。
 * 本テストをローカルPCで実行する場合はスタンドアローンのRedisをキャッシュ用とセッション用の2つ起動させ、maven profileとspring profileにmultiredisを指定する。
 * <li>mvn integration-test -P multiredis -Dspring.profiles.active=multiredis</li>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
@IfProfileValue(name="spring.profiles.active", values={"multiredis","ci"})
public class CacheAbstractionTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Inject
    @Named("redisTemplateForCache")
    private RedisTemplate<Object, Object> redisTemplateForCache;

    @Inject
    @Named("jedisConnectionFactoryForSession")
    private JedisConnectionFactory jedisConnectionFactoryForSession;

    @Inject
    private ObjectMapper objectMapper;

    /**
     * setUpMultiRedis
     * <ul>
     * <li>Redisデータの削除</li>
     * </ul>
     */
    @Before
    public void setUp() {
        redisTemplateForCache.getConnectionFactory().getConnection().flushAll();
        jedisConnectionFactoryForSession.getConnection().flushAll();
    }
    
    @After
    public void tearDown() {

    }

    /**
     * testCAAB0401001
     * <ul>
     * <li>Redis接続（独自プロパティキー）の確認</li>
     * <li>{@code @Cacheable}によるキャッシュおよび{@code @CacheEvict}によるキャッシュ削除の確認</li>
     * </ul>
     */
    @Test
    public void testCAAB0401001() throws JsonProcessingException {

        assertNull(redisTemplateForCache.opsForValue().get("member/0000000001"));
        assertThat(jedisConnectionFactoryForSession.getConnection().keys("spring:session:*".getBytes()).size(),is(0));

        // @formatter:off
        given()
            .contentType("application/json; charset=UTF-8")
        .when()
            .get(applicationContextUrl + "api/v1/Member/update/0000000001")
        .then()
            .statusCode(200)
            .body("kanjiFamilyName", equalTo("電電"))
            .body("kanjiGivenName", equalTo("花子"));
        // @formatter:on

        Member member = (Member) redisTemplateForCache.opsForValue().get(
                "member/0000000001");
        
        assertThat(member.getKanjiFamilyName(), equalTo("電電"));
        assertThat(member.getKanjiGivenName(), equalTo("花子"));

        MemberResource requestMember = new MemberResource();
        requestMember.setKanjiFamilyName("日電");
        requestMember.setKanjiGivenName("花子");
        requestMember.setKanaFamilyName("ニチデン");
        requestMember.setKanaGivenName("ハナコ");
        requestMember.setYearOfBirth(1979);
        requestMember.setMonthOfBirth(01);
        requestMember.setDayOfBirth(25);
        requestMember.setTel1("111");
        requestMember.setTel2("1111");
        requestMember.setTel3("1111");
        requestMember.setZipCode1("111");
        requestMember.setZipCode2("111");
        requestMember.setAddress("東京都港区港南Ｘ－Ｘ－Ｘ");

        assertThat(jedisConnectionFactoryForSession.getConnection().keys("spring:session:*".getBytes()).size(),is(3));

        // @formatter:off
        given()
            .contentType("application/json; charset=UTF-8")
            .body(objectMapper.writeValueAsString(requestMember))
        .when()
            .put(applicationContextUrl + "api/v1/Member/update/0000000001")
        .then()
            .statusCode(200)
            .body("kanjiFamilyName", equalTo("日電"))
            .body("kanjiGivenName", equalTo("花子"));
        // @formatter:on

        assertNull(redisTemplateForCache.opsForValue().get("member/0000000001"));

    }

}
