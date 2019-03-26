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

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;

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

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.ntt.cloud.functionaltest.selenide.page.HomePage;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class CacheAbstractTest extends TestCase {

    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    @Value("${path.report}")
    private String reportPath;

    @Value("${selenide.geckodriverVersion}")
    private String geckodriverVersion;

    @Rule
    public TestName testName = new TestName();

    @Override
    @Before
    public void setUp() {

        // geckoドライバーの設定
        if (System.getProperty("webdriver.gecko.driver") == null) {
            WebDriverManager.firefoxdriver().version(geckodriverVersion)
                    .setup();
        }

        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /**
     * CCAB0101 001 1回目のメソッド呼び出し時に戻り値をヒープにキャッシュし、2回目以降のメソッドの呼び出しに対しては、戻り値をキャッシュから取得していること
     */
    @Test
    public void h1HeapCachingTest() {

        HomePage homePage = open(applicationContextUrl + "heap",
                HomePage.class);

        String firstRandomNo = homePage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-1");

        Selenide.refresh();

        // アサート:1回目と2回目のメソッド呼び出し時の戻り値が同じになること。
        homePage.getMemberRandomNo().shouldHave(exactText(firstRandomNo));
        screenshot(testName.getMethodName() + "-access-2");
    }

    /**
     * CCAB0102 001 メソッドの戻り値のキャッシュが削除されて、更新後メソッドの戻り値が取得できること
     */
    @Test
    public void h2EvictHeapCacheTest() {

        HomePage homePage = open(applicationContextUrl + "heap",
                HomePage.class);

        String firstRandomNo = homePage.getMemberRandomNo().getText();
        screenshot(testName.getMethodName() + "-access-1");

        Selenide.refresh();

        // アサート:1回目と2回目のメソッド呼び出し時の戻り値が同じになること。
        homePage.getMemberRandomNo().shouldHave(exactText(firstRandomNo));
        screenshot(testName.getMethodName() + "-access-2");

        open(applicationContextUrl + "heap/deleteCache?update");

        // アサート:3回目のメソッド呼び出し時の戻り値と、1回めのメソッド呼び出し時の戻り値が異なること。
        homePage.getMemberRandomNo().shouldNotHave(exactText(firstRandomNo));
        screenshot(testName.getMethodName() + "-access-3");
    }

    /**
     * CCAB0101 002 1回目のメソッド呼び出し時に戻り値をRedisにキャッシュし、2回目以降のメソッドの呼び出しに対しては、戻り値をキャッシュから取得していること
     */
    @Test
    public void h2RedisCachingTest() {

        HomePage homePage = open(applicationContextUrl + "redis",
                HomePage.class);

        String firstRandomNo = homePage.getMemberRandomNo().getText();
        screenshot("cachingTest-access-1");

        Selenide.refresh();

        // アサート:1回目と2回目のメソッド呼び出し時の戻り値が同じになること。
        homePage.getMemberRandomNo().shouldHave(exactText(firstRandomNo));
        screenshot("cachingTest-access-2");
    }

    /**
     * CCAB0102 002 メソッドの戻り値のキャッシュが削除されて、更新後メソッドの戻り値が取得できること
     */
    @Test
    public void h4RedisHeapCacheTest() {

        HomePage homePage = open(applicationContextUrl + "redis",
                HomePage.class);

        String firstRandomNo = homePage.getMemberRandomNo().getText();
        screenshot("evictCacheTest-access-1");

        Selenide.refresh();

        // アサート:1回目と2回目のメソッド呼び出し時の戻り値が同じになること。
        homePage.getMemberRandomNo().shouldHave(exactText(firstRandomNo));
        screenshot("evictCacheTest-access-2");

        open(applicationContextUrl + "redis/deleteCache?update");

        // アサート:3回目のメソッド呼び出し時の戻り値と、1回めのメソッド呼び出し時の戻り値が異なること。
        homePage.getMemberRandomNo().shouldNotHave(exactText(firstRandomNo));
        screenshot("evictCacheTest-access-3");
    }

}
