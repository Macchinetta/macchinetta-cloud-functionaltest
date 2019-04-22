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
package jp.co.ntt.cloud.functionaltest.selenide.testcase;

import com.codeborne.selenide.Configuration;
import jp.co.ntt.cloud.functionaltest.selenide.page.AtscPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/selenideContext.xml" })
public class AtscTest {

    /*
     * アプリケーションURL
     */
    @Value("${target.applicationContextUrl}")
    private String applicationContextUrl;

    /*
     * レポートパス
     */
    @Value("${path.report}")
    private String reportPath;

    @Before
    public void setUp() {
        // テスト結果の出力先の設定
        Configuration.reportsFolder = reportPath;
    }

    /*
     * しきい値超えアラームの発生を待ち、通知内容を画面に表示する。
     */
    @Test
    public void testStartListen() {

        // テスト実行
        AtscPage atscPage = open(applicationContextUrl, AtscPage.class)
                .submit();

        // アサーション
        assertThat(atscPage.getNewState(), is("ALARM"));
        assertThat(atscPage.getNewStateReason(), containsString(
                "Threshold Crossed"));
        assertThat(atscPage.getNamespace(), either(is("local")).or(is("ci")));
        assertThat(atscPage.getMetricName(), is("HeapMemory.Max"));
        assertThat(atscPage.getDimensions(), containsString(
                "AutoScalingGroupName:atscGroup"));

        // 証跡取得
        screenshot("testStartListen");
    }
}
