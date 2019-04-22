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
package jp.co.ntt.cloud.functionaltest.app.fileupload;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.message.ResultMessages;

import jp.co.ntt.cloud.functionaltest.domain.service.fileupload.MaintenanceService;

/**
 * メンテナンス画面表示コントローラ。
 * @author NTT 電電太郎
 */
@Controller
public class MaintenanceController {

    /** ロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(
            MaintenanceController.class);

    /** ファイルアップロード処理用サービス */
    @Inject
    MaintenanceService fileUploadService;

    /**
     * メンテナンス画面を初期表示する。
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(value = "/maintenance", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String maintenance(Model model) {
        logger.info("do Maintenance.");

        model.addAttribute("bucketNameList", FileUploadControllerUtil
                .createBucketPulldown(false));
        model.addAttribute(new MaintenanceForm());
        return "fileupload/maintenance";
    }

    /**
     * ファイルアップロードを実行する。
     * @param form 画面フォーム
     * @param result バリデーション結果
     * @param redirectAttributes リダイレクトパラメータ
     * @return View論理名
     */
    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public String upload(@Validated MaintenanceForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        logger.info("do File Upload.");

        if (result.hasErrors()) {
            return "fileupload/maintenance";
        }

        fileUploadService.doUpload(form.getuUploadUser(), form.getuBucketName(),
                form.getuFile());

        redirectAttributes.addFlashAttribute(ResultMessages.success().add(
                "i.xx.at.0001"));

        return "redirect:/upload?complete";
    }

    /**
     * アップロード完了後画面（自画面）への遷移。
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(value = "/upload", method = RequestMethod.GET, params = "complete")
    public String uploadComplete(Model model) {
        logger.info("File Upload completed.");
        model.addAttribute("bucketNameList", FileUploadControllerUtil
                .createBucketPulldown(false));
        model.addAttribute(new MaintenanceForm());
        return "fileupload/maintenance";
    }

    /**
     * ファイル削除を実行する。
     * @param form 画面フォーム
     * @param result バリデーション結果
     * @param redirectAttributes リダイレクトパラメータ
     * @return View論理名
     */
    @RequestMapping(value = "/delete", method = { RequestMethod.POST })
    public String delete(@Validated MaintenanceForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        logger.info("do File Delete.");

        if (result.hasErrors()) {
            return "fileupload/maintenance";
        }

        fileUploadService.doDelete(form.getdBucketName(), form.getdObjectKey(),
                form.getdUploadUser());

        redirectAttributes.addFlashAttribute(ResultMessages.success().add(
                "i.xx.at.0001"));

        return "redirect:/delete?complete";
    }

    /**
     * 削除完了後画面（自画面）への遷移。
     * @param model 出力情報を保持するクラス
     * @return View論理名
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET, params = "complete")
    public String deleteComplete(Model model) {
        logger.info("File Delete completed.");
        model.addAttribute("bucketNameList", FileUploadControllerUtil
                .createBucketPulldown(false));
        model.addAttribute(new MaintenanceForm());
        return "fileupload/maintenance";
    }
}
