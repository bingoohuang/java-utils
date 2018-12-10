package com.github.bingoohuang.utils.mail;

import org.joda.time.DateTime;

public interface MailMatcher {
    /**
     * 匹配邮件主题。
     *
     * @param subject 邮件主题。
     * @return true 匹配 false 不匹配
     */
    default boolean matchSubject(String subject) {
        return true;
    }

    /**
     * 匹配发件人。
     *
     * @param from 发件人邮箱。
     * @return true 匹配 false 不匹配
     */
    default boolean matchFrom(String from) {
        return true;
    }

    /**
     * 匹配发送时间。
     *
     * @param sentDateTime 发送时间
     * @return true 匹配 false 不匹配
     */
    default boolean matchSentDate(DateTime sentDateTime) {
        return true;
    }

    /**
     * 匹配邮件内容。
     *
     * @param content 邮件内容。
     * @return true 匹配 false 不匹配
     */
    default boolean matchBodyContent(String content) {
        return true;
    }

    /**
     * 匹配附件文件名。
     *
     * @param fileName 附件文件名
     * @return true 匹配 false 不匹配
     */
    default boolean matchAttachmentFileName(String fileName) {
        return true;
    }

    /**
     * 指定消息开始索引（1-based）。
     *
     * @return 开始索引
     */
    default int messageStart() {
        return 1;
    }

    /**
     * 指定消息结束索引（included)(1-based)。
     * -1表示
     *
     * @return 结束索引
     */
    default int messageEnd() {
        return -1;
    }


}
