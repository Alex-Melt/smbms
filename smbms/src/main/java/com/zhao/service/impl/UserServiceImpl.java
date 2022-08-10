package com.zhao.service.impl;

import com.zhao.dao.BaseDao;
import com.zhao.dao.UserDao;
import com.zhao.dao.impl.UserDaoImpl;
import com.zhao.pojo.User;
import com.zhao.service.UserService;
import com.zhao.utils.Constants;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Time : 2022/8/5 16:01
 * @Author : 赵浩栋
 * @File : UserServiceImpl.java
 * @Software: IntelliJ IDEA
 */
public class UserServiceImpl implements UserService {
    //业务层都会调用dao
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    //用户登陆
    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        if (user == null) {
            return null;
        } else if (!user.getUserPassword().equals(password)) {
            user = null;
        }
        return user;
    }

    //修改密码
    @Override
    public boolean updatePwd(String userCode, String password) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            if (userDao.pwdModify(connection, userCode, password) > 0) {
                flag = true;
            }
            ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }

        return flag;
    }

    //查询记录数
    @Override
    public int Counts(String userName, int userRole) {
        Connection connection = null;
        int count = 0;

        try {
            connection = BaseDao.getConnection();

            count = userDao.getCounts(connection, userName, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    //通过条件查询 用户列表
    @Override
    public List<User> getUserList(String username, int userRole, int currentPageNo, int PageSize) {
        Connection connection = null;
        List<User> userList = null;

        try {
            connection = BaseDao.getConnection();

            userList = userDao.getUserList(connection, username, userRole, currentPageNo, PageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return userList;

    }

    //添加用户
    @Override
    public boolean add(User user) {
        boolean flag = false;
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int add = userDao.add(connection, user);
            connection.commit();
            System.out.println(add);
            if (add > 0) {
                flag = true;
                System.out.println("add success!");
            } else {
                System.out.println("add failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("rollback--------");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection,null,null);
        }

        return flag;
    }

    //查询该用户是否存在
    @Override
    public User selectUserCodeExist(String userCode) {
        Connection connection=null;
        User user = null;

        connection=BaseDao.getConnection();
        try {
            user=userDao.selectUserCodeExist(connection,userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    //删除用户
    @Override
    public boolean delUser(int id)  {
       boolean flag=false;
       Connection connection=null;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int i = userDao.delUser(connection, id);
            connection.commit();
            System.out.println(i);
            if (i > 0) {
                flag = true;
                System.out.println("delete success!");
            } else {
                System.out.println("delete failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("rollback--------");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    //通过id得到用户
    @Override
    public User findById(int id) {
        Connection connection=null;
        User user = null;


        connection=BaseDao.getConnection();
        try {
            user=userDao.findById(connection,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    //修改用户
    @Override
    public boolean modify(int id, User user) {
        Connection connection=null;
        boolean flag=false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int i = userDao.modify(connection, id,user);
            connection.commit();
            System.out.println(i);
            if (i > 0) {
                flag = true;
                System.out.println("update success!");
            } else {
                System.out.println("update failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("rollback--------");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    //@Test
    //public void Test(){
    //    UserServiceImpl userService = new UserServiceImpl();
    //    User user = new User();
    //    user.setUserCode("111");
    //    user.setUserName("111");
    //    user.setUserPassword("111");
    //    user.setAddress("address");
    //    try {
    //        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1986-03-28"));
    //    } catch (ParseException e) {
    //        e.printStackTrace();
    //    }
    //    user.setGender(1);
    //    user.setPhone("111");
    //    user.setUserRole(1);
    //    user.setCreationDate(new Date());
    //    user.setCreatedBy(1);
    //    boolean add = userService.add(user);
    //}

}
