package com.hjt.filter;

import com.hjt.dto.Result;
import com.hjt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(filterName = "D1_UserFilter", urlPatterns = {"/user/*", "/admin/*"})
public class D1_UserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        /* * * * * * * * * * 筛选用户 * * * * * * * * * */
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            // 解析jwtToken
            Claims claims = JwtUtils.parseJwt(req.getHeader("Authorization"));
            // 用户id,用户名,权限信息。写入session
            HttpSession session = req.getSession();
            session.setAttribute("userId", claims.get("id"));
            session.setAttribute("username", claims.get("username"));
            // 如果是去/admin/*，则判断权限

            if (req.getRequestURI().contains("/admin/")) {
                if ("admin".equals(claims.get("rule"))) {
                    chain.doFilter(request, response);
                } else {
                    // 没有权限重定向到首页
                    resp.setHeader("Redirect", "/");
                }
                return;
            }
            // 普通用户直接放行
            chain.doFilter(request, response);
        } catch (Exception e) {
            /*
             *  踩坑：filter 的catch能够捕获的到chain.doFilter放行到servlet，servlet产生的的异常，
             *  用来捕获jwt解析异常却捕获到了servlet中的异常，导致进行了错误的异常处理发送了重定向要求，
             *  所以在servlet中要自己处理异常，不能偷懒。
             */
            // jwtToken解析失败说明token过期或遭到了修改。重定向到登录页面
            String uri = req.getRequestURI();
            // 前端在加载首页时，会自动发送身份验证的请求，这里不需要重定向，否则会出现死循环
            if (!"/SSLS/user/InfoServlet".equals(uri)) {
                resp.setHeader("Redirect", "/auth");
            }
            resp.getWriter().print(Result.error("NOT_LOGIN").toJsonString());
        }
    }
}