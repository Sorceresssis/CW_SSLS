package com.hjt.Controller.user;

import com.hjt.dao.BookBagDao;
import com.hjt.dao.impl.BookBagDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "BookBagServlet", value = "/user/BookBagServlet")
public class BookBagServlet extends HttpServlet {
    private final BookBagDao bookBagDao = new BookBagDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print(Result.success(
                bookBagDao.getBookBagByUserId(
                        (int) request.getSession().getAttribute("userId"))
        ));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
