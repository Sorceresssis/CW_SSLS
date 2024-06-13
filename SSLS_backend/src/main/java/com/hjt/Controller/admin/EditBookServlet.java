package com.hjt.Controller.admin;

import com.hjt.dao.BookDao;
import com.hjt.dao.impl.BookDaoImpl;
import com.hjt.domain.Book;
import com.hjt.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet(name = "EditBookServlet", value = "/admin/EditBookServlet")
@MultipartConfig
public class EditBookServlet extends HttpServlet {
    private final BookDao bookDao = new BookDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            // 获得书的id
            int bookId = Integer.parseInt(request.getParameter("bookId"));

            // 接受参数
            String name = request.getParameter("name");
            String authors = request.getParameter("authors");
            String press = request.getParameter("press");
            String coverImage = handleImage(request.getPart("coverImage"));
            String description = request.getParameter("description");
            String publishDate = request.getParameter("publishDate");
            String status = request.getParameter("status");

            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            // 构造book对象
            Book book = new Book(name, authors, press, coverImage, description, publishDate, status, categoryId);

            // 如果bookId为0，表示是添加书籍，否则是修改书籍
            if (bookId == 0) {
                out.print(bookDao.addBook(book) ? new Result(1, "添加成功", null) : Result.error("添加失败"));
            } else {
                out.print(bookDao.editBook(bookId, book) ? new Result(1, "修改成功", null) : Result.error("修改失败"));
            }
        } catch (Exception e) {
            out.print(Result.error("参数错误"));
        }
    }

    /**
     * 处理上传的图片，保存到本地，返回保存的图片路径
     *
     * @param imagePart 上传的图片文件
     * @return 保存的图片路径，发送错误返回null
     */
    private String handleImage(Part imagePart) {
        try {
            // 没有图片上传
            if (imagePart.getSize() != 0) {
                String fileName = imagePart.getSubmittedFileName();
                String ext = fileName.substring(fileName.lastIndexOf('.'));
                // 通过扩展名判断上传的是不是图片
                if (".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext)) {
                    String uuid = UUID.randomUUID().toString();
                    imagePart.write(getServletContext().getRealPath("/bookImg") + "/" + uuid + ext);
                    return uuid + ext;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
