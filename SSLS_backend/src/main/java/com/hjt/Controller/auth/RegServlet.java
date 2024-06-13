package com.hjt.Controller.auth;

import com.hjt.dao.UserLoginDao;
import com.hjt.dao.impl.UserLoginDaoImpl;
import com.hjt.dto.Result;
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

@WebServlet(name = "RegServlet", value = "/auth/RegServlet")
// 在 servlet 3.0+ 环境中工作，您还可以使用它的 multipart 支持轻松完成 multipart-data 解析工作。
// 只需在servlet类上添加一个@MultipartConfig，然后调用request.getParameter() 即可接收文本数据，非常简单。
@MultipartConfig
public class RegServlet extends HttpServlet {
    private final UserLoginDao userLoginDao = new UserLoginDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            // 验证验证码
            String captcha = RSAUtils.decrypt(request.getParameter("captcha"), RSAUtils.getPrivateKeyString()).toUpperCase();

            // 如果在这里报了空指针异常，有可能是前端在请求时没有带上cookies
            String captchaSession = ((String) request.getSession().getAttribute("captcha")).toUpperCase();
            // 移除验证码
            request.getSession().removeAttribute("captcha");

            if (!captcha.equals(captchaSession)) {
                out.print(Result.error("验证码错误").toJsonString());
                return;
            }

            // 准备好注册数据
            String username = RSAUtils.decrypt(request.getParameter("username"), RSAUtils.getPrivateKeyString());
            String salt = SaltedHashEncryptionUtils.generateSalt();
            String psd = SaltedHashEncryptionUtils.hashPassword(RSAUtils.decrypt(
                    request.getParameter("psd"), RSAUtils.getPrivateKeyString()), salt);
            String phone = RSAUtils.decrypt(request.getParameter("phone"), RSAUtils.getPrivateKeyString());
            String email = RSAUtils.decrypt(request.getParameter("email"), RSAUtils.getPrivateKeyString());


            // 执行注册操作
            if (userLoginDao.addUser(username, psd, salt, "".equals(phone) ? null : phone, "".equals(email) ? null : email)) {
                out.print(Result.success("注册成功"));
            } else {
                // 注册失败，查找原因
                if (userLoginDao.getUserLoginByUsername(username) != null) {
                    out.print(Result.error("用户名已存在"));
                } else if (userLoginDao.getUserLoginByUsername(phone) != null) {
                    out.print(Result.error("手机号已存在"));
                } else if (userLoginDao.getUserLoginByUsername(email) != null) {
                    out.print(Result.error("邮箱已存在"));
                } else {
                    out.print(Result.error("未知错误"));
                }
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
