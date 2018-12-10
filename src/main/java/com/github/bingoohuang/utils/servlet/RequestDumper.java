package com.github.bingoohuang.utils.servlet;

import com.github.bingoohuang.utils.codec.Bytes;
import com.google.common.base.Charsets;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class RequestDumper {
    @SneakyThrows
    public static ServletRequest wrap(ServletRequest request) {
        val req = new BufferedRequestWrapper((HttpServletRequest) request);
        req.setAttribute("_log_req", req);
        return req;
    }

    private static final String INDENT = "\t";
    private static final String LF = "\n";

    public static String dumpRequest(HttpServletRequest request) {
        val req = (BufferedRequestWrapper) request.getAttribute("_log_req");
        val sb = new StringBuilder(LF);
        sb.append(INDENT).append("[RemoteAddr] ").append(req.getRemoteAddr());
        if (!StringUtils.equals(req.getRemoteAddr(), req.getRemoteHost())) sb.append(" ").append(req.getRemoteHost());
        sb.append(LF);

        sb.append(INDENT).append("[URL] ").append(req.getMethod()).append(" ").append(getURL(req)).append(LF);

        dumpRequestHeader(sb, req);
        dumpRequestParameter(sb, req);

        String requestBody = req.getRequestBody();
        if (StringUtils.isNotEmpty(requestBody)) sb.append(INDENT).append("[RequestBody] ").append(requestBody);

        return sb.toString();
    }


    public static String getURL(HttpServletRequest req) {
        val scheme = req.getScheme();             // http
        val serverName = req.getServerName();     // hostname.com
        val serverPort = req.getServerPort();     // 80
        val contextPath = req.getContextPath();   // /mywebapp
        val servletPath = req.getServletPath();   // /servlet/MyServlet
        val pathInfo = req.getPathInfo();         // /a/b;c=123
        val queryString = req.getQueryString();   // d=789

        // Reconstruct original requesting URL
        val url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath).append(servletPath);

        if (pathInfo != null) url.append(pathInfo);
        if (queryString != null) url.append("?").append(queryString);
        return url.toString();
    }

    public static void dumpRequestHeader(StringBuilder sb, HttpServletRequest request) {
        val hNames = request.getHeaderNames();
        while (hNames.hasMoreElements()) {
            String name = hNames.nextElement();
            sb.append(INDENT).append("[header] ").append(name).append("=").append(request.getHeader(name)).append(LF);
        }
    }

    public static void dumpRequestParameter(StringBuilder sb, HttpServletRequest request) {
        val pNames = request.getParameterNames();
        while (pNames.hasMoreElements()) {
            String name = pNames.nextElement();
            sb.append(INDENT).append("[param] ").append(name).append("=").append(request.getParameter(name)).append(LF);
        }
    }


    public static class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private static final class BufferedServletInputStream extends ServletInputStream {
            private final ServletInputStream inputStream;
            private ByteArrayInputStream bais;

            public BufferedServletInputStream(ByteArrayInputStream bais, ServletInputStream inputStream) {
                this.bais = bais;
                this.inputStream = inputStream;
            }

            @Override
            public int available() {
                return this.bais.available();
            }

            @Override
            public int read() {
                return this.bais.read();
            }

            @Override
            public int read(byte[] buf, int off, int len) {
                return this.bais.read(buf, off, len);
            }

            @Override
            public boolean isFinished() {
                return inputStream.isFinished();
            }

            @Override
            public boolean isReady() {
                return inputStream.isReady();
            }

            @Override
            public void setReadListener(ReadListener listener) {
                inputStream.setReadListener(listener);
            }
        }

        private byte[] mBodyBuffer;

        public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);

            val in = request.getInputStream();
            mBodyBuffer = Bytes.toByteArray(in);
        }

        public String getRequestBody() {
            return new String(mBodyBuffer, Charsets.UTF_8);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            val in = new ByteArrayInputStream(mBodyBuffer);
            return new BufferedServletInputStream(in, super.getInputStream());
        }
    }
}
