package com.hjt.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "A1ExpressFilter", urlPatterns = "/*")
public class A1ExpressFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        /* * * * * * * * * * 解决跨域和编码问题 * * * * * * * * * */
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        /* 编码 */
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        /* 标头 */
        // 允许访问的方式
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // 解决跨域问题 设置响应头允许axios跨域访问
        // 允许的ip的可以访问- http://localhost:5173/ 为前端项目的地址
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        // 是否允许请求带有验证信息
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        // 设置超时时间
        resp.setHeader("Access-Control-Max-Age", "3600");
        // Authorization 是jwtToken
        resp.setHeader("Access-Control-Allow-Headers", "Authorization, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
        // 设置可以响应头可以暴露的值：Redirect用于重定向, Authorization更新token
        resp.setHeader("Access-Control-Expose-Headers", "Redirect, Authorization");
        chain.doFilter(request, response);
    }
}
