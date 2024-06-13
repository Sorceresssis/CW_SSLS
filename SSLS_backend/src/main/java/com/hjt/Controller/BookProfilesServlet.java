package com.hjt.Controller;

import com.hjt.dao.BookDao;
import com.hjt.dao.impl.BookDaoImpl;
import com.hjt.dto.BookPageDTO;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "BookProfilesServlet", value = "/BookProfilesServlet")
public class BookProfilesServlet extends HttpServlet {
    private final BookDao bookDao = new BookDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获得分类信息
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        // 获取分页信息
        int pageNo = Integer.parseInt(request.getParameter("pageNo"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        // 把数据放到DTO中，total是总数，bookProfiles是当前页的书籍信息
        BookPageDTO bookPageDTO = new BookPageDTO();
        if (categoryId == 0) {
            bookPageDTO.setTotal(bookDao.queryBookCount());
            bookPageDTO.setBookProfiles(bookDao.queryBookProfiles((pageNo - 1) * pageSize, pageSize));
        } else {
            bookPageDTO.setTotal(bookDao.queryBookCountByCategoryId(categoryId));
            bookPageDTO.setBookProfiles(bookDao.queryBookProfilesByCategoryId(
                    categoryId, (pageNo - 1) * pageSize, pageSize));
        }
        response.getWriter().print(Result.success(bookPageDTO));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
