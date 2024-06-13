package com.hjt.Controller.user;

import com.hjt.dao.UserLoginDao;
import com.hjt.dao.impl.UserLoginDaoImpl;
import com.hjt.domain.UserLogin;
import com.hjt.dto.Result;
import com.hjt.utils.RSAUtils;
import com.hjt.utils.SaltedHashEncryptionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "EditPsdServlet", value = "/user/EditPsdServlet")
public class EditPsdServlet extends HttpServlet {
    private final UserLoginDao userLoginDao = new UserLoginDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            // 提取并解密旧密码和新密码
            String oldPsd = RSAUtils.decrypt(request.getParameter("oldPsd"), RSAUtils.getPrivateKeyString());
            String newPsd = RSAUtils.decrypt(request.getParameter("newPsd"), RSAUtils.getPrivateKeyString());
            UserLogin userLogin = userLoginDao.getUserLoginByUsername(
                    (String) request.getSession().getAttribute("username"));
            // 判断旧密码是否正确
            if (!userLogin.getPsd().equals(
                    SaltedHashEncryptionUtils.hashPassword(oldPsd, userLogin.getSalt()))) {
                out.print(Result.error("密码错误"));
                return;
            }
            // 判断新密码是否与旧密码相同
            if (oldPsd.equals(newPsd)) {
                out.print(Result.error("新密码与旧密码相同"));
                return;
            }
            String salt = SaltedHashEncryptionUtils.generateSalt();
            response.getWriter().print(
                    // 修改密码
                    userLoginDao.editPsd((int) request.getSession().getAttribute("userId"),
                            SaltedHashEncryptionUtils.hashPassword(newPsd, salt), salt) ?
                            Result.success("修改成功") : Result.error("修改失败")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
