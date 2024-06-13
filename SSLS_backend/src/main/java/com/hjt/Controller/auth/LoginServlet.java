package com.hjt.Controller.auth;

import com.hjt.dao.UserLoginDao;
import com.hjt.dao.impl.UserLoginDaoImpl;
import com.hjt.domain.UserLogin;
import com.hjt.dto.Result;
import com.hjt.utils.JwtUtils;
import com.hjt.utils.RSAUtils;
import com.hjt.utils.SaltedHashEncryptionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", value = "/auth/LoginServlet")
@MultipartConfig
public class LoginServlet extends HttpServlet {
    private final UserLoginDao userLoginDao = new UserLoginDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            // 验证验证码
            String captcha = RSAUtils.decrypt(request.getParameter("captcha"), RSAUtils.getPrivateKeyString()).toUpperCase();
            // 如果在这里报了空指针异常，那么就是前端在请求是没有带上cookies
            String captchaSession = ((String) request.getSession().getAttribute("captcha")).toUpperCase();
            // 移除验证码
            request.getSession().removeAttribute("captcha");

            if (!captcha.equals(captchaSession)) {
                // 移除验证码
                request.getSession().removeAttribute("captcha");
                out.print(Result.error("验证码错误").toJsonString());
                return;
            }
            // 从前端获取加密后的用户名和密码，解密后进行登录操作
            String username = RSAUtils.decrypt(request.getParameter("username"), RSAUtils.getPrivateKeyString());
            String psd = RSAUtils.decrypt(request.getParameter("psd"), RSAUtils.getPrivateKeyString());
            // 执行登陆操作
            UserLogin userLogin = this.userLoginDao.getUserLoginByUsername(username);
            // 判断用户名和密码是否正确
            if (userLogin != null && userLogin.getPsd().equals(SaltedHashEncryptionUtils.hashPassword(psd, userLogin.getSalt()))) {
                // 登录成功，把用户凭证存入claims转化成token发送给前端
                Map<String, Object> claims = new HashMap<>();
                claims.put("id", userLogin.getId());
                claims.put("username", userLogin.getUsername());
                claims.put("rule", userLogin.getRule());
                out.print(Result.success(JwtUtils.generateJwt(claims)).toJsonString());
                // 如果是管理员登录，那么就重定向到管理员页面
                if ("admin".equals(userLogin.getRule())) {
                    response.setHeader("Redirect", "/admin");
                }
            } else {
                // 登录失败返回错误信息
                out.print(Result.error("用户名或密码错误"));
            }
        } catch (Exception e) {
            out.print(Result.error("验证码失效"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
