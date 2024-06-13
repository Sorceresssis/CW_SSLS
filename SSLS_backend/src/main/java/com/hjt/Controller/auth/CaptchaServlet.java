package com.hjt.Controller.auth;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.hjt.utils.PropertiesReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;


@WebServlet(name = "CaptchaServlet", value = "/auth/CaptchaServlet")
public class CaptchaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/png");
        // 禁止图像缓存
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 创建Kaptcha配置对象
        Properties properties = PropertiesReader.loadProperties("kaptcha.properties");
        Config config = new Config(properties);

        // 创建Kaptcha实例
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);

        // 生成验证码字符串
        String captchaText = kaptcha.createText();
        request.getSession().setAttribute("captcha", captchaText);

        // 创建验证码图片
        BufferedImage captchaImage = kaptcha.createImage(captchaText);

        ImageIO.write(captchaImage, "png", response.getOutputStream());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
