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

import com.codeborne.selenide.Configuration;
import jp.co.ntt.cloud.functionaltest.selenide.page.CaapPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import jp.co.ntt.cloud.functionaltest.selenide.page.TopPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CaapTest {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * アプリケーションURL
     */
    @Value("${path.report}")
    private String reportPath;

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;

        // ログイン
        open(applicationContextUrl, TopPage.class)
                .login("0000000002", "aaaaa11111");
    }

    @After
    public void tearDown() {
        // ログアウト
        open(applicationContextUrl, TopPage.class).logout();
    }

    /*
     * AWS開発プロジェクトの起動条件として、AutoConfigureが無効化されていること。
     * すなわち、AmazonElastiCacheがクラスパス上に存在し、DIコンテナ上に存在していないこと。
     */
    @Test
    public void testInspect() {
        // テスト実行
        final CaapPage page = open(applicationContextUrl, CaapPage.class).inspect();

        // アサーション
        assertThat(page.isExistFQCNClasspath(), is(true));
        assertThat(page.isExistInApplicationContext(), is(false));

        // 証跡取得
        screenshot("testInspect");
    }
}
