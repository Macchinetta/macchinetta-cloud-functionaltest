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
package jp.co.ntt.cloud.functionaltest.testcase;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.Configuration;

import io.restassured.RestAssured;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/selenideContext.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class S03SessionEnforcerNoExcludePathTest extends TestCase {

    @Rule
    public TestName testName = new TestName();

    /**
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /**
     * テスト結果出力先
     */
    @Value("${path.report}")
    private String reportPath;

    /**
     * セッションタイムアウトするまでの時間(秒単位)
     */
    @Value("${until.session.timeout.sec}")
    private Integer untilSessionTimeout;

    @Override
    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
        // RestAssured のベースURI設定
        RestAssured.baseURI = this.applicationContextUrl;
    }

    /**
     * SSMN0205002<br>
     * {@link jp.co.ntt.cloud.functionaltest.app.common.session.SessionEnforcerFilter}を適用するパスで、セッションがない状態でGETするとリダイレクトすること。
     *
     * @throws ClientProtocolException
     *
     * @throws IOException
     */
    @Test
    public void session01HealthCheck() throws ClientProtocolException, IOException {

        CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();

        // HealthCheckにアクセス
        try (CloseableHttpResponse response = client
                .execute(new HttpGet(this.applicationContextUrl + "management/health"))) {
            assertEquals(302, response.getStatusLine().getStatusCode());
        }
    }

}
