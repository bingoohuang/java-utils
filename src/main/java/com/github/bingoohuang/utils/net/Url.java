package com.github.bingoohuang.utils.net;

import com.github.bingoohuang.utils.codec.Bytes;
import lombok.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Url {

    @SneakyThrows
    public static String encode(String s) {
        return URLEncoder.encode(s, "UTF-8");

    }

    @SneakyThrows
    public static String decode(String s) {
        return URLDecoder.decode(s, "UTF-8");
    }

    /**
     * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
     * the 200-399 range.
     *
     * @param url     The HTTP URL to be pinged.
     * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
     *                the total timeout is effectively two times the given timeout.
     * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
     * given timeout, otherwise <code>false</code>.
     */
    @SneakyThrows
    public static boolean ping(String url, int timeout) {
        val httpUrl = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            val conn = (HttpURLConnection) new URL(httpUrl).openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestMethod("HEAD");
            int responseCode = conn.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }


    /**
     * 网络下载内容对象
     */
    @Value @ToString(exclude = "content")
    public static class DownloadContent {
        private final String fileName;
        private final byte[] content;
    }

    /**
     * 从指定链接下载资源。
     *
     * @param fileUrl 指定链接
     * @return 下载内容
     */
    @SneakyThrows
    public static DownloadContent download(String fileUrl) {
        val url = new URL(fileUrl);
        @Cleanup("disconnect") val httpConn = (HttpURLConnection) url.openConnection();
        val responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("No file to download. Server replied HTTP code: " + responseCode);
        }

        val fileName = parseFileName(fileUrl, httpConn);
        val content = Bytes.toByteArray(httpConn.getInputStream());
        return new DownloadContent(fileName, content);
    }

    /**
     * 解析网络下载的文件名。
     *
     * @param fileUrl  下载路径
     * @param httpConn HTTP连接
     * @return 解析后文件名
     */
    private static String parseFileName(String fileUrl, HttpURLConnection httpConn) {
        val disposition = httpConn.getHeaderField("Content-Disposition");
        if (disposition != null) {
            int index = disposition.indexOf("filename="); // extracts file name from header field
            if (index > 0) {
                val sub = disposition.substring(index + 9);
                return restoreUtf8FileName(sub);
            }
        }

        // extracts file name from URL
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * 将ISO编码的文件名重置为UTF-8编码的文件名。
     *
     * @param isoFileName ISO编码的文件名
     * @return UTF-8编码的文件名
     */
    private static String restoreUtf8FileName(String isoFileName) {
        val fileNameISOBytes = isoFileName.getBytes(StandardCharsets.ISO_8859_1);
        val fileNameUTF8 = new String(fileNameISOBytes, StandardCharsets.UTF_8);
        return isoFileName.length() == fileNameUTF8.length() ? isoFileName : fileNameUTF8;
    }
}
