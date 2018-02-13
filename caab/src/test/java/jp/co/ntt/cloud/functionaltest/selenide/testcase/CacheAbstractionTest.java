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
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.ntt.cloud.functionaltest.domain.model.Member;
import jp.co.ntt.cloud.functionaltest.rest.api.member.MemberResource;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CacheAbstractionTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Inject
    private RedisTemplate<Object, Object> redisTemplate;

    @Inject
    private ObjectMapper objectMapper;

    /**
     * setUp
     * <ul>
     * <li>Redisデータの削除</li>
     * </ul>
     */
    @Before
    public void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @After
    public void tearDown() {

    }

    /**
     * testCAAB0101001
     * <ul>
     * <li>Redis接続の確認</li>
     * <li>{@code @Cacheable}によるキャッシュおよび{@code @CacheEvict}によるキャッシュ削除の確認</li>
     * </ul>
     */
    @Test
    public void testCAAB0101001() throws JsonProcessingException {

        assertNull(redisTemplate.opsForValue().get("member/0000000001"));

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

        Member member = (Member) redisTemplate.opsForValue().get(
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

        assertNull(redisTemplate.opsForValue().get("member/0000000001"));

    }

}
