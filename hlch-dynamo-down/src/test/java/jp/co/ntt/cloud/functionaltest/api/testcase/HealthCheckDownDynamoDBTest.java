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
 *
 */
package jp.co.ntt.cloud.functionaltest.api.testcase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.restassured.RestAssured;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class HealthCheckDownDynamoDBTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${path.testdata}")
    private String testDataPath;

    @Before
    public void setUp() {

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // RestAssuredのベースURI設定
        RestAssured.baseURI = this.applicationContextUrl;
    }

    /**
     * HLCH0201 001 DynamoDBでエラーが発生したときにヘルスチェックのdynamoDBのstatusと全体のstatus「DOWN」になること
     */
    @Test
    public void downDynamoDBHealthCheckTest() {

        // アサート:dynamoDBのstatusが「DOWN」になっていること、全体のヘルスチェック結果のstatusが「DOWN」になっていること
        given().get("/management/health").then().body("status", equalTo("DOWN"))
                .body("details.dynamodb.status", equalTo("DOWN")).body(
                        "details.dynamodb.details", hasKey("error")).body(
                                "details.diskSpace.status", equalTo("UP")).body(
                                        "details.db.status", equalTo("UP"))
                .body("details.refreshScope.status", equalTo("UP"));

    }
}
