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
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.*;

import org.junit.After;
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
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.HelloPage;
import junit.framework.TestCase;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CacheAbstractTest extends TestCase {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * テストデータ保存先
     */
    @Value("${path.report}")
    private String reportPath;

    /*
     * geckoドライバーバージョン
     */
    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    /*
     * テストメソッド取得用
     */
    @Rule
    public TestName testName = new TestName();

    @Override
    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            FirefoxDriverManager.getInstance().version(geckodriverVersion)
                    .setup();
        }

        // ブラウザの設定
        Configuration.browser = WebDriverRunner.MARIONETTE;

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    @Override
    @After
    public void tearDown() {
    }

    /**
     * ヒープでキャッシュできていることを確認する。
     */
    @Test
    public void h1HeapCachingTest() {

        open(applicationContextUrl + "heap");
        HelloPage helloPage = new HelloPage();

        // アサーション
        $("h1").shouldHave(text("Hello world!"));

        String firstRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-1");

        Selenide.refresh();

        String secondRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-2");

        assertEquals(firstRandomNo, secondRandomNo);
    }

    /**
     * ヒープのキャッシュが削除できていることを確認する。
     */
    @Test
    public void h2EvictHeapCacheTest() {

        open(applicationContextUrl + "heap");
        HelloPage helloPage = new HelloPage();

        // アサーション
        $("h1").shouldHave(text("Hello world!"));

        String firstRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-1");

        Selenide.refresh();

        String secondRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-2");
        assertEquals(firstRandomNo, secondRandomNo);

        open(applicationContextUrl + "heap/deleteCache?update");

        String thirdRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-3");
        assertNotEquals(firstRandomNo, thirdRandomNo);
    }

    /**
     * Redisでキャッシュできていることを確認する。
     */
    @Test
    public void h2RedisCachingTest() {

        open(applicationContextUrl + "redis");
        HelloPage helloPage = new HelloPage();

        // アサーション
        $("h1").shouldHave(text("Hello world!"));

        String firstRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot("cachingTest-access-1");

        Selenide.refresh();

        String secondRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot("cachingTest-access-2");

        assertEquals(firstRandomNo, secondRandomNo);
    }

    /**
     * Redisのキャッシュが削除できていることを確認する。
     */
    @Test
    public void h4RedisHeapCacheTest() {

        open(applicationContextUrl + "redis");
        HelloPage helloPage = new HelloPage();

        // アサーション
        $("h1").shouldHave(text("Hello world!"));

        String firstRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot("evictCacheTest-access-1");

        Selenide.refresh();

        String secondRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot("evictCacheTest-access-2");
        assertEquals(firstRandomNo, secondRandomNo);

        open(applicationContextUrl + "redis/deleteCache?update");

        String thirdRandomNo = helloPage.getMemberRandomNo().getText();
        screenshot("evictCacheTest-access-3");
        assertNotEquals(firstRandomNo, thirdRandomNo);
    }

}
