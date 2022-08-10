package com.zhao.dao;

import com.zhao.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RoleDao {

    //获取角色列表
    List<Role> getRoleList(Connection connection) throws SQLException;
}
