package com.github.bingoohuang.utils.mail;

import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.util.List;
import java.util.Properties;

/**
 * POP3 邮箱收取。
 * <p>
 * https://user-images.githubusercontent.com/1940588/47476178-c098f380-d851-11e8-8338-11c32d6fa6f2.png
 */
public class MailFetcher {
    private final Properties properties;
    private final String username;
    private final String password;
    private final MailMatcher matcher;

    public MailFetcher(Properties properties, MailMatcher matcher) {
        this.properties = properties;
        this.username = properties.getProperty("mail.pop3.username");
        this.password = properties.getProperty("mail.pop3.password");
        this.matcher = matcher;

        val portKey = "mail.pop3.port";
        if (!this.properties.containsKey(portKey)) this.properties.put(portKey, "110");
    }

    public MailFetcher(MailMatcher matcher) {
        this(createProp(), matcher);
    }

    private static Properties createProp() {
        val prop = new Properties();
        val env = MailConfig.getEnv();
        env.stringPropertyNames().forEach(k -> {
            if (k.startsWith("mail.pop3.")) prop.put(k, env.getProperty(k));
        });

        return prop;
    }

    @SneakyThrows
    public List<Pop3MailMessage> fetchMails() {
        val session = Session.getDefaultInstance(properties);

        @Cleanup val store = session.getStore("pop3");
        store.connect(username, password);
        @Cleanup val inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        return fetchInboxMessages(inbox);
    }

    @SneakyThrows
    private List<Pop3MailMessage> fetchInboxMessages(Folder inbox) {
        int end = matcher.messageEnd();
        if (end < 0) end = inbox.getMessageCount();

        val inboxMessages = inbox.getMessages(matcher.messageStart(), end);

        List<Pop3MailMessage> messages = Lists.newArrayList();
        for (val message : inboxMessages) {
            val subject = message.getSubject();
            if (!matcher.matchSubject(subject)) continue;

            val from = ((InternetAddress) message.getFrom()[0]).getAddress();
            if (!matcher.matchFrom(from)) continue;

            val sentDate = new DateTime(message.getSentDate());
            if (!matcher.matchSentDate(sentDate)) continue;

            val content = getTextFromMessage(message);
            if (!matcher.matchBodyContent(content)) continue;

            val attachments = parseAttachments(message);

            messages.add(Pop3MailMessage.builder()
                    .messageNumber(message.getMessageNumber())
                    .subject(subject)
                    .from(from)
                    .sentDate(sentDate)
                    .content(content)
                    .attachments(attachments)
                    .build());
        }

        return messages;
    }

    @SneakyThrows
    private List<Pop3MailMessageAttachment> parseAttachments(Message message) {
        List<Pop3MailMessageAttachment> attachments = Lists.newArrayList();

        if (message.getContent() instanceof Multipart) {
            val multi = (Multipart) message.getContent();
            for (int j = 0; j < multi.getCount(); j++) {
                val bodyPart = multi.getBodyPart(j);
                val partFileName = bodyPart.getFileName();
                if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                        || StringUtils.isBlank(partFileName)) {
                    continue; // dealing with attachments only
                }

                val fileName = MimeUtility.decodeText(partFileName);
                if (!matcher.matchAttachmentFileName(fileName)) continue;

                attachments.add(Pop3MailMessageAttachment.builder()
                        .fileName(fileName)
                        .inputStream(bodyPart.getInputStream())
                        .build());
            }
        }

        return attachments;
    }

    @SneakyThrows
    private String getTextFromMessage(Message message) {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMimeMultipart((MimeMultipart) message.getContent());
        }

        return "";
    }

    @SneakyThrows
    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) {
        val result = new StringBuilder();
        val count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            val p = mimeMultipart.getBodyPart(i);
            if (p.isMimeType("text/plain")) {
                result.append("\n").append(p.getContent());
                break; // without break same text appears twice in my tests
            } else if (p.isMimeType("text/html")) {
                val html = (String) p.getContent();
                result.append("\n").append(Jsoup.parse(html).text());
            } else if (p.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) p.getContent()));
            }
        }

        return result.toString();
    }
}
