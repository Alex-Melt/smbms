package com.zhao.dao;

import com.zhao.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    //得到登录的用户
    User getLoginUser(Connection connection,String userCode) throws SQLException;

    //修改密码
    int pwdModify(Connection connection,String userCode,String password) throws SQLException;

    //查询用户总数
    int getCounts(Connection connection,String username,int userRole) throws SQLException;

    //通过条件查询 用户列表
    List<User> getUserList(Connection connection,String username,int userRole,int currentPageNo,int PageSize) throws SQLException;

    //添加用户
    int add(Connection connection,User user) throws SQLException;

    //查询该用户是否存在
    User selectUserCodeExist(Connection connection,String userCode) throws SQLException;

    //删除用户
    int delUser(Connection connection,int id) throws SQLException;

    //根据用户id 查询用户信息
     User findById(Connection connection,int userId)throws SQLException;

    //用户管理模块中的子模块 —— 更改用户信息
     int modify(Connection connection,int id,User user)throws SQLException;

}
