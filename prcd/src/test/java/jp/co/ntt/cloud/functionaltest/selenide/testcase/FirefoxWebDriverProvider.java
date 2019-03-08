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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.codeborne.selenide.WebDriverProvider;

public class FirefoxWebDriverProvider implements WebDriverProvider {

    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
        String userProfilePath = null;
        PropertyLoader loader = null;
        try {
            loader = new PropertyLoader();
            userProfilePath = loader.getValue("userProfilePath");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create object for FirefoxProfile
        FirefoxProfile profile = new FirefoxProfile(new File(userProfilePath));

        // Initialize Firefox driver
        return new FirefoxDriver(profile);

    }

    public class PropertyLoader {

        private Properties conf = null;

        public PropertyLoader() throws IOException {

            conf = new Properties();
            conf.load(this.getClass().getResourceAsStream(
                    "/META-INF/spring/selenide.properties"));
        }

        public String getValue(String key) {
            return conf.getProperty(key);
        }
    }

}
