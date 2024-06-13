package com.hjt.Controller.admin;

import com.hjt.dao.BookDao;
import com.hjt.dao.BorrowDao;
import com.hjt.dao.FineDao;
import com.hjt.dao.impl.BookDaoImpl;
import com.hjt.dao.impl.BorrowDaoImpl;
import com.hjt.dao.impl.FineDaoImpl;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ChartsServlet", value = "/admin/ChartsServlet")
public class ChartsServlet extends HttpServlet {
    private final BookDao bookDao = new BookDaoImpl();
    private final BorrowDao borrowDao = new BorrowDaoImpl();
    private final FineDao fineDao = new FineDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 分类统计 + 书籍状态统计
        List<Object> charts = new ArrayList<>();
        charts.add(bookDao.queryBookStatus());
        // 图书分类统计
        charts.add(bookDao.queryBookCountOfCategorys());
        // 借阅排行榜前十
        charts.add(borrowDao.borrowingRanking());
        // 最近12个月，每个月新增的罚款
        charts.add(fineDao.queryFineTotalByMonth());

        response.getWriter().print(Result.success(charts));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
