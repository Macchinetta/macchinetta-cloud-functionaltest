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
package jp.co.ntt.cloud.functionaltest.app.file;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import jp.co.ntt.cloud.functionaltest.domain.helper.S3Helper;

@Controller
@RequestMapping("download")
public class DownloadController {

	@Value("${functionaltest.download.bucketName}")
	private String bucketName;

	@Value("${functionaltest.download.expiration:30}")
	private int seconds;

	@Inject
	S3Helper s3Helper;

	/**
	 * ダウンロード画面を表示する。
	 * 
	 * @param model
	 *            {@link Model} オブジェクト
	 * @return 画面テンプレートパス
	 */
	@GetMapping
	public String index(Model model) {

		List<String> keys = new ArrayList<>();
		
		for (S3ObjectSummary object : s3Helper.listFiles(bucketName)) {
			String key = object.getKey();
			if (!key.endsWith(S3Helper.DELIMITER)) {
				keys.add(key);
			}
		}

        // S3バケット上に存在しないオブジェクトキー
        keys.add("invalid-object-key.jpg");

		model.addAttribute("keys", keys);

		return "download/index";
	}

	/**
	 * 一覧で選択されたファイルの署名付き URL を取得して返す。
	 * 
	 * クライアントは取得した書名付き URL を使って S3 から直接コンテンツをダウンロードするので EC2
	 * 上アプリにコンテンツダウンロード処理は必要ない
	 * 
	 * @param key
	 *            ダウンロード対象ファイルキー文字列
	 * @return 署名付き URL
	 */
	@GetMapping("url")
	@ResponseBody
	public DirectUrlResponse getDownloadUrl(@RequestParam("key") String key) {
		
		Date expiration = DateTime.now().plusSeconds(seconds).toDate();

		URL presignedUrl = s3Helper.generatePresignedUrl(bucketName, key, expiration);

		return new DirectUrlResponse(presignedUrl.toString());
	}

	private class DirectUrlResponse {
		private String presignedUrl;

		public DirectUrlResponse(String presignedUrl) {
			this.presignedUrl = presignedUrl;
		}

		@SuppressWarnings("unused")
		public String getPresignedUrl() {
			return presignedUrl;
		}
	}
}
