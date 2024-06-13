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

@WebServlet(name = "BorrowListServlet", value = "/user/BorrowListServlet")
public class BorrowListServlet extends HttpServlet {
    private final BorrowDao borrowDao = new BorrowDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print(Result.success(
                borrowDao.getBorrowedBooksByUserId(
                        (int) request.getSession().getAttribute("userId")
                )));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
