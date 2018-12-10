package com.github.bingoohuang.utils.net;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;

public class BytesImageMultipartFile implements MultipartFile {
    private final byte[] imgContent;

    public BytesImageMultipartFile(byte[] imgContent) {
        this.imgContent = imgContent;
    }

    @Override
    public String getName() {
        return "uploadImage";
    }

    // 此方法返回的不是真实的文件名，但是文件后缀名为正确的
    @SneakyThrows
    @Override
    public String getOriginalFilename() {
        @Cleanup val is = new BufferedInputStream(new ByteArrayInputStream(imgContent));
        String contentType = URLConnection.guessContentTypeFromStream(is);
        if (contentType.startsWith("image/")) {
            return "filename." + contentType.substring("image/".length());
        }

        throw new RuntimeException("unknown image type " + contentType);
    }

    @Override
    public String getContentType() {
        return "utf-8";
    }

    @Override
    public boolean isEmpty() {
        return imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(dest)) {
            fileOutputStream.write(imgContent);
        }
    }

}
