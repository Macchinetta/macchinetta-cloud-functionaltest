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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import jp.co.ntt.cloud.functionaltest.selenium.FunctionTestSupport;
import jp.co.ntt.cloud.functionaltest.selenium.ScreenCaptureWebDriverEventListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/seleniumContext.xml" })
public class S02TransactionTokenTest extends FunctionTestSupport {

    /**
     * セッションタイムアウトするまでの時間(秒単位)
     */
    @Value("${until.session.timeout.sec}")
    private Integer untilSessionTimeout;

    private static final Set<String> testCasesOfRebootTarget = new HashSet<String>(Arrays
            .asList("test03_01_defaultTokenStoreSizeOver",
                    "test03_02_customTokenStoreSizeOverClassMethodNamespace",
                    "test03_03_customTokenStoreSizeOverMethodOnlyNamespace",
                    "test03_04_customTokenStoreSizeOverGlobalNamespace"));

    protected EventFiringWebDriver driver;

    public S02TransactionTokenTest() {
        disableSetupDefaultWebDriver();
    }

    @Before
    public void setUp() {
        if (testCasesOfRebootTarget.contains(testName.getMethodName())) {
            quitDefaultWebDriver();
        }
        bootDefaultWebDriver();
        driver = new EventFiringWebDriver(getDefaultWebDriver());
        driver.register(new ScreenCaptureWebDriverEventListener(screenCapture));
    }

    @After
    public void tearDown() {
        if (testCasesOfRebootTarget.contains(testName.getMethodName())) {
            quitDefaultWebDriver();
        }
    }

    /**
     * testSSMN0101001
     * <ul>
     * <li>クラスに対して、指定したvalueの値で、Namespaceが有効になっていること</li>
     * </ul>
     */
    @Test
    public void testSSMN0101001() {
        driver.findElement(By.id("link1")).click();
        driver.findElement(By.id("btn1")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}"));
    }

    /**
     * testSSMN0101002
     * <ul>
     * <li>クラスとメソッドに対して、指定したvalueの値で、Namespaceが有効になっていること</li>
     * </ul>
     */
    @Test
    public void testSSMN0101002() {
        driver.findElement(By.id("link1")).click();
        driver.findElement(By.id("btn2")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "transactiontoken/create~[0-9a-z]{32}~[0-9a-z]{32}"));
    }

    /**
     * testSSMN0101003
     * <ul>
     * <li>メソッドに対して、指定したvalueの値で、Namespaceが有効になっていること</li>
     * </ul>
     */
    @Test
    public void testSSMN0101003() {
        driver.findElement(By.id("link1")).click();
        driver.findElement(By.id("btn3")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "create~[0-9a-z]{32}~[0-9a-z]{32}"));
    }

    /**
     * testSSMN0101004
     * <ul>
     * <li>クラスとメソッド両方に対して、valueの値は指定していないので、globalTokenでNamespaceが有効になっていること</li>
     * </ul>
     */
    @Test
    public void testSSMN0101004() {
        driver.findElement(By.id("link1")).click();
        driver.findElement(By.id("btn4")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "globalToken~[0-9a-z]{32}~[0-9a-z]{32}"));
    }

    /**
     * testSSMN0102001
     * <ul>
     * <li>BEGIN-IN-CHECK-END (Namespace指定している - value属性)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102001() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertUpdateTokenValue(newToken, currentToken);

        // check
        currentToken = newToken;
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        driver.findElement(By.id("btn-check")).click();
        newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertThat(newToken, is(currentToken));

        driver.findElement(By.id("btn-end")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));

    }

    /**
     * testSSMN0102002
     * <ul>
     * <li>BEGIN-IN-CHECK-END (Namespace指定している - namespace属性)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102002() {
        driver.findElement(By.id("link5")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertUpdateTokenValue(newToken, currentToken);

        // check
        currentToken = newToken;
        driver.findElement(By.id("btn-check")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertThat(newToken, is(currentToken));

        driver.findElement(By.id("btn-end")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));

    }

    /**
     * testSSMN0102003
     * <ul>
     * <li>BEGIN-IN-CHECK-END (Namespace指定していない - globalToken)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102003() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow2")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertThat(currentToken.split("~")[0], is("globalToken"));
        assertUpdateTokenValue(newToken, currentToken);

        // check
        currentToken = newToken;
        driver.findElement(By.id("btn-check")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertThat(newToken, is(currentToken));

        driver.findElement(By.id("btn-end")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));
    }

    /**
     * testSSMN0102004
     * <ul>
     * <li>BEGIN-END</li>
     * </ul>
     */
    @Test
    public void testSSMN0102004() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        driver.findElement(By.id("btn-end")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));

    }

    /**
     * testSSMN0102005
     * <ul>
     * <li>BEGIN-IN-TransactionTokenContext経由破棄</li>
     * </ul>
     */
    @Test
    public void testSSMN0102005() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        driver.findElement(By.id("btn-in-finish")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));

    }

    /**
     * testSSMN0102006
     * <ul>
     * <li>BEGIN-IN-IN(Redo)-IN-END</li>
     * </ul>
     */
    @Test
    public void testSSMN0102006() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        String currentTokenName = currentToken.split("~")[0];
        String newTokenName = newToken.split("~")[0];
        String currentTokenKey = currentToken.split("~")[1];
        String newTokenKey = newToken.split("~")[1];
        String currentTokenValue = currentToken.split("~")[2];
        String newTokenValue = newToken.split("~")[2];
        assertThat(newTokenName, is(currentTokenName));
        assertThat(newTokenKey, is(currentTokenKey));
        assertThat(newTokenValue, is(not(currentTokenValue)));

        driver.findElement(By.id("btn-back")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        currentToken = newToken;
        newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        currentTokenName = currentToken.split("~")[0];
        newTokenName = newToken.split("~")[0];
        currentTokenKey = currentToken.split("~")[1];
        newTokenKey = newToken.split("~")[1];
        currentTokenValue = currentToken.split("~")[2];
        newTokenValue = newToken.split("~")[2];
        assertThat(newTokenName, is(currentTokenName));
        assertThat(newTokenKey, is(currentTokenKey));
        assertThat(newTokenValue, is(not(currentTokenValue)));

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        currentToken = newToken;
        newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        currentTokenName = currentToken.split("~")[0];
        newTokenName = newToken.split("~")[0];
        currentTokenKey = currentToken.split("~")[1];
        newTokenKey = newToken.split("~")[1];
        currentTokenValue = currentToken.split("~")[2];
        newTokenValue = newToken.split("~")[2];
        assertThat(newTokenName, is(currentTokenName));
        assertThat(newTokenKey, is(currentTokenKey));
        assertThat(newTokenValue, is(not(currentTokenValue)));

        driver.findElement(By.id("btn-end")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));
    }

    /**
     * testSSMN0102007
     * <ul>
     * <li>BEGIN(Input Error)-BEGIN-IN</li>
     * </ul>
     */
    @Test
    public void testSSMN0102007() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow5_1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-flow5_2")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")), notNullValue());
        String inToken = driver.findElement(By.name("_TRANSACTION_TOKEN")).getAttribute("value");

        String currentTokenName = currentToken.split("~")[0];
        String newTokenName = newToken.split("~")[0];
        String inTokenName = inToken.split("~")[0];

        String currentTokenKey = currentToken.split("~")[1];
        String newTokenKey = newToken.split("~")[1];
        String inTokenKey = inToken.split("~")[1];

        String currentTokenValue = currentToken.split("~")[2];
        String newTokenValue = newToken.split("~")[2];
        String inTokenValue = inToken.split("~")[2];

        assertThat(currentTokenName, is(newTokenName));
        assertThat(currentTokenKey, is(not(newTokenKey)));
        assertThat(currentTokenValue, is(not(newTokenValue)));

        assertThat(newTokenName, is(inTokenName));
        assertThat(newTokenKey, is(inTokenKey));
        assertThat(newTokenValue, is(not(inTokenValue)));
    }

    /**
     * testSSMN0102008
     * <ul>
     * <li>BEGIN-END (Business Error)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102008() {
        driver.findElement(By.id("link2")).click();

        // token generation
        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        // error occurs in end and returns back to step-1 screen with transaction token destroyed
        driver.findElement(By.id("btn-end-error")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));
    }

    /**
     * testSSMN0102009
     * <ul>
     * <li>BEGIN-IN-TransactionTokenContext経由破棄(Business Error)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102009() {
        driver.findElement(By.id("link2")).click();

        // token generation
        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        // error occurs in IN and returns back to step-2 screen but transaction token not destroyed
        driver.findElement(By.id("btn-in-finish-error")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        // Check whether transaction token is updated
        // key will remain same, only value will be updated
        String currentTokenName = currentToken.split("~")[0];
        String newTokenName = newToken.split("~")[0];
        String currentTokenKey = currentToken.split("~")[1];
        String newTokenKey = newToken.split("~")[1];
        String currentTokenValue = currentToken.split("~")[2];
        String newTokenValue = newToken.split("~")[2];
        assertThat(newTokenName, is(currentTokenName));
        assertThat(newTokenKey, is(currentTokenKey));
        assertThat(newTokenValue, is(not(currentTokenValue)));

        // this time no error and token is destroyed
        driver.findElement(By.id("btn-in-finish")).click();
        assertFalse(webDriverOperations.exists(By.name("_TRANSACTION_TOKEN")));
    }

    /**
     * testSSMN0102010
     * <ul>
     * <li>IN called without BEGIN (Token error since token not present)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102010() {
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow3")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0102011
     * <ul>
     * <li>BEGIN-IN-(Browser Back)-IN (Token error due to Token mismatch)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102011() {

        driver.findElement(By.id("link2")).click();

        // token generation
        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        // Browser Back Simulation
        // consider that back button is clicked and old token is sent again for IN/END request
        // As Browser back in not working, request is sent to END method instead of IN again
        // Expected Flow : BEGIN -> IN -> Browser Back -> IN (token error)
        // Actual implemented flow : BEGIN -> IN -> END (Token error since token generated in BEGIN is passed instead of that
        // updated after IN)

        JavascriptExecutor jse = driver;
        jse.executeScript(
                "document.getElementsByName('_TRANSACTION_TOKEN')[0].setAttribute('type', 'text');");
        jse.executeScript(
                "document.getElementsByName('_TRANSACTION_TOKEN')[0].value = '"
                        + currentToken + "';");

        driver.findElement(By.id("btn-end")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0102012
     * <ul>
     * <li>BEGIN-IN (Token error due to Token mismatch)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102012() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow7")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));

    }

    /**
     * testSSMN0102013
     * <ul>
     * <li>END called without BEGIN (Token error since token not present)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102013() {
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow4")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0102014
     * <ul>
     * <li>BEGIN-END (Token error due to Token mismatch)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102014() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow7")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        driver.findElement(By.id("btn-end")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0102015
     * <ul>
     * <li>BEGIN-IN</li>
     * </ul>
     */
    @Test
    public void testSSMN0102015() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        String currentTokenName = currentToken.split("~")[0];
        String newTokenName = newToken.split("~")[0];
        String currentTokenKey = currentToken.split("~")[1];
        String newTokenKey = newToken.split("~")[1];
        String currentTokenValue = currentToken.split("~")[2];
        String newTokenValue = newToken.split("~")[2];
        assertThat(newTokenName, is(currentTokenName));
        assertThat(newTokenKey, is(currentTokenKey));
        assertThat(newTokenValue, is(not(currentTokenValue)));
    }

    /**
     * testSSMN0102016
     * <ul>
     * <li>BEGIN-CHECK(File Download)-IN</li>
     * </ul>
     */
    @Test
    public void testSSMN0102016() {
        driver.findElement(By.id("link2")).click();

        // begin
        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        // filedownload (check)
        driver.findElement(By.id("btn-download01")).click();

        // in
        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertUpdateTokenValue(newToken, currentToken);

    }

    /**
     * testSSMN0102017
     * <ul>
     * <li>BEGIN-IN-(Browser Back)-CHECK</li>
     * </ul>
     */
    @Test
    public void testSSMN0102017() {
        driver.findElement(By.id("link2")).click();

        // begin
        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        String currentToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");

        // in
        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());
        String newToken = driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value");
        assertUpdateTokenValue(newToken, currentToken);

        // Browser Back Simulation
        // consider that back button is clicked and old token is sent again for UPDATE request
        // As Browser back in not working, request is sent to UPDATE method with oldtoken
        // Expected Flow : BEGIN -> IN -> Browser Back -> UPDATE
        // Actual implemented flow : BEGIN -> IN -> UPDATE(old token)

        JavascriptExecutor jse = driver;
        jse.executeScript(
                "document.getElementsByName('_TRANSACTION_TOKEN')[0].setAttribute('type', 'text');");
        jse.executeScript(
                "document.getElementsByName('_TRANSACTION_TOKEN')[0].value = '"
                        + currentToken + "';");

        // check
        driver.findElement(By.id("btn-check")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));

    }

    /**
     * testSSMN0102018
     * <ul>
     * <li>CHECK called without BEGIN (Token error since token not present)</li>
     * </ul>
     */
    @Test
    public void testSSMN0102018() {
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow8")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0103001
     * <ul>
     * <li>同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される (windowをMax+1起動)</li>
     * </ul>
     */
    @Test
    public void testSSMN0103001() {

        if (driver.getWrappedDriver() instanceof InternetExplorerDriver) {
            logger.warn(testName.getMethodName()
                    + " is not support Internet Explorer.");
            return;
        }

        // Main window
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow1")).click();
        String mainWindow = driver.getWindowHandle();

        for (int i = 0; i < 10; i++) {
            driver.findElement(By.id("open-new-window")).click();
            driver.switchTo().window(new LinkedList<String>(driver
                    .getWindowHandles()).getLast());
            driver.findElement(By.id("link2")).click();
            driver.findElement(By.id("btn-flow1")).click();
        }

        // Click for in
        driver.switchTo().window(mainWindow);
        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0103002
     * <ul>
     * <li>同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される (カスタム設定 = 2)</li>
     * </ul>
     */
    @Test
    public void testSSMN0103002() {

        if (driver.getWrappedDriver() instanceof InternetExplorerDriver) {
            logger.warn(testName.getMethodName()
                    + " is not support Internet Explorer.");
            return;
        }

        // TODO this test fails if the other namespace starts with the main namespace

        // Main window
        // start some non conflicting operation
        driver.findElement(By.id("link3")).click();
        driver.findElement(By.id("btn-begin1-other")).click();
        String mainWindow = driver.getWindowHandle();

        // Conflict window
        // Start conflicting operation
        driver.findElement(By.id("open-new-window")).click();
        driver.switchTo().window(new LinkedList<String>(driver
                .getWindowHandles()).getLast());
        driver.findElement(By.id("link3")).click();
        driver.findElement(By.id("btn-begin1")).click();
        String conflictWindow = driver.getWindowHandle();

        for (int i = 0; i < 2; i++) {
            // Other window
            driver.findElement(By.id("open-new-window")).click();
            driver.switchTo().window(new LinkedList<String>(driver
                    .getWindowHandles()).getLast());
            driver.findElement(By.id("link3")).click();
            driver.findElement(By.id("btn-begin1")).click();
        }

        // Complete non conflicting operation main window
        // Click for in
        driver.switchTo().window(mainWindow);
        driver.findElement(By.id("btn-in1-other")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(not(
                "Transaction Token Error")));

        // Check transaction token error for the operation of which transaction token has expired
        // Click for in
        driver.switchTo().window(conflictWindow);
        driver.findElement(By.id("btn-in1")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0103003
     * <ul>
     * <li>同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される (カスタム設定 = 2)</li>
     * </ul>
     */
    @Test
    public void testSSMN0103003() {

        if (driver.getWrappedDriver() instanceof InternetExplorerDriver) {
            logger.warn(testName.getMethodName()
                    + " is not support Internet Explorer.");
            return;
        }

        // Main window
        driver.findElement(By.id("link3")).click();
        driver.findElement(By.id("btn-begin2")).click();
        String mainWindow = driver.getWindowHandle();

        for (int i = 0; i < 2; i++) {
            // Other window
            driver.findElement(By.id("open-new-window")).click();
            driver.switchTo().window(new LinkedList<String>(driver
                    .getWindowHandles()).getLast());
            driver.findElement(By.id("link3")).click();
            driver.findElement(By.id("btn-begin2")).click();
        }

        // Click for in
        driver.switchTo().window(mainWindow);
        driver.findElement(By.id("btn-in2")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0103004
     * <ul>
     * <li>同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される</li>
     * </ul>
     */
    @Test
    public void testSSMN0103004() {

        if (driver.getWrappedDriver() instanceof InternetExplorerDriver) {
            logger.warn(testName.getMethodName()
                    + " is not support Internet Explorer.");
            return;
        }

        // Main window
        driver.findElement(By.id("link3")).click();
        driver.findElement(By.id("btn-begin3")).click();
        String mainWindow = driver.getWindowHandle();

        // Other window
        driver.findElement(By.id("open-new-window")).click();
        driver.switchTo().window(new LinkedList<String>(driver
                .getWindowHandles()).getLast());
        driver.findElement(By.id("link3")).click();
        driver.findElement(By.id("btn-begin3")).click();

        // Click for in
        driver.switchTo().window(mainWindow);
        driver.findElement(By.id("btn-in3")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0103005
     * <ul>
     * <li>同じセッションでNameSpaceごとに保持できるTransactionTokenの数と同じ場合、自動で過去のトランザクショントークンを削除されないこと (デフォルト10個)</li>
     * </ul>
     */
    @Test
    public void testSSMN0103005() {

        if (driver.getWrappedDriver() instanceof InternetExplorerDriver) {
            logger.warn(testName.getMethodName()
                    + " is not support Internet Explorer.");
            return;
        }

        // Main window
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow1")).click();
        String mainWindow = driver.getWindowHandle();

        // The default maximum number of open window(create token)
        for (int i = 0; i < 9; i++) {
            driver.findElement(By.id("open-new-window")).click();
            driver.switchTo().window(new LinkedList<String>(driver
                    .getWindowHandles()).getLast());
            driver.findElement(By.id("link2")).click();
            driver.findElement(By.id("btn-flow1")).click();
        }

        // Click for in
        driver.switchTo().window(mainWindow);
        driver.findElement(By.id("btn-in")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}"));
    }

    /**
     * testSSMN0103006
     * <ul>
     * <li>同じセッションでNameSpaceごとに保持できるTransactionTokenの数が超えると自動で過去のトランザクショントークンを削除される(windowをMax起動し、上書きしたセッションでリクエスト送信)</li>
     * </ul>
     */
    @Test
    public void testSSMN0103006() {

        if (driver.getWrappedDriver() instanceof InternetExplorerDriver) {
            logger.warn(testName.getMethodName()
                    + " is not support Internet Explorer.");
            return;
        }

        // Main window
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow1")).click();
        String mainWindow = driver.getWindowHandle();

        // The default maximum number of open window(create token)
        for (int i = 0; i < 9; i++) {
            driver.findElement(By.id("open-new-window")).click();
            driver.switchTo().window(new LinkedList<String>(driver
                    .getWindowHandles()).getLast());
            driver.findElement(By.id("link2")).click();
            driver.findElement(By.id("btn-flow1")).click();
        }

        // Other window token check is OK (Overwrite token of the first window)
        driver.switchTo().window(new LinkedList<String>(driver
                .getWindowHandles()).get(4));
        driver.findElement(By.name("redo1")).click();
        driver.findElement(By.id("btn-flow1")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "transactiontoken~[0-9a-z]{32}~[0-9a-z]{32}"));

        // Token check of the open window in the first is NG
        driver.switchTo().window(mainWindow);
        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    /**
     * testSSMN0104001
     * <ul>
     * <li><form:form>タグを使用しない場合は、<t:transaction /> を明示的に使用することにより、同じようにTransactionTokenCheckのhidden項目が埋め込まれる</li>
     * </ul>
     */
    @Test
    public void testSSMN0104001() {
        driver.findElement(By.id("link2")).click();
        driver.findElement(By.id("btn-flow6")).click();
        assertTrue(driver.findElement(By.name("_TRANSACTION_TOKEN"))
                .getAttribute("value").matches(
                        "globalToken~[0-9a-z]{32}~[0-9a-z]{32}"));
    }

    /**
     * testSSMN0105001
     * <ul>
     * <li>セッションタイムアウト時にDBに保持しているTransactionTokenが削除される</li>
     * </ul>
     */
    @Test
    public void testSSMN0105001() {
        driver.findElement(By.id("link2")).click();

        driver.findElement(By.id("btn-flow1")).click();
        assertThat(driver.findElement(By.name("_TRANSACTION_TOKEN")),
                notNullValue());

        // wait for session timeout 1min.
        try {
            Thread.sleep(untilSessionTimeout * 1000);
        } catch (InterruptedException e) {
            fail();
        }

        driver.findElement(By.id("btn-in")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is(
                "Transaction Token Error"));
    }

    private void assertUpdateTokenValue(String token1, String token2) {
        String token1Name = token1.split("~")[0];
        String token2Name = token2.split("~")[0];
        String token1Key = token1.split("~")[1];
        String token2Key = token2.split("~")[1];
        String token1Value = token1.split("~")[2];
        String token2Value = token2.split("~")[2];

        assertThat(token2Name, is(token1Name));
        assertThat(token2Key, is(token1Key));
        assertThat(token2Value, is(not(token1Value)));
    }

}
