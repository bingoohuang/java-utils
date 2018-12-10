package com.github.bingoohuang.utils.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Email {
    private final String emailHost;
    private final String emailUserName;
    private final String emailPassword;
    private final String emailSendFrom;
    private final boolean exceptionSilent;

    Logger log = LoggerFactory.getLogger(Email.class);

    public Email(String emailHost, String emailUserName, String emailPassword, String emailSendFrom, boolean exceptionSilent) {
        this.emailHost = emailHost;
        this.emailUserName = emailUserName;
        this.emailPassword = emailPassword;
        this.emailSendFrom = emailSendFrom;
        this.exceptionSilent = exceptionSilent;
    }


    public void emailTo(String to, String subject, String htmlContent) {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost(emailHost);
        senderImpl.setUsername(emailUserName);
        senderImpl.setPassword(emailPassword);

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put("mail.smtp.timeout", "25000");

        senderImpl.setJavaMailProperties(prop);

        try {
            MimeMessage mimeMessage = createMimeMessage(senderImpl, to, subject, htmlContent);
            senderImpl.send(mimeMessage);
        } catch (Exception e) {
            if (!exceptionSilent) throw new RuntimeException(e);
            log.error("email to {} with subject {} exception", to, subject, e);
        }
    }

    private MimeMessage createMimeMessage(JavaMailSenderImpl senderImpl,
                                          String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mailMessage = senderImpl.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");
        messageHelper.setFrom(emailSendFrom);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        //true 表示启动HTML格式的邮件
        messageHelper.setText(htmlContent, true);
        return mailMessage;

    }

}
