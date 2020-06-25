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

import java.util.LinkedHashMap;
import java.util.Map;

public class FileUploadControllerUtil {

    /** 使用バケット名リスト */
    private static final String[] BUCKET_NAME_ARR = new String[] { "func-temp",
            "func-bucket" };

    /**
     * アップロード先バケット名のマップを返却する。
     * @return バケット名マップ
     */
    public static Map<String, String> createBucketPulldown(
            boolean enableBlank) {
        Map<String, String> buckets = new LinkedHashMap<>();
        if (enableBlank) {
            buckets.put("", "");
        }
        for (String bucketName : BUCKET_NAME_ARR) {
            buckets.put(bucketName, bucketName);
        }
        return buckets;
    }
}
