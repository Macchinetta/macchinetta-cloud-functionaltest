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
package jp.co.ntt.cloud.functionaltest.app.atsc;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Objects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.util.EC2MetadataUtils;

/**
 * CloudWatchメトリクス送信クラス。
 * @author NTT 電電太郎
 */
@ConfigurationProperties(prefix = "custom.metric")
public class CloudWatchMetricSender implements InitializingBean {

    @Value("${cloud.aws.cloudwatch.region:}")
    String region;

    @Value("${spring.application.name:autoScalingGroupName}")
    String autoScalingGroupName;

    @Value("${cloud.aws.cloudwatch.namespace:}")
    String namespace;

    private AmazonCloudWatch amazonCloudWatch;

    private String instanceId;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(region)) {
            this.amazonCloudWatch = AmazonCloudWatchClientBuilder
                    .defaultClient();
        } else {
            this.amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                    .withRegion(region).build();
        }

        try {
            EC2MetadataUtils.InstanceInfo instanceInfo = EC2MetadataUtils
                    .getInstanceInfo();
            if (Objects.isNull(instanceInfo)) {
                resolveInstanceIdWithLocalHostAddress();
            } else {
                this.instanceId = instanceInfo.getInstanceId();
            }

        } catch (AmazonClientException e) {
            resolveInstanceIdWithLocalHostAddress();
        }

    }

    @Scheduled(fixedRate = 5000)
    public void sendCloudWatch() {
        MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = mBean.getHeapMemoryUsage();
        Dimension InstanceIdDimension = new Dimension().withName("instanceId")
                .withValue(instanceId);

        Dimension AutoScalingGroupNameDimension = new Dimension().withName(
                "AutoScalingGroupName").withValue(autoScalingGroupName);

        PutMetricDataRequest request = new PutMetricDataRequest().withNamespace(
                namespace).withMetricData(
                        // Used
                        new MetricDatum().withDimensions(InstanceIdDimension,
                                AutoScalingGroupNameDimension).withMetricName(
                                        "HeapMemory.Used").withUnit(
                                                StandardUnit.Bytes.toString())
                                .withValue((double) heapUsage.getUsed()),
                        // Max
                        new MetricDatum().withDimensions(InstanceIdDimension,
                                AutoScalingGroupNameDimension).withMetricName(
                                        "HeapMemory.Max").withUnit(
                                                StandardUnit.Bytes.toString())
                                .withValue((double) heapUsage.getMax()),
                        // Committed
                        new MetricDatum().withDimensions(InstanceIdDimension,
                                AutoScalingGroupNameDimension).withMetricName(
                                        "HeapMemory.Committed").withUnit(
                                                StandardUnit.Bytes.toString())
                                .withValue((double) heapUsage.getCommitted()),
                        // Utilization
                        new MetricDatum().withDimensions(InstanceIdDimension,
                                AutoScalingGroupNameDimension).withMetricName(
                                        "HeapMemory.Utilization").withUnit(
                                                StandardUnit.Percent.toString())
                                .withValue(100 * ((double) heapUsage.getUsed()
                                        / (double) heapUsage.getMax())));

        amazonCloudWatch.putMetricData(request);
    }

    private void resolveInstanceIdWithLocalHostAddress() {
        // インスタンスID(=IPアドレス)が試験実施環境によって変化してしまうため、
        // localhostで固定化させている。
        this.instanceId = "localhost";
    }

    public String getAutoScalingGroupName() {
        return autoScalingGroupName;
    }

    public void setAutoScalingGroupName(String autoScalingGroupName) {
        this.autoScalingGroupName = autoScalingGroupName;
    }
}
