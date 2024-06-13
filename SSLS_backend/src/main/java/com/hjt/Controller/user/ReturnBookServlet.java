package com.hjt.Controller.user;

import com.hjt.dao.BorrowDao;
import com.hjt.dao.FineDao;
import com.hjt.dao.impl.BorrowDaoImpl;
import com.hjt.dao.impl.FineDaoImpl;
import com.hjt.domain.Fine;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet(name = "ReturnBookServlet", value = "/user/ReturnBookServlet")
public class ReturnBookServlet extends HttpServlet {
    private final BorrowDao borrowDao = new BorrowDaoImpl();
    private final FineDao fineDao = new FineDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            // 判断是否还书成功, 有触发器会自动生成罚单。
            if (borrowDao.returnBook(Integer.parseInt(request.getParameter("borrowId")))) {
                // 查询是否有罚金
                Fine fine = fineDao.queryFineByBorrowId(
                        Integer.parseInt(request.getParameter("borrowId")));
                // 没有罚金就返回0.00
                out.print(Result.success(
                        fine != null ? fine.getAmount() : BigDecimal.valueOf(0.00)));
            } else {
                out.print(Result.error("还书失败"));
            }
        } catch (Exception e) {
            out.print(Result.error("还书失败"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
