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

@WebServlet(name = "AddToBookBagServlet", value = "/user/AddToBookBagServlet")
public class AddToBookBagServlet extends HttpServlet {
    private final BookBagDao bookBagDao = new BookBagDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int affectedRow = bookBagDao.addBookToBookBag(
                (int) request.getSession().getAttribute("userId"),
                Integer.parseInt(request.getParameter("bookId"))
        );
        if (affectedRow == 0)
            response.getWriter().print(Result.error("添加失败").toJsonString());
        else
            response.getWriter().print(Result.success(null).toJsonString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
