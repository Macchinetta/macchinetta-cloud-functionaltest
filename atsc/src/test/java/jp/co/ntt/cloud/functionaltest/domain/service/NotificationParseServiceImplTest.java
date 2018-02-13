/*
 * Copyright 2014-2017 NTT Corporation.
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
package jp.co.ntt.cloud.functionaltest.domain.service;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NotificationParseServiceImplTest {

    private final NotificationParseService target = new NotificationParseServiceImpl();

    @Test
    public void testParse() throws Exception {
        final String json = "{ \"Type\" : \"Notification\", \"MessageId\" : \"f5ca37d5-f29a-5900-b243-6550fd19f4b1\", \"TopicArn\" : \"arn:aws:sns:ap-northeast-1:905391819229:TEST_CWALM\", \"Subject\" : \"ALARM: \\\"TestAlarmNotification\\\" in Asia Pacific (Tokyo)\", \"Message\" : \"{\\\"AlarmName\\\":\\\"TestAlarmNotification\\\",               \\\"AlarmDescription\\\":\\\"機能試験オートスケーリング代用通知用\\\",               \\\"AWSAccountId\\\":\\\"905391819229\\\",               \\\"NewStateValue\\\":\\\"ALARM\\\",               \\\"NewStateReason\\\":\\\"Threshold Crossed: 1 datapoint [2.841116672E9 (14/11/17 15:34:00)] was greater than the threshold (0.0).\\\",               \\\"StateChangeTime\\\":\\\"2017-11-14T15:35:31.593+0000\\\",               \\\"Region\\\":\\\"Asia Pacific (Tokyo)\\\",               \\\"OldStateValue\\\":\\\"OK\\\",               \\\"Trigger\\\":{\\\"MetricName\\\":\\\"HeapMemory.Max\\\",                            \\\"Namespace\\\":\\\"local\\\",                            \\\"StatisticType\\\":\\\"Statistic\\\",                            \\\"Statistic\\\":\\\"MAXIMUM\\\",                            \\\"Unit\\\":null,                            \\\"Dimensions\\\":[{\\\"name\\\":\\\"AutoScalingGroupName\\\", \\\"value\\\":\\\"atscGroup\\\"},                                            {\\\"name\\\":\\\"instanceId\\\", \\\"value\\\":\\\"localhost\\\"}],                            \\\"Period\\\":60,                            \\\"EvaluationPeriods\\\":1,                            \\\"ComparisonOperator\\\":\\\"GreaterThanThreshold\\\",                            \\\"Threshold\\\":0.0,                            \\\"TreatMissingData\\\":\\\"- TreatMissingData: NonBreaching\\\",                            \\\"EvaluateLowSampleCountPercentile\\\":\\\"\\\"}}\",  \"Timestamp\" : \"2017-11-14T15:35:31.736Z\",  \"SignatureVersion\" : \"1\",  \"Signature\" : \"gVCDbn+KVN3GHcpmU46Gz6hQZ6m5A9VaEJVQdcY+W7QMN5GOvASSp4/VSaN89cM8oCKrTMsGg9zV94mTP+6s4SACmpXVfP1Mbp4tQ3QKql6IbRm4tlt+8rIKv22Mt2LEBiMWf8ayPsuPA8zPbiamlmubWzyqtUvtnFIW3hsGm3Gn4ElgtfmTQHOhfqOHaHPVnfE3zovacLcwLroPVmenykm5t0OyYanUNrFAolPTkXSpDVSN85VvkhtiYYNe7VW+q//24narLynogS8L6V9+s3uB4Hx1UFyY17wJR9Ov6mgVUnUxSMpqX5fKC+K2NJrnu6kMHy9QS8W8+/L4B3PpJw==\",  \"SigningCertURL\" : \"https://sns.ap-northeast-1.amazonaws.com/SimpleNotificationService-433026a4050d206028891664da859041.pem\",  \"UnsubscribeURL\" : \"https://sns.ap-northeast-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:ap-northeast-1:905391819229:TEST_CWALM:b5691cae-0f39-4fb9-acb6-1e6f65170838\" }";
        Notification notification = target.parseNotification(json);
        assertThat(notification.getMessageId(), is("f5ca37d5-f29a-5900-b243-6550fd19f4b1"));
        assertThat(notification.getMessage().getNewStateValue(), is("ALARM"));
        assertThat(notification.getMessage().getNewStateReason(), is(containsString("Threshold Crossed")));
        assertThat(notification.getMessage().getTrigger().getMetricName(), is("HeapMemory.Max"));
        assertThat(Arrays.asList(notification.getMessage().getTrigger().getDimensions()),
            is(contains(
                Notification.Message.Trigger.Dimension.DimensionBuilder.builder()
                    .name("AutoScalingGroupName")
                    .value("atscGroup")
                    .build(),
                Notification.Message.Trigger.Dimension.DimensionBuilder.builder()
                    .name("instanceId")
                    .value("localhost")
                    .build()
            ))
        );
    }
}
