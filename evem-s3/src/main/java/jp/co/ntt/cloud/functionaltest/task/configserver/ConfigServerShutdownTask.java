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
package jp.co.ntt.cloud.functionaltest.task.configserver;

import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Config Serverを終了するタスク
 * @author NTT 電電太郎
 */
public class ConfigServerShutdownTask extends Task {

    /**
     * Config Serverが起動しているかチェックするURL
     */
    private String configServerPingUrl;

    /**
     * Config Serverを終了するように指示するエンドポイント
     */
    private String configServerShutdownUrl;

    /**
     * Config Serverに接続する user名
     */
    private String configUser;

    /**
     * Config Serverに接続するPassword
     */
    private String configPassword;

    /**
     * Config Server が起動するまでのタイムアウト時間
     */
    private int timeout;

    /**
     * Config Server が起動するまでのデフォルトタイムアウト時間
     */
    private static final int DEFAULT_TIME_OUT = 300000;

    /**
     * Config Serverが起動しているかチェックするURLを設定する。
     * @param configServerPingUrl Config Serverが起動しているかチェックするURL
     */
    public void setConfigServerPingUrl(String configServerPingUrl) {
        this.configServerPingUrl = whenSetValidation(configServerPingUrl);
    }

    /**
     * Config Serverを終了するように指示するエンドポイントを設定する。
     * @param configServerShutdownUrl Config Serverを終了するように指示するエンドポイント
     */
    public void setConfigServerShutdownUrl(String configServerShutdownUrl) {
        this.configServerShutdownUrl = whenSetValidation(
                configServerShutdownUrl);
    }

    /**
     * Config Serverに接続する user名を設定する。
     * @param configUser Config Serverに接続する user名
     */
    public void setConfigServerUser(String configUser) {
        this.configUser = whenSetValidation(configUser);
    }

    /**
     * Config Serverに接続するPasswordを設定する。
     * @param configPassword Config Serverに接続する password名
     */
    public void setConfigServerPassword(String configPassword) {
        this.configPassword = whenSetValidation(configPassword);
    }

    /**
     * Config Server が起動するまでのタイムアウト時間を設定する。
     * @param timeout Config Server が起動するまでのタイムアウト時間
     */
    public void setTimeout(String timeout) {
        if (timeout != null && timeout.startsWith("${")) {
            this.timeout = DEFAULT_TIME_OUT;
        }
        this.timeout = Integer.valueOf(timeout);
    }

    /*
     * (非 Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {

        System.out.println("*********************** Config Server を終了します。 "
                + " timeout=" + timeout + " configServerShutdownUrl="
                + configServerShutdownUrl + " configServerPingUrl="
                + configServerPingUrl + " ***********************");

        // Basic認証設定
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(configUser, configPassword));

        // Timeout設定
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(
                timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(
                        timeout).build();

        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(requestConfig).build();

        long start = System.currentTimeMillis();
        long timeDiff = 0;
        boolean postConfigServerShutdownEndPointFlag = false;
        while (true) {
            try (CloseableHttpResponse response = client.execute(
                    new HttpPost(configServerShutdownUrl))) {
                if (200 == response.getStatusLine().getStatusCode()) {
                    System.out.println(
                            "*********************** Config Server を終了するエンドポイントへPOSTしました。 ***********************");
                    postConfigServerShutdownEndPointFlag = true;
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            if (postConfigServerShutdownEndPointFlag) {
                break;
            }

            try {
                Thread.sleep(100); // 100ミリ秒Sleepする
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long end = System.currentTimeMillis();
            timeDiff = end - start;
            if (timeDiff >= timeout) {
                throw new BuildException("Config Server を終了するエンドポイントへのPOSTがタイムアウトしました。 timeout="
                        + timeout + " ms");
            }
        }

        start = System.currentTimeMillis();
        timeDiff = 0;
        boolean shutdownConfigServerCompleteFlag = false;
        while (true) {
            try (CloseableHttpResponse response = client.execute(
                    new HttpGet(configServerPingUrl))) {
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
                System.out.println(
                        "*********************** Config Server が終了しました。 ***********************");
                shutdownConfigServerCompleteFlag = true;
            }

            if (shutdownConfigServerCompleteFlag) {
                break;
            }

            try {
                Thread.sleep(100); // 100ミリ秒Sleepする
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long end = System.currentTimeMillis();
            timeDiff = end - start;
            if (timeDiff >= timeout) {
                throw new BuildException("Config Server を終了できませんでした。タイムアウトしました。 timeout="
                        + timeout + " ms");
            }
        }

    }

    /**
     * 入力文字列チェック
     * @param str 入力文字列
     * @return 入力文字列("${" で始まっていたら {@link null}を返す)
     */
    private String whenSetValidation(String str) {
        if (str != null && str.startsWith("${")) {
            return null;
        }
        return str;
    }
}
