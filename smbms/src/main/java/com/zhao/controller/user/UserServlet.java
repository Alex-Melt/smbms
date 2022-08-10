package com.zhao.controller.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.zhao.pojo.Role;
import com.zhao.pojo.User;
import com.zhao.service.UserService;
import com.zhao.service.impl.RoleServiceImpl;
import com.zhao.service.impl.UserServiceImpl;
import com.zhao.utils.Constants;
import com.zhao.utils.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Time : 2022/8/5 19:23
 * @Author : 赵浩栋
 * @File : UserServlet.java
 * @Software: IntelliJ IDEA
 */

//实现Servlet复用，实现复用需要提取出方法，然后在doGet函数中调用即可
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && method.equals("savepwd")) {
            this.updatePwd(req, resp);
        } else if (method != null && method.equals("pwdmodify")) {
            this.modifyPwd(req, resp);
        } else if (method != null && method.equals("query")) {
            this.query(req, resp);
        } else if (method != null && method.equals("add")) {
            this.add(req, resp);
        } else if (method != null && method.equals("getrolelist")) {
            this.getRoleList(req, resp);
        } else if (method != null && method.equals("ucexist")) {
            this.ucexist(req, resp);
        } else if (method != null && method.equals("modifyexe")) {
            this.modifyexe(req, resp);
        } else if (method != null && method.equals("deluser")) {
            this.deluser(req, resp);
        } else if (method != null && method.equals("view")) {
            this.getUserById(req, resp, "userview.jsp");
        } else if (method != null && method.equals("modify")) {
            this.getUserById(req, resp, "usermodify.jsp");
        }

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    //修改密码
    private void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        //从session拿数据
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //获取新密码
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        //判断用户是否存在，以及密码不为空
        if (o != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getUserCode(), newpassword);
            if (flag) {
                req.setAttribute(Constants.SYS_MESSAGE, "修改成功，请退出");
                //修改密码，移除session
                //会出现刷新才会返回错误页面
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute(Constants.SYS_MESSAGE, "修改失败");
            }
        } else {
            req.setAttribute(Constants.SYS_MESSAGE, "新密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码，session中可以获得旧密码，不需要重复去数据库中寻找
    private void modifyPwd(HttpServletRequest req, HttpServletResponse resp) {
        //从session拿数据
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //从前端输入的页面中获得输入的旧密码
        String oldpassword = req.getParameter("oldpassword");
        //万能的Map
        Map<String, String> resultMap = new HashMap<String, String>();
        if (o == null) {//取到的session为空，意味着session过期了
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {//如果输入的旧密码为空
            resultMap.put("result", "error");
        } else {//session不为空，输入的旧密码也不为空，则取出当前旧密码与之比较
            String userPassword = ((User) o).getUserPassword();
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询记录
    private void query(HttpServletRequest req, HttpServletResponse resp) {

        //从前端获取数据
        String queryName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //输出控制台，显示参数的当前值
        System.out.println("queryUserName servlet--------" + queryName);
        System.out.println("queryUserRole servlet--------" + queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);

        //分页
        int pageSize = 5;
        int currentPageNo = 1;

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();

        if (queryName == null) {
            queryName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);
        }
        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        //获取用户的总量
        int totalCount = userService.Counts(queryName, queryUserRole);
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        List<User> userList = userService.getUserList(queryName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        //获取角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("queryUserName", queryName);
        req.setAttribute("queryUserRole", queryUserRole);

        //分页
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

    }

    //添加用户
    private void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("当前正在执行增加用户操作");
        //从前端获取数据
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String password = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //将数据存入user
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(password);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        //调用service
        UserService userService = new UserServiceImpl();
        if (userService.add(user)) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }

    //添加用户表单获取的角色
    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //获取角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //验证--userCode是否已存在
    private void ucexist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //先拿到用户的编码
        String userCode = req.getParameter("userCode");
        //用一个hashmap，暂存现在所有现存的用户编码
        HashMap<String, String> resultMap = new HashMap<>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            //userCode == null || userCode.equals("")
            //如果输入的这个编码为空或者不存在，说明可用
            resultMap.put("userCode", "exist");
        } else {//如果输入的编码不为空，则需要去找一下是否存在这个用户
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    //修改用户
    private void modifyexe(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获取 要修改的用户 的id
        String uId = req.getParameter("uid");
        int userId = 0;
        try {
            userId = Integer.parseInt(uId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //从修改信息的表单中封装信息
        User user = new User();
        user.setUserName(req.getParameter("userName"));
        user.setGender(Integer.parseInt(req.getParameter("gender")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("birthday")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(req.getParameter("phone"));
        user.setAddress(req.getParameter("address"));
        user.setUserRole(Integer.parseInt(req.getParameter("userRole")));
        //注意这两个参数不在表单的填写范围内
        user.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        UserServiceImpl userService = new UserServiceImpl();
        if (userService.modify(userId, user)) {
            //如果执行成功了 网页重定向到 用户管理页面(即 查询全部用户列表)
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            //说明 添加失败 转发到此 添加页面
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }

    //删除用户
    private void deluser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uid = req.getParameter("uid");
        int delId = 0;
        try {
            delId = Integer.parseInt(uid);
        } catch (Exception e) {
            delId = 0;
        }

        //需要判断是否能删除成功
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (delId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserService userService = new UserServiceImpl();
            if (userService.delUser(delId)) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //通过id得到用户信息
    private void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String id = req.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(id)) {
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.findById(Integer.parseInt(id));
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

}
