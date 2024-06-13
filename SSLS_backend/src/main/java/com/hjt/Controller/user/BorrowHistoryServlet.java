package com.hjt.Controller.user;

import com.hjt.dao.BorrowDao;
import com.hjt.dao.impl.BorrowDaoImpl;
import com.hjt.dto.BorrowHistoryPageDTO;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "BorrowHistoryServlet", value = "/user/BorrowHistoryServlet")
public class BorrowHistoryServlet extends HttpServlet {
    private final BorrowDao borrowDao = new BorrowDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int pageNo = Integer.parseInt(request.getParameter("pageNo"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));
            
            // 返回的数据
            BorrowHistoryPageDTO borrowHistoryPageDTO = new BorrowHistoryPageDTO();
            // 记录总数
            borrowHistoryPageDTO.setTotal(
                    borrowDao.getReturnedBooksCountByUserId(
                            (int) request.getSession().getAttribute("userId")));
            // 当前页码的数据
            borrowHistoryPageDTO.setBorrowHistoryInfos(
                    borrowDao.getReturnedBooksByUserId(
                            (int) request.getSession().getAttribute("userId"),
                            (pageNo - 1) * pageSize,
                            pageSize
                    )
            );

            response.getWriter().print(Result.success(borrowHistoryPageDTO));

        } catch (NumberFormatException e) {
            response.getWriter().print(Result.error("参数错误"));
        } catch (Exception e) {
            response.getWriter().print(Result.error("服务器错误"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
