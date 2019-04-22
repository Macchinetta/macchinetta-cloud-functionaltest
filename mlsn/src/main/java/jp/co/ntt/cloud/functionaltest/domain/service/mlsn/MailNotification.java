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
package jp.co.ntt.cloud.functionaltest.domain.service.mlsn;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * メール送信通知受信機能。
 * @author NTT 電電太郎
 */
public class MailNotification {

    private String type;

    private String messageId;

    private String topicArn;

    private String messageStr;

    private Message message;

    private Date timestamp;

    private int signatureVersion;

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

    @JsonProperty("Message")
    public String getMessageStr() {
        return messageStr;
    }

    public void setMessageStr(String messageStr) {
        this.messageStr = messageStr;
    }

    @JsonProperty("Timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("SignatureVersion")
    public int getSignatureVersion() {
        return signatureVersion;
    }

    public void setSignatureVersion(int signatureVersion) {
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Message {

        private String notificationType;

        private Bounce bounce;

        private Mail mail;

        private Delivery delivery;

        private Complaint complaint;

        @JsonProperty
        public Bounce getBounce() {
            return bounce;
        }

        public void setBounce(Bounce bounce) {
            this.bounce = bounce;
        }

        @JsonProperty
        public Mail getMail() {
            return mail;
        }

        public void setMail(Mail mail) {
            this.mail = mail;
        }

        @JsonProperty
        public String getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(String notificationType) {
            this.notificationType = notificationType;
        }

        @JsonProperty
        public Delivery getDelivery() {
            return delivery;
        }

        public void setDelivery(Delivery delivery) {
            this.delivery = delivery;
        }

        @JsonProperty
        public Complaint getComplaint() {
            return complaint;
        }

        public void setComplaint(Complaint complaint) {
            this.complaint = complaint;
        }

        public static class Complaint {

            private List<ComplainedRecipient> complainedRecipients;

            private Date timestamp;

            private String feedbackId;

            private String userAgent;

            private String complaintFeedbackType;

            @JsonProperty
            public List<ComplainedRecipient> getComplainedRecipients() {
                return complainedRecipients;
            }

            public void setComplainedRecipients(
                    List<ComplainedRecipient> complainedRecipients) {
                this.complainedRecipients = complainedRecipients;
            }

            @JsonProperty
            public Date getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Date timestamp) {
                this.timestamp = timestamp;
            }

            @JsonProperty
            public String getFeedbackId() {
                return feedbackId;
            }

            public void setFeedbackId(String feedbackId) {
                this.feedbackId = feedbackId;
            }

            @JsonProperty
            public String getUserAgent() {
                return userAgent;
            }

            public void setUserAgent(String userAgent) {
                this.userAgent = userAgent;
            }

            @JsonProperty
            public String getComplaintFeedbackType() {
                return complaintFeedbackType;
            }

            public void setComplaintFeedbackType(String complaintFeedbackType) {
                this.complaintFeedbackType = complaintFeedbackType;
            }

            public static class ComplainedRecipient {
                private String emailAddress;

                @JsonProperty
                public String getEmailAddress() {
                    return emailAddress;
                }

                public void setEmailAddress(String emailAddress) {
                    this.emailAddress = emailAddress;
                }
            }
        }

        public static class Delivery {

            private Date timestamp;

            private long processingTimeMillis;

            private List<String> recipients;

            private String smtpResponse;

            private String remoteMtaIp;

            private String reportingMTA;

            @JsonProperty
            public Date getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Date timestamp) {
                this.timestamp = timestamp;
            }

            @JsonProperty
            public long getProcessingTimeMillis() {
                return processingTimeMillis;
            }

            public void setProcessingTimeMillis(long processingTimeMillis) {
                this.processingTimeMillis = processingTimeMillis;
            }

            @JsonProperty
            public List<String> getRecipients() {
                return recipients;
            }

            public void setRecipients(List<String> recipients) {
                this.recipients = recipients;
            }

            @JsonProperty
            public String getSmtpResponse() {
                return smtpResponse;
            }

            public void setSmtpResponse(String smtpResponse) {
                this.smtpResponse = smtpResponse;
            }

            @JsonProperty
            public String getRemoteMtaIp() {
                return remoteMtaIp;
            }

            public void setRemoteMtaIp(String remoteMtaIp) {
                this.remoteMtaIp = remoteMtaIp;
            }

            @JsonProperty
            public String getReportingMTA() {
                return reportingMTA;
            }

            public void setReportingMTA(String reportingMTA) {
                this.reportingMTA = reportingMTA;
            }
        }

        public static class CommonHeaders {

            private List<String> from;

            private List<String> to;

            private String messageId;

            private String subject;

            public List<String> getFrom() {
                return from;
            }

            public void setFrom(List<String> from) {
                this.from = from;
            }

            public List<String> getTo() {
                return to;
            }

            public void setTo(List<String> to) {
                this.to = to;
            }

            public String getMessageId() {
                return messageId;
            }

            public void setMessageId(String messageId) {
                this.messageId = messageId;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }
        }

        public static class Mail {

            private Date timestamp;

            private String source;

            private String sourceArn;

            private String sourceIp;

            private String sendingAccountId;

            private String messageId;

            private List<String> destination;

            private boolean headersTruncated;

            private List<Header> headers;

            private CommonHeaders commonHeaders;

            @JsonProperty
            public Date getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Date timestamp) {
                this.timestamp = timestamp;
            }

            @JsonProperty
            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            @JsonProperty
            public String getSourceArn() {
                return sourceArn;
            }

            public void setSourceArn(String sourceArn) {
                this.sourceArn = sourceArn;
            }

            @JsonProperty
            public String getSourceIp() {
                return sourceIp;
            }

            public void setSourceIp(String sourceIp) {
                this.sourceIp = sourceIp;
            }

            @JsonProperty
            public String getSendingAccountId() {
                return sendingAccountId;
            }

            public void setSendingAccountId(String sendingAccountId) {
                this.sendingAccountId = sendingAccountId;
            }

            @JsonProperty
            public String getMessageId() {
                return messageId;
            }

            public void setMessageId(String messageId) {
                this.messageId = messageId;
            }

            @JsonProperty
            public List<String> getDestination() {
                return destination;
            }

            public void setDestination(List<String> destination) {
                this.destination = destination;
            }

            @JsonProperty
            public boolean isHeadersTruncated() {
                return headersTruncated;
            }

            public void setHeadersTruncated(boolean headersTruncated) {
                this.headersTruncated = headersTruncated;
            }

            @JsonProperty
            public List<Header> getHeaders() {
                return headers;
            }

            public void setHeaders(List<Header> headers) {
                this.headers = headers;
            }

            @JsonProperty
            public CommonHeaders getCommonHeaders() {
                return commonHeaders;
            }

            public void setCommonHeaders(CommonHeaders commonHeaders) {
                this.commonHeaders = commonHeaders;
            }

            public static class Header {

                private String name;

                private String value;

                @JsonProperty
                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                @JsonProperty
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }

        public static class Bounce {

            private String bounceType;

            private String bounceSubType;

            private List<BouncedRecipients> bouncedRecipients;

            private Date timestamp;

            private String feedbackId;

            private String remoteMtaIp;

            private String reportingMTA;

            @JsonProperty
            public String getBounceType() {
                return bounceType;
            }

            public void setBounceType(String bounceType) {
                this.bounceType = bounceType;
            }

            @JsonProperty
            public String getBounceSubType() {
                return bounceSubType;
            }

            public void setBounceSubType(String bounceSubType) {
                this.bounceSubType = bounceSubType;
            }

            @JsonProperty
            public List<BouncedRecipients> getBouncedRecipients() {
                return bouncedRecipients;
            }

            public void setBouncedRecipients(
                    List<BouncedRecipients> bouncedRecipients) {
                this.bouncedRecipients = bouncedRecipients;
            }

            @JsonProperty
            public Date getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Date timestamp) {
                this.timestamp = timestamp;
            }

            @JsonProperty
            public String getFeedbackId() {
                return feedbackId;
            }

            public void setFeedbackId(String feedbackId) {
                this.feedbackId = feedbackId;
            }

            @JsonProperty
            public String getRemoteMtaIp() {
                return remoteMtaIp;
            }

            public void setRemoteMtaIp(String remoteMtaIp) {
                this.remoteMtaIp = remoteMtaIp;
            }

            @JsonProperty
            public String getReportingMTA() {
                return reportingMTA;
            }

            public void setReportingMTA(String reportingMTA) {
                this.reportingMTA = reportingMTA;
            }

            public static class BouncedRecipients {

                private String emailAddress;

                private String action;

                private String status;

                private String diagnosticCode;

                public String getEmailAddress() {
                    return emailAddress;
                }

                public void setEmailAddress(String emailAddress) {
                    this.emailAddress = emailAddress;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getDiagnosticCode() {
                    return diagnosticCode;
                }

                public void setDiagnosticCode(String diagnosticCode) {
                    this.diagnosticCode = diagnosticCode;
                }
            }
        }
    }
}
