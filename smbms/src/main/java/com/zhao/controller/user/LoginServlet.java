package com.zhao.controller.user;

import com.zhao.pojo.User;
import com.zhao.service.UserService;
import com.zhao.service.impl.UserServiceImpl;
import com.zhao.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Time : 2022/8/5 16:13
 * @Author : 赵浩栋
 * @File : LoginServlet.java
 * @Software: IntelliJ IDEA
 */
public class LoginServlet extends HttpServlet {
    //调用业务层代码
    private UserService service;

    public LoginServlet(){
        service=new UserServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取用户名密码
        String userCode = req.getParameter("userCode");
        String password= req.getParameter("userPassword");

        //和数据库密码进行比对，调用service
        User user = service.login(userCode, password);

        if (user!=null){
            //将信息保存到session，并跳转
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            resp.sendRedirect("jsp/frame.jsp");
        } else {
            //登陆错误
            req.setAttribute("error","用户名或密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       this.doGet(req, resp);
    }
}
