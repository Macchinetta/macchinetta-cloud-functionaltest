/*
 * Copyright 2014-2020 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.app.fileupload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.message.ResultMessages;

import jp.co.ntt.cloud.functionaltest.app.common.constants.WebPagePathConstants;
import jp.co.ntt.cloud.functionaltest.domain.model.FileMetaData;
import jp.co.ntt.cloud.functionaltest.domain.service.fileupload.SearchSharedService;

/**
 * 検索画面表示コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class SearchController {

    /** ロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(
            SearchController.class);

    /** ファイル検索用サービス */
    @Inject
    SearchSharedService searchSharedService;

    /**
     * 検索画面を初期表示する。
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @GetMapping(value = WebPagePathConstants.ROOT_HOME)
    public String home(Model model) {

        model.addAttribute("bucketNameList", FileUploadControllerUtil
                .createBucketPulldown(true));
        model.addAttribute(new SearchForm());
        return WebPagePathConstants.FILEUPLOAD_SEARCH;
    }

    /**
     * ファイル検索を実行する。
     * @param model 出力情報を保持するクラス
     * @param form 画面フォーム
     * @param result バリデーション結果
     * @param redirectAttributes リダイレクトパラメータ
     */
    @PostMapping(value = WebPagePathConstants.SEARCH)
    public String search(Model model, @Validated SearchForm form,
            BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("do File Search.");

        if (result.hasErrors()) {
            return WebPagePathConstants.FILEUPLOAD_SEARCH;
        }

        List<FileMetaData> srchRsltList = null;

        // objectKeyが指定された場合はハッシュキー検索を実行する
        if (form.getObjectKey().length() > 0) {
            FileMetaData srchRslt = searchSharedService.doPkSearch(form
                    .getObjectKey());
            srchRsltList = new ArrayList<>();
            srchRsltList.add(srchRslt);

            // uploadUserが指定された場合はセカンダリインデックス検索を実行する
        } else if (form.getUploadUser().length() > 0) {
            srchRsltList = searchSharedService.doUserIdIndexSearch(form
                    .getUploadUser(), form.getUploadDate());

            // バケット名が指定された場合はセカンダリインデックス検索を実行する
        } else if (form.getBucketName().length() > 0) {
            srchRsltList = searchSharedService.doBucketNameIndexSearch(form
                    .getBucketName());

            // 条件未指定の場合は全件検索を実行する
        } else {
            srchRsltList = searchSharedService.doSearch();
        }
        model.addAttribute("resultList", srchRsltList);

        model.addAttribute("bucketNameList", FileUploadControllerUtil
                .createBucketPulldown(true));

        redirectAttributes.addFlashAttribute(ResultMessages.success().add(
                "i.xx.at.0001"));

        return WebPagePathConstants.FILEUPLOAD_SEARCH;
    }
}
