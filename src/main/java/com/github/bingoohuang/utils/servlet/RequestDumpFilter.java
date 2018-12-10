package com.github.bingoohuang.utils.servlet;

import javax.servlet.*;
import java.io.IOException;

public class RequestDumpFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(RequestDumper.wrap(request), response);
    }

    @Override public void destroy() {

    }
}
