package com.zhao.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Time : 2022/8/5 14:57
 * @Author : 赵浩栋
 * @File : BaseDao.java
 * @Software: IntelliJ IDEA
 */
//操作数据库的公共类
public class BaseDao {
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    //静态代码块，类加载就初始化
    static {
        //通过类加载器读取
        Properties properties = new Properties();
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver= properties.getProperty("driver");
        url= properties.getProperty("url");
        user= properties.getProperty("user");
        password= properties.getProperty("password");


    }

    //获取数据库的链接
    public static Connection getConnection(){
        Connection connection=null;
        try {
            Class.forName(driver);
            connection= DriverManager.getConnection(url,user,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共类
    public static ResultSet execute(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet,String sql,Object[] params) throws SQLException {
        //预编译的sql直接执行
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }

        return resultSet = preparedStatement.executeQuery();

    }
    //编写增删改公共类
    public static int execute(Connection connection,PreparedStatement preparedStatement,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }

        return  preparedStatement.executeUpdate();

    }

    //关闭连接
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag=true;

        if (resultSet!=null) {
            try {
                resultSet.close();
                //GC回收
                resultSet=null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag=false;
            }
        }

        if (preparedStatement!=null) {
            try {
                preparedStatement.close();
                //GC回收
                preparedStatement=null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag=false;
            }
        }

        if (connection!=null) {
            try {
                connection.close();
                //GC回收
                connection=null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag=false;
            }
        }

        return flag;
    }
}



