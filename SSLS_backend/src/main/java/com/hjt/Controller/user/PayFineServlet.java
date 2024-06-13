package com.hjt.Controller.user;

import com.hjt.dao.FineDao;
import com.hjt.dao.impl.FineDaoImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "PayFineServlet", value = "/user/PayFineServlet")
public class PayFineServlet extends HttpServlet {
    private final FineDao fineDao = new FineDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print(
                fineDao.payFineByFineId(Integer.parseInt(request.getParameter("fineId"))));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
