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

@WebServlet(name = "RemoveFromBookBagServlet", value = "/user/RemoveFromBookBagServlet")
public class RemoveFromBookBagServlet extends HttpServlet {
    private final BookBagDao bookBagDao = new BookBagDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int affectedRows = bookBagDao.removeBookFromBookBag(
                    (int) request.getSession().getAttribute("userId"),
                    Integer.parseInt(request.getParameter("bookId"))
            );
            if (affectedRows == 0) {
                response.getWriter().print(Result.error("Remove book from book bag failed!"));
            } else {
                response.getWriter().print(Result.success(null));
            }
        } catch (Exception e) {
            response.getWriter().print(Result.error(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
