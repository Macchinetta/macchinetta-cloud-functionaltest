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
package jp.co.ntt.cloud.functionaltest.app.view;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Bean定義ファイル内のViewResolverの有効化確認用カスタムView。
 * @author NTT 電電太郎
 */
public class CwapCustomView implements View {

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE + ";charset=utf-8";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());
        response.getWriter().append("<!DOCTYPE html>\n" + "<html>\n"
                + "<head>\n" + "<meta charset=\"utf-8\">\n"
                + "<title>Custom View</title>\n" + "<link rel=\"stylesheet\"\n"
                + "    href=\"/cwap/resources/app/css/styles.css\">\n"
                + "</head>\n" + "\n" + "<body>\n" + "    <div id=\"wrapper\">\n"
                + "        <h1>Custom View</h1>\n" + "\n"
                + "        <p><div id=\"viewName\">CustomView</div></p>\n"
                + "    </div>\n" + "</body>\n" + "</html>\n");
    }
}
