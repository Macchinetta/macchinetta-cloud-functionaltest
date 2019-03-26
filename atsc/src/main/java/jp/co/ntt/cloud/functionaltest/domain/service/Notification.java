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
package jp.co.ntt.cloud.functionaltest.domain.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * アラーム通知JSONマッピングクラス。
 * @author NTT 電電太郎
 */
public class Notification {

    private String type;

    private String messageId;

    private String topicArn;

    private String subject;

    private String messageStr;

    private Message message;

    private Date timestamp;

    private long signatureVersion;

    private String signature;

    private String signingCertURL;

    private String unsubscribeURL;

    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("MessageId")
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("TopicArn")
    public String getTopicArn() {
        return topicArn;
    }

    public void setTopicArn(String topicArn) {
        this.topicArn = topicArn;
    }

    @JsonProperty("Subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @JsonProperty("Timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("SignatureVersion")
    public long getSignatureVersion() {
        return signatureVersion;
    }

    public void setSignatureVersion(long signatureVersion) {
        this.signatureVersion = signatureVersion;
    }

    @JsonProperty("Signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @JsonProperty("SigningCertURL")
    public String getSigningCertURL() {
        return signingCertURL;
    }

    public void setSigningCertURL(String signingCertURL) {
        this.signingCertURL = signingCertURL;
    }

    @JsonProperty("UnsubscribeURL")
    public String getUnsubscribeURL() {
        return unsubscribeURL;
    }

    public void setUnsubscribeURL(String unsubscribeURL) {
        this.unsubscribeURL = unsubscribeURL;
    }

    @JsonProperty("Message")
    public String getMessageStr() {
        return messageStr;
    }

    public void setMessageStr(String messageStr) {
        this.messageStr = messageStr;
    }

    public static class Message {
        private String alarmName;

        private String alarmDescription;

        private String awsAccountId;

        private String newStateValue;

        private String newStateReason;

        private Date stateChangeTime;

        private String region;

        private String oldStateValue;

        private Trigger trigger;

        @JsonProperty("AlarmName")
        public String getAlarmName() {
            return alarmName;
        }

        public void setAlarmName(String alarmName) {
            this.alarmName = alarmName;
        }

        @JsonProperty("AlarmDescription")
        public String getAlarmDescription() {
            return alarmDescription;
        }

        public void setAlarmDescription(String alarmDescription) {
            this.alarmDescription = alarmDescription;
        }

        @JsonProperty("AWSAccountId")
        public String getAwsAccountId() {
            return awsAccountId;
        }

        public void setAwsAccountId(String awsAccountId) {
            this.awsAccountId = awsAccountId;
        }

        @JsonProperty("NewStateValue")
        public String getNewStateValue() {
            return newStateValue;
        }

        public void setNewStateValue(String newStateValue) {
            this.newStateValue = newStateValue;
        }

        @JsonProperty("NewStateReason")
        public String getNewStateReason() {
            return newStateReason;
        }

        public void setNewStateReason(String newStateReason) {
            this.newStateReason = newStateReason;
        }

        @JsonProperty("StateChangeTime")
        public Date getStateChangeTime() {
            return stateChangeTime;
        }

        public void setStateChangeTime(Date stateChangeTime) {
            this.stateChangeTime = stateChangeTime;
        }

        @JsonProperty("Region")
        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        @JsonProperty("OldStateValue")
        public String getOldStateValue() {
            return oldStateValue;
        }

        public void setOldStateValue(String oldStateValue) {
            this.oldStateValue = oldStateValue;
        }

        @JsonProperty("Trigger")
        public Trigger getTrigger() {
            return trigger;
        }

        public void setTrigger(Trigger trigger) {
            this.trigger = trigger;
        }

        public static class Trigger {

            private String metricName;

            private String namespace;

            private String statisticType;

            private String statistic;

            private String unit;

            private Dimension[] dimensions;

            private int period;

            private int evaluationPeriods;

            private String ComparisonOperator;

            private double threshold;

            private String treatMissingData;

            private String evaluateLowSampleCountPercentile;

            @JsonProperty("MetricName")
            public String getMetricName() {
                return metricName;
            }

            public void setMetricName(String metricName) {
                this.metricName = metricName;
            }

            @JsonProperty("Namespace")
            public String getNamespace() {
                return namespace;
            }

            public void setNamespace(String namespace) {
                this.namespace = namespace;
            }

            @JsonProperty("StatisticType")
            public String getStatisticType() {
                return statisticType;
            }

            public void setStatisticType(String statisticType) {
                this.statisticType = statisticType;
            }

            @JsonProperty("Statistic")
            public String getStatistic() {
                return statistic;
            }

            public void setStatistic(String statistic) {
                this.statistic = statistic;
            }

            @JsonProperty("Unit")
            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            @JsonProperty("Dimensions")
            public Dimension[] getDimensions() {
                return dimensions;
            }

            public void setDimensions(Dimension[] dimensions) {
                this.dimensions = dimensions;
            }

            @JsonProperty("Period")
            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }

            @JsonProperty("EvaluationPeriods")
            public int getEvaluationPeriods() {
                return evaluationPeriods;
            }

            public void setEvaluationPeriods(int evaluationPeriods) {
                this.evaluationPeriods = evaluationPeriods;
            }

            @JsonProperty("ComparisonOperator")
            public String getComparisonOperator() {
                return ComparisonOperator;
            }

            public void setComparisonOperator(String comparisonOperator) {
                ComparisonOperator = comparisonOperator;
            }

            @JsonProperty("Threshold")
            public double getThreshold() {
                return threshold;
            }

            public void setThreshold(double threshold) {
                this.threshold = threshold;
            }

            @JsonProperty("TreatMissingData")
            public String getTreatMissingData() {
                return treatMissingData;
            }

            public void setTreatMissingData(String treatMissingData) {
                this.treatMissingData = treatMissingData;
            }

            @JsonProperty("EvaluateLowSampleCountPercentile")
            public String getEvaluateLowSampleCountPercentile() {
                return evaluateLowSampleCountPercentile;
            }

            public void setEvaluateLowSampleCountPercentile(
                    String evaluateLowSampleCountPercentile) {
                this.evaluateLowSampleCountPercentile = evaluateLowSampleCountPercentile;
            }

            public static class Dimension {

                private String name;

                private String value;

                @Override
                public String toString() {
                    return String.format("%s:%s", name, value);
                }

                @Override
                public int hashCode() {
                    return toString().hashCode();
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == null) {
                        return false;
                    }
                    return this.hashCode() == obj.hashCode();
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public static class DimensionBuilder {
                    private DimensionBuilder() {
                    }

                    private final Dimension dimension = new Dimension();

                    public static DimensionBuilder builder() {
                        return new DimensionBuilder();
                    }

                    public DimensionBuilder name(String name) {
                        dimension.name = name;
                        return this;
                    }

                    public DimensionBuilder value(String value) {
                        dimension.value = value;
                        return this;
                    }

                    public Dimension build() {
                        return dimension;
                    }
                }
            }
        }
    }
}
