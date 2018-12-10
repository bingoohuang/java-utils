package com.github.bingoohuang.utils.net;

import com.github.bingoohuang.utils.lang.Arr;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Cookies {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String domain;
    private String name;

    public Cookies(HttpServletRequest request, HttpServletResponse response, String domain, String name) {
        this.request = request;
        this.response = response;
        this.domain = domain;
        this.name = name;
    }

    public Cookies(HttpServletResponse response, String domain, String name) {
        this(null, response, domain, name);
    }

    public Cookies(HttpServletResponse response, String name) {
        this(null, response, null, name);
    }

    public Cookies(HttpServletRequest request, String name) {
        this(request, null, null, name);
    }


    public String getCookie() {
        if (request.getCookies() == null) return "";

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public Cookie addCookie(String value) {
        return addCookie(value, -1, HttpOnly.OFF, Secure4Https.OFF);
    }

    public Cookie addCookie(String value, int expire, TimeUnit timeUnit) {
        return addCookie(value, (int) timeUnit.toSeconds(expire), HttpOnly.OFF, Secure4Https.OFF);
    }


    public enum HttpOnly {ON, OFF}

    public enum Secure4Https {ON, OFF}

    public Cookie addCookie(String value, int maxSeconds,
                            HttpOnly httpOnly, Secure4Https secure4Https) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxSeconds);
        cookie.setHttpOnly(httpOnly == HttpOnly.ON); // allowed for javascripts or not
        cookie.setSecure(secure4Https == Secure4Https.ON); // for https only
        if (isNotEmpty(domain)) cookie.setDomain(domain);

        response.addCookie(cookie);
        return cookie;
    }

    public void delCookie() {
        addCookie("", 0, HttpOnly.OFF, Secure4Https.OFF);
    }


    /**
     * 查找cookie.
     *
     * @param r          HTTP请求
     * @param cookieName COOKIE名字
     * @return 可能找不到的可选值
     */
    public static Optional<Cookie> findCookie(HttpServletRequest r, String cookieName) {
        val cookies = r.getCookies();
        if (cookies == null) return Optional.empty();

        return Arr.find(cookies, cookieName, Cookie::getName);
    }

    /**
     * 清除cookie。
     *
     * @param r          HTTP响应
     * @param cookieName COOKIE名字
     * @param domain     域
     */
    public static void clearCookie(HttpServletResponse r, String cookieName, String domain) {
        val c = new Cookie(cookieName, null);
        c.setPath("/");
        if (StringUtils.isNotEmpty(domain)) {
            c.setDomain(domain);
        }
        c.setMaxAge(0); // Don't set to -1 or it will become a session cookie!
        r.addCookie(c);
    }

    public static void clearCookies(HttpServletResponse r, String... cookieNames) {
        for (val cookieName : cookieNames) {
            clearCookie(r, cookieName, null);
        }
    }
}
