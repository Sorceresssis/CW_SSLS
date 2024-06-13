package com.hjt.Controller.user;

import com.hjt.dao.UserInfoDao;
import com.hjt.dao.impl.UserInfoDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "InfoServlet", value = "/user/InfoServlet")
public class InfoServlet extends HttpServlet {
    UserInfoDao userInfoDao = new UserInfoDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 从token中获取用户凭证
        try {
            response.getWriter().print(Result.success(
                    userInfoDao.queryUserInfoById(
                            (int) request.getSession().getAttribute("userId"))
            ));
        } catch (Exception e) {
            // token_error
            response.getWriter().print(Result.error("NOT_LOGIN"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
