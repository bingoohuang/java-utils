package com.github.bingoohuang.utils.mail;

import lombok.Builder;
import lombok.Value;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

@Value @Builder
public class Pop3MailMessageAttachment implements Closeable {
    private final String fileName;
    private final InputStream inputStream;

    @Override public void close() throws IOException {
        inputStream.close();
    }
}
