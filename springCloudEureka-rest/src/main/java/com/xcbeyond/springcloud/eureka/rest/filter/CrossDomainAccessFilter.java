package com.xcbeyond.springcloud.eureka.rest.filter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域访问过滤器。</br>
 * 用于处理RESTFull接口跨域调用时，跨域访问的问题。
 * @Auther: xcbeyond
 * @Date: 2018/11/20 11:31
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/*",filterName = "CrossDomainAccessFilter")
public class CrossDomainAccessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(servletRequest, response);
    }

    @Override
    public void destroy() {

    }
}
