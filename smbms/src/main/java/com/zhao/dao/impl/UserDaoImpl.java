package com.zhao.dao.impl;

import com.mysql.cj.util.StringUtils;
import com.zhao.dao.BaseDao;
import com.zhao.dao.UserDao;
import com.zhao.pojo.Role;
import com.zhao.pojo.User;
import jdk.nashorn.internal.runtime.ECMAErrors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Time : 2022/8/5 15:46
 * @Author : 赵浩栋
 * @File : UserDaoImpl.java
 * @Software: IntelliJ IDEA
 */
public class UserDaoImpl implements UserDao {
    //得到登录的用户
    @Override
    public User getLoginUser(Connection connection, String userCode) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        if (connection != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};

            try {
                resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql, params);
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setUserPassword(resultSet.getString("userPassword"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setCreatedBy(resultSet.getInt("createdBy"));
                    user.setCreationDate(resultSet.getTimestamp("creationDate"));
                    user.setModifyBy(resultSet.getInt("modifyBy"));
                    user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(null, preparedStatement, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
                user = null;
            }
        }

        return user;
    }

    //修改密码
    @Override
    public int pwdModify(Connection connection, String userCode, String password) {
        PreparedStatement preparedStatement = null;
        int i = 0;
        if (connection != null) {
            String sql = "update smbms_user set userPassword=? where userCode=?";
            Object[] params = {password, userCode};

            try {
                i = BaseDao.execute(connection, preparedStatement, sql, params);
                BaseDao.closeResource(null, preparedStatement, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    //查询用户总数
    @Override
    public int getCounts(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;
        if (connection != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole=r.id");
            ArrayList<Object> list = new ArrayList<>();

            //if (!StringUtils.isNullOrEmpty(username) && userRole == 0) {
            //    sql.append(" and u.userName like ?");
            //    list.add("%" + username + "%");
            //
            //} else if (!StringUtils.isNullOrEmpty(username) && userRole > 0) {
            //    sql.append(" and u.userName like ? and u.userRole=?");
            //    list.add("%" + username + "%");
            //    list.add(userRole);
            //
            //} else if (StringUtils.isNullOrEmpty(username) && userRole > 0) {
            //    sql.append(" and u.userRole=?");
            //    list.add(userRole);
            //}
            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.username like ?");
                list.add("%" + username + "%");//模糊查询，index:0
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);//index:1
            }
            Object[] params = list.toArray();

            resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql.toString(), params);

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            BaseDao.closeResource(null, preparedStatement, resultSet);
        }
        return count;
    }

    //通过条件查询 用户列表
    @Override
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int PageSize) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> userList = new ArrayList<>();

        if (connection != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole=r.id");
            ArrayList<Object> list = new ArrayList<>();

            //if (!StringUtils.isNullOrEmpty(username) && userRole == 0) {
            //    sql.append(" and u.userName like ?");
            //    list.add("%" + username + "%");
            //
            //} else if (!StringUtils.isNullOrEmpty(username) && userRole > 0) {
            //    sql.append(" and u.userName like ? and u.userRole=?");
            //    list.add("%" + username + "%");
            //    list.add(userRole);
            //
            //} else if (StringUtils.isNullOrEmpty(username) && userRole > 0) {
            //    sql.append(" and u.userRole like ?");
            //    list.add(userRole);
            //}
            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.username like ?");
                list.add("%" + username + "%");//模糊查询，index:0
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);//index:1
            }

            //利用数据库分页
            sql.append(" order by creationDate DESC  limit  ?,?");
            currentPageNo = PageSize * (currentPageNo - 1);
            list.add(currentPageNo);
            list.add(PageSize);

            Object[] params = list.toArray();

            try {
                resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql.toString(), params);
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setUserRoleName(resultSet.getString("userRoleName"));
                    userList.add(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            BaseDao.closeResource(null, preparedStatement, resultSet);

        }


        return userList;
    }

    //添加用户
    @Override
    public int add(Connection connection, User user) throws SQLException {
        PreparedStatement preparedStatement = null;
        int i = 0;

        if (connection != null) {
            String sql = "insert into smbms_user(userCode, userName, userPassword, gender, " +
                    "birthday, phone, address, userRole, createdBy, creationDate) values (?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(), user.getGender(), user.getBirthday(), user.getPhone(),
                    user.getAddress(), user.getUserRole(), user.getCreatedBy(), user.getCreationDate()};

            i = BaseDao.execute(connection, preparedStatement, sql, params);
            BaseDao.closeResource(null, preparedStatement, null);
        }
        return i;
    }

    //查询该用户是否存在
    @Override
    public User selectUserCodeExist(Connection connection,String userCode) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        if (connection != null) {
            String sql = "select userCode from smbms_user where userCode=?";
            Object[] params = {userCode};
            resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql, params);
            //if (resultSet==null){
            //    user=null;
            //}
             if (resultSet.next()){
                user=new User();
                user.setUserCode(resultSet.getString("userCode"));
            }
            BaseDao.closeResource(null,preparedStatement,resultSet);
        }
        return user;

    }

    //删除用户
    @Override
    public int delUser(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement=null;
        int execute=0;

        if (connection!=null){
            String sql="delete from smbms_user where id =?";
            Object[] params={id};

            execute = BaseDao.execute(connection, preparedStatement, sql, params);

            BaseDao.closeResource(null,preparedStatement,null);
        }
        return execute;
    }

    //根据用户id 查询用户信息
    @Override
    public User findById(Connection connection, int userId) throws SQLException {
        User user = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if(connection != null){
            String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id = ? and u.userRole = r.id";
            Object[] params ={userId};
            resultSet = BaseDao.execute(connection,preparedStatement,resultSet,sql,params);
            if(resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getTimestamp("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                user.setUserRoleName(resultSet.getString("userRoleName"));
            }
            //释放资源
            BaseDao.closeResource(null,preparedStatement,resultSet);
        }
        return user;
    }

    //用户管理模块中的子模块 —— 更改用户信息
    @Override
    public int modify(Connection connection, int id, User user) throws SQLException {
        PreparedStatement preparedStatement=null;
        int execute=0;
        if (connection!=null){
            String sql="update smbms_user set userName = ?,gender = ?,birthday =?,phone = ?,address = ?,userRole = ?,modifyBy = ?,modifyDate = ? where id = ?";
            Object[] params={user.getUserName(),user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifyBy(),user.getModifyDate(),id};

            execute = BaseDao.execute(connection, preparedStatement, sql, params);

            BaseDao.closeResource(null,preparedStatement,null);
        }


        return execute;
    }


}
