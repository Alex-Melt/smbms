package com.zhao.service;

import com.zhao.pojo.Role;
import com.zhao.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    //用户登陆
    User login(String userCode,String password);

    //修改密码
    boolean updatePwd(String userCode,String password);

    //查询记录数
    int Counts(String userName,int userRole);

    //通过条件查询 用户列表
    List<User> getUserList(String username, int userRole, int currentPageNo, int PageSize);

    //添加用户
    boolean add(User user);

    //查询该用户是否存在
    User selectUserCodeExist(String userCode);

    //删除用户
    boolean delUser(int id);

    //通过id得到用户
    User findById(int id);

    //修改用户
    boolean modify(int id,User user);
}
