package com.github.bingoohuang.utils.net;

import com.github.bingoohuang.utils.lang.Closer;
import com.google.common.base.Throwables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Http {
    public static void respondJSON(HttpServletResponse rsp, String json) {
        responseContent(rsp, json, "application/json", "UTF-8");
    }

    public static void responseText(HttpServletResponse rsp, String text) {
        responseContent(rsp, text, "text/plain", "UTF-8");
    }

    public static void responseContent(HttpServletResponse rsp, String content,
                                       String contentType, String characterEncoding) {
        if (content == null) return;

        try {
            rsp.setHeader("Content-Type", contentType + "; charset=" + characterEncoding);
            rsp.setCharacterEncoding(characterEncoding);
            PrintWriter writer = rsp.getWriter();
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> dealReqParams(Map<String, String[]> requestParams) {
        Map<String, String> params = new HashMap();
        try {
            for (String key : requestParams.keySet()) {
                String[] values = requestParams.get(key);

                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), "gbk");
                params.put(key, valueStr);
            }
            return params;
        } catch (Exception e) {
            throw new RuntimeException("参数处理出错", e);
        }
    }

    public static String dealRequestBody(HttpServletRequest req, String charsetName) {
        DataInputStream dis = null;
        try {
            int formDataLength = req.getContentLength();
            dis = new DataInputStream(req.getInputStream());
            byte buff[] = new byte[formDataLength];
            int totalBytes = 0;
            while (totalBytes < formDataLength) {
                int bytes = dis.read(buff, totalBytes, formDataLength);
                totalBytes += bytes;
            }
            return new String(buff, charsetName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closer.closeQuietly(dis);
        }
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }


    public static void error(HttpServletResponse response, int statusCode, Throwable ex) {
        error(response, statusCode, Throwables.getStackTraceAsString(ex));
    }

    public static void error(HttpServletResponse response, int statusCode, String message) {
        response.setStatus(statusCode);
        responseText(response, message);
    }
}
