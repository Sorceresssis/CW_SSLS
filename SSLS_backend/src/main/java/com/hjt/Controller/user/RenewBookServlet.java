package com.hjt.Controller.user;

import com.hjt.dao.BorrowDao;
import com.hjt.dao.impl.BorrowDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RenewBookServlet", value = "/user/RenewBookServlet")
public class RenewBookServlet extends HttpServlet {
    private final BorrowDao borrowDao = new BorrowDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.getWriter().print(
                    borrowDao.renewBook(
                            Integer.parseInt(request.getParameter("borrowId"))
                    ) ? Result.success(null) : Result.error("续借失败！")
            );
        } catch (Exception e) {
            response.getWriter().print(Result.error("续借失败！"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
