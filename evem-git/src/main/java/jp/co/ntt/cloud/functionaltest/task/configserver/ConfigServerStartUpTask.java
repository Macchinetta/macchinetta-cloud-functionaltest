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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Config Serverを起動するタスク
 * @author NTT 電電太郎
 */
public class ConfigServerStartUpTask extends Task {

    /**
     * Config Server プロジェクトへのパス
     */
    private String configServerProjectDir;

    /**
     * Config Server 起動プロファイル
     */
    private String configServerProfile;

    /**
     * Config Server が起動しているかチェックするURL
     */
    private String configServerPingUrl;

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
     * Config Serverが起動しているかチェックするURL
     */
    private String mvnPath;

    /**
     * Config Server プロジェクトへのパスを設定する。
     * @param configServerProjectDir Config Server プロジェクトへのパス
     */
    public void setConfigServerProjectDir(String configServerProjectDir) {
        this.configServerProjectDir = whenSetValidation(configServerProjectDir);
    }

    /**
     * Config Server プロファイルを設定する。
     * @param configServerProfile Config Server プロファイル
     */
    public void setConfigServerProfile(String configServerProfile) {
        this.configServerProfile = whenSetValidation(configServerProfile);
    }

    /**
     * Config Server が起動しているかチェックするURLを設定する。
     * @param configServerPingUrl Config Server が起動しているかチェックするURL
     */
    public void setConfigServerPingUrl(String configServerPingUrl) {
        this.configServerPingUrl = whenSetValidation(configServerPingUrl);
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
     * @param configPassword Config Serverに接続するPassword
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

    /**
     * mvnへのパスを設定する。
     * @param mvnPath mvnへのパス
     */
    public void setMvnPath(String mvnPath) {
        this.mvnPath = whenSetValidation(mvnPath);
    }

    /*
     * (非 Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {

        Path dir = Paths.get(configServerProjectDir).resolve("pom.xml");
        String pomPath = dir.toAbsolutePath().toString();

        ProcessBuilder processBuilder = new ProcessBuilder(mvnPath, "-f", pomPath, "spring-boot:run", "-P", configServerProfile);

        processBuilder.inheritIO();

        try {
            System.out.println(
                    "*********************** Config Server を起動します。pomPath="
                            + pomPath + " configServerProfile="
                            + configServerProfile + " timeout=" + timeout
                            + " mvnPath=" + mvnPath + " configServerPingUrl="
                            + configServerPingUrl + " ***********************");
            processBuilder.start();
        } catch (IOException e) {
            throw new BuildException(e);
        }

        // Basic認証設定
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(configUser, configPassword));

        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider).build();

        long start = System.currentTimeMillis();
        long timeDiff = 0;
        boolean configServerStartUpFlag = false;
        while (true) {
            try (CloseableHttpResponse response = client.execute(
                    new HttpGet(configServerPingUrl))) {
                if (200 == response.getStatusLine().getStatusCode()) {
                    System.out.println(
                            "*********************** Config Server が起動しました。 ***********************");
                    configServerStartUpFlag = true;
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            if (configServerStartUpFlag) {
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
                throw new BuildException("Config Server の起動がタイムアウトしました。 timeout="
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
