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

@WebServlet(name = "BookProfilesSearchServlet", value = "/BookProfilesSearchServlet")
public class BookProfilesSearchServlet extends HttpServlet {
    private final BookDao bookDao = new BookDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyWord = request.getParameter("keyWord");
        int pageNo = Integer.parseInt(request.getParameter("pageNo"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        BookPageDTO bookPageDTO = new BookPageDTO();
        bookPageDTO.setTotal(bookDao.queryBookCountByKeyWord(keyWord));
        bookPageDTO.setBookProfiles(
                bookDao.queryBookProfilesByKeyWord(
                        keyWord, (pageNo - 1) * pageSize, pageSize));
        response.getWriter().print(Result.success(bookPageDTO));
        // 冒泡

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
