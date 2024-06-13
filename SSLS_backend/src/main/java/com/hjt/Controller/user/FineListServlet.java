package com.hjt.Controller.user;

import com.hjt.dao.FineDao;
import com.hjt.dao.impl.FineDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "FineListServlet", value = "/user/FineListServlet")
public class FineListServlet extends HttpServlet {
    private final FineDao fineDao = new FineDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print(Result.success(
                fineDao.queryFineByUserId(
                        (int) request.getSession().getAttribute("userId")
                )
        ).toJsonString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
