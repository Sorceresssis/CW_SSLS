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
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet(name = "RechargeMoneyServlet", value = "/user/RechargeMoneyServlet")
public class RechargeMoneyServlet extends HttpServlet {
    private final UserInfoDao userInfoDao = new UserInfoDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // 充值金额
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        if (userInfoDao.increaseBalance(
                (int) request.getSession().getAttribute("userId"), amount)) {
            response.getWriter().print(Result.success(
                    // 返回充值后的余额
                    userInfoDao.queryUserInfoById(
                            (int) request.getSession().getAttribute("userId")).getBalance()
            ));
        } else {
            out.print(Result.error("充值失败"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
