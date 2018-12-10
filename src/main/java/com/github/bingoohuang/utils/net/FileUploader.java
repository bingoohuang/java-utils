package com.github.bingoohuang.utils.net;


import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FileUploader {
    String boundary;
    HttpURLConnection httpConn;
    OutputStream out;

    public FileUploader(String requestURL) {
        try {
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();

            boundary = "---" + System.currentTimeMillis() + "---"; // 定义数据分隔线

            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            out = new DataOutputStream(httpConn.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    @SneakyThrows
    public void addHeader(String name, String value) {
        out.write((name + ": " + value + "\r\n").getBytes());
    }

    @SneakyThrows
    public void addFilePart(File file) {
        addFilePart(file.getName(), new FileInputStream(file));
    }

    public void addFilePart(String fileName, InputStream inputStream) {
        String preparePart = preparePart(fileName);
        try {
            out.write(preparePart.getBytes());

            copyStream(inputStream);

            out.write("\r\n".getBytes()); //多个文件时，二个文件之间加入这个
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String preparePart(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary + "\r\n");

        sb.append("Content-Disposition: form-data;name=\"" + fileName + "\";filename=\"" + fileName + "\"\r\n");
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        //sb.append("Content-Type:application/octet-stream\r\n\r\n");
        sb.append("Content-Type:" + contentType + "\r\n\r\n");
        return sb.toString();
    }

    @SneakyThrows
    private void copyStream(InputStream is) {
        DataInputStream in = new DataInputStream(is);

        try {
            int bytes;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
        } finally {
            in.close();
            is.close();
        }
    }

    @SneakyThrows
    public String finish() {
        byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes();// 定义最后数据分隔线
        int status = 0;
        out.write(end_data);
        out.flush();
        out.close();

        status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            String response = readInputStreamToString(inputStream);
            httpConn.disconnect();

            return response;
        }

        throw new RuntimeException("Server returned non-OK status: " + status);
    }

    @SneakyThrows
    private String readInputStreamToString(InputStream inputStream) {
        StringBuilder response = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } finally {
            if (reader != null) reader.close();
        }

        return response.toString();
    }

}