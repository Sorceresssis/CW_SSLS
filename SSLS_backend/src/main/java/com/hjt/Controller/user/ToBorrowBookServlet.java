package com.hjt.Controller.user;

import com.hjt.dao.BookBagDao;
import com.hjt.dao.BorrowDao;
import com.hjt.dao.FineDao;
import com.hjt.dao.impl.BookBagDaoImpl;
import com.hjt.dao.impl.BorrowDaoImpl;
import com.hjt.dao.impl.FineDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet(name = "ToBorrowBookServlet", value = "/user/ToBorrowBookServlet")
public class ToBorrowBookServlet extends HttpServlet {
    private final BorrowDao borrowDao = new BorrowDaoImpl();
    private final FineDao fineDao = new FineDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // 获取要借的书的数组;
        try {
            // 判断是否有罚款未结清
            if (fineDao.queryFineCountByUserId((int) request.getSession().getAttribute("userId")) > 0) {
                out.print(Result.error("请先缴纳罚款"));
                return;
            }
            //检查数据合法: 去重且书的数量大于0
            int[] bookIds = Arrays.stream(request.getParameterValues("bookIds"))
                    .distinct().mapToInt(Integer::parseInt).toArray();
            if (bookIds.length == 0) {
                out.print(Result.error("数量小于1").toJsonString());
                return;
            }
            int userId = (int) request.getSession().getAttribute("userId");
            // 删除bookBag中的书
            for (int bookId : bookIds) {
                BookBagDao bookBagDao = new BookBagDaoImpl();
                bookBagDao.removeBookFromBookBag(userId, bookId);
            }
            // 借书并返回结果
            out.print(Result.success(borrowDao.toBorrowBooks(userId, bookIds)));
        } catch (Exception e) {
            out.print(Result.error("传参错误"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
