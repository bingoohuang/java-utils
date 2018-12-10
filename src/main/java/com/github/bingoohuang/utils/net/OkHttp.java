package com.github.bingoohuang.utils.net;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;

@Slf4j
public class OkHttp {
    public static final OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    public static String doGet(String url, String username, String password) {
        val request = createRequestBuilder(url, username, password);

        return execute(OkHttp.client, request);
    }

    public static Request.Builder createRequestBuilder(String url, String username, String password) {
        log.debug("url {}", url);

        val request = new Request.Builder().url(url);
        if (StringUtils.isNotEmpty(username)) {
            val credentials = Credentials.basic(username, password);
            request.header("Authorization", credentials);
        }

        return request;
    }

    @SneakyThrows
    public static String postJson(String url, String username, String password, String jsonBody) {
        val request = createRequestBuilder(url, username, password);

        log.debug("json body {}", jsonBody);

        request.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody));

        return execute(OkHttp.client, request);
    }

    public static String execute(OkHttpClient client, Request.Builder request) throws IOException {
        val response = client.newCall(request.build()).execute();
        val responseBody = response.body().string();

        log.debug("Response Body {}", responseBody);
        return responseBody;
    }


    @SneakyThrows
    public static String postJSON(String url, String json, Proxy proxy) {
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        val request = new Request.Builder().url(url).post(body).build();
        return getClient(proxy).newCall(request).execute().body().string();
    }

    @SneakyThrows
    public static String postForm(String url, Map<String, String> map, Proxy proxy) {
        val bodyBuilder = new FormBody.Builder();
        for (val e : map.entrySet()) {
            bodyBuilder.add(e.getKey(), e.getValue());
        }

        val request = new Request.Builder().url(url).post(bodyBuilder.build());
        return execute(getClient(proxy), request);
    }


    @SneakyThrows
    public static String encodedGet(String url, Map<String, String> encodedQueryParameters, Proxy proxy) {
        val urlBuilder = HttpUrl.get(url).newBuilder();
        encodedQueryParameters.forEach(urlBuilder::addEncodedQueryParameter);

        val request = new Request.Builder().url(urlBuilder.build()).get();
        return execute(getClient(proxy), request);
    }

    /**
     * 获得OkHttpClient实例。
     * proxy对象示例:
     * <code>
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.105", 8081);
     * </code>
     *
     * @param proxy 代理对象。
     * @return 定制化后的OkHttpClient对象。
     */
    @SneakyThrows
    public static OkHttpClient getClient(Proxy proxy) {
        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.html
        // You can customize a shared OkHttpClient instance with newBuilder().
        // This builds a CLIENT that shares the same connection pool, thread pools, and configuration.
        // Use the builder methods to configure the derived CLIENT for a specific purpose.
        return proxy != null ? client.newBuilder().proxy(proxy).build() : client;
    }
}
