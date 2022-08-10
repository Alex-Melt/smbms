package com.zhao.service.impl;

import com.zhao.dao.BaseDao;
import com.zhao.dao.RoleDao;
import com.zhao.dao.impl.RoleDaoImpl;
import com.zhao.pojo.Role;
import com.zhao.service.RoleService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Time : 2022/8/6 17:24
 * @Author : 赵浩栋
 * @File : RoleServiceImpl.java
 * @Software: IntelliJ IDEA
 */
public class RoleServiceImpl implements RoleService {
    private RoleDao roleDao;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }


    @Override
    public List<Role> getRoleList() {
        Connection connection = null;

        List<Role> roleList = null;
        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }


        return roleList;
    }
}
