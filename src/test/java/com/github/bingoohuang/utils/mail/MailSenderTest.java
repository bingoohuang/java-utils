package com.github.bingoohuang.utils.mail;

import lombok.val;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

public class MailSenderTest {
    @Test @Ignore
    public void sendText0() {
        val prop = new Properties();
        prop.put("mail.smtp.host", "smtp.ym.163.com");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.username", "abcd@abcd.com");
        prop.put("mail.smtp.password", "abcd");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.connectiontimeout", "10000");
        prop.put("mail.smtp.timeout", "10000");
        // 更多属性设置请参见 https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html


        new MailSender(prop).send(MailMessage.builder()
                .to("bingoohuang@dingtalk.com")
                .subject("黄进兵测试")
                .content("你好，黄进兵。我就是小测试一下")
                .build());
    }

    @Test @Ignore
    public void sendText() {
        new MailSender().send(MailMessage.builder()
                .to("bingoohuang@dingtalk.com")
                .subject("黄进兵测试")
                .content("你好，黄进兵。我就是小测试一下")
                .build());
    }

    @Test @Ignore
    public void sendHtml() {
        new MailSender().send(MailMessage.builder()
                .to("bingoohuang@dingtalk.com")
                .subject("黄进兵测试HTML2018-10-25 19:45:16")
                .content("<!doctype html>" +
                        "<html>" +
                        "<head>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head>" +
                        "<body><h3>你好，黄进兵。我哈哈哈哈</h3></body>" +
                        "</html>")
                .build());
    }

    @Test @Ignore
    public void sendAttachments() {
        new MailSender().send(MailMessage.builder()
                .to("huangjb@raiyee.com")
                .cc("bingoohuang@dingtalk.com")
                .subject("黄进兵测试附件")
                .content("<!doctype html>" +
                        "<html>" +
                        "<head>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head>" +
                        "<body><h3>你好，详情见附件</h3></body>" +
                        "</html>")
                .build());
    }
}

