package com.github.bingoohuang.utils.mail;

import lombok.Builder;
import lombok.Value;
import org.joda.time.DateTime;

import java.util.List;

@Value @Builder
public class Pop3MailMessage {
    private final int messageNumber;
    private final String subject;
    private final String from;
    private final DateTime sentDate;
    private final String content;
    private final List<Pop3MailMessageAttachment> attachments;
}
