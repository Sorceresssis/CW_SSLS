package com.hjt.Controller;

import com.hjt.dao.BookDao;
import com.hjt.dao.impl.BookDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AutoCompleteBookNameServlet", value = "/AutoCompleteBookNameServlet")
public class AutoCompleteBookNameServlet extends HttpServlet {
    private final BookDao bookDao = new BookDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyWord = request.getParameter("keyWord");
        response.getWriter().print(
                Result.success(
                        bookDao.queryBookNamesByKeyWord(keyWord, 0, 20)));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}