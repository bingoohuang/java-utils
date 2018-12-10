package com.github.bingoohuang.utils.mail;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value @Builder
public class MailMessage {
    @Singular private final List<String> tos;
    @Singular private final List<String> ccs;
    @Singular private final List<String> bccs;
    private final String subject;
    private final String content;
    @Singular private final List<MailAttachment> attachments;
}
