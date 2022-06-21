package ru.newsystems.basecore.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    @JsonProperty("ArticleID")
    private String articleID;
    @JsonProperty("ArticleNumber")
    private String articleNumber;
    @JsonProperty("Bcc")
    private String bcc;
    @JsonProperty("Body")
    private String body;
    @JsonProperty("Cc")
    private String cc;
    @JsonProperty("ChangeBy")
    private String changeBy;
    @JsonProperty("ChangeTime")
    private String changeTime;
    @JsonProperty("Charset")
    private String charset;
    @JsonProperty("CommunicationChannelID")
    private String communicationChannelID;
    @JsonProperty("ContentCharset")
    private String contentCharset;
    @JsonProperty("ContentType")
    private String contentType;
    @JsonProperty("CreateBy")
    private String createBy;
    @JsonProperty("CreateTime")
    private String createTime;
    @JsonProperty("From")
    private String from;
    @JsonProperty("InReplyTo")
    private String inReplyTo;
    @JsonProperty("IncomingTime")
    private String incomingTime;
    @JsonProperty("IsVisibleForCustomer")
    private String IiVisibleForCustomer;
    @JsonProperty("MessageID")
    private String messageID;
    @JsonProperty("MimeType")
    private String mimeType;
    @JsonProperty("References")
    private String references;
    @JsonProperty("ReplyTo")
    private String replyTo;
    @JsonProperty("SenderType")
    private String senderType;
    @JsonProperty("SenderTypeID")
    private String senderTypeID;
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("TicketID")
    private String ticketID;
    @JsonProperty("TimeUnit")
    private String timeUnit;
    @JsonProperty("To")
    private String to;
    @JsonProperty("Attachment")
    private List<Attachment> attachments;
}
