package com.zhao.dao.impl;

import com.mysql.cj.util.StringUtils;
import com.zhao.dao.BaseDao;
import com.zhao.dao.BillDao;
import com.zhao.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Time : 2022/8/7 18:01
 * @Author : 赵浩栋
 * @File : BillDaoImpl.java
 * @Software: IntelliJ IDEA
 */
public class BillDaoImpl implements BillDao {

    @Override
    //根据用户输入的值，新增订单表
    public int add(Connection connection, Bill bill) throws Exception {
        int updateNum=0;
        PreparedStatement pstm=null;
        if(connection!=null){
            String sql = "insert into smbms_bill (billCode,productName,productDesc," +
                    "productUnit,productCount,totalPrice,isPayment,providerId,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {bill.getBillCode(),bill.getProductName(),bill.getProductDesc(),
                    bill.getProductUnit(),bill.getProductCount(),bill.getTotalPrice(),bill.getIsPayment(),
                    bill.getProviderId(),bill.getCreatedBy(),bill.getCreationDate()};
            updateNum = BaseDao.execute(connection,pstm,sql,params);
            BaseDao.closeResource(null, pstm, null);
            System.out.println("dao--------修改行数 " + updateNum);
        }
        return updateNum;
    }

    //查询所有帐单列表
    @Override
    public List<Bill> getBillList(Connection connection, Bill bill) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Bill> billList = new ArrayList<>();

        if (connection != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("select b.*,p.proName as providerName from smbms_bill b ,smbms_provider p where b.providerId=p.id");

            ArrayList<Object> list = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(bill.getProductName())) {
                sql.append(" and b.productName like ?");
                list.add("%" + bill.getProductName() + "%");//模糊查询，index:0
            }
            if (bill.getProviderId() > 0) {
                sql.append(" and b.providerId=?");
                list.add(bill.getProviderId());
            }
            if (bill.getIsPayment() > 0) {//判断是否选择了是否付款
                sql.append(" and b.isPayment=?");
                list.add(bill.getIsPayment());
            }
            Object[] params = list.toArray();

            resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql.toString(), params);
            System.out.println("当前的sql是----->"+sql.toString());
            while (resultSet.next()) {
                Bill _bill = new Bill();//创建一个bill对象存储查询到的属性
                _bill.setId(resultSet.getInt("id"));
                _bill.setBillCode(resultSet.getString("billCode"));
                _bill.setProductName(resultSet.getString("productName"));
                _bill.setProductDesc(resultSet.getString("productDesc"));
                _bill.setProductUnit(resultSet.getString("productUnit"));
                _bill.setProductCount(resultSet.getBigDecimal("productCount"));
                _bill.setTotalPrice(resultSet.getBigDecimal("totalPrice"));
                _bill.setIsPayment(resultSet.getInt("isPayment"));
                _bill.setProviderId(resultSet.getInt("providerId"));
                _bill.setProviderName(resultSet.getString("providerName"));
                _bill.setCreationDate(resultSet.getTimestamp("creationDate"));
                _bill.setCreatedBy(resultSet.getInt("createdBy"));
                billList.add(_bill);
            }

            BaseDao.closeResource(null, preparedStatement, resultSet);
        }

        return billList;
    }

    @Override
    //根据订单id删除该订单
    public int deleteBillById(Connection connection, String delId) throws Exception {
        int delNum=0;
        PreparedStatement pstm=null;
        if(connection!=null){
            String sql="DELETE FROM `smbms_bill` WHERE id=?";
            Object[] params={delId};
            delNum = BaseDao.execute(connection,pstm,sql,params);
            BaseDao.closeResource(null,pstm,null);
        }
        return delNum;
    }


    @Override
    //通过订单id得到该订单的所有信息（正确）
    public Bill getBillById(Connection connection, String id) throws Exception {

        Bill bill = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(null != connection){
            String sql = "select b.*,p.proName as providerName from smbms_bill b, smbms_provider p " +
                    "where b.providerId = p.id and b.id=?";
            Object[] params = {id};
            rs = BaseDao.execute(connection,pstm,rs,sql,params);
            if(rs.next()){
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return bill;
    }


    @Override
    //根据用户传递输入的值修改订单表
    public int modify(Connection connection, Bill bill) throws Exception {
        int modifyNum=0;
        PreparedStatement pstm=null;
        if(connection!=null){
            String sql = "update smbms_bill set productName=?," +
                    "productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
                    "isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {bill.getProductName(),bill.getProductDesc(),
                    bill.getProductUnit(),bill.getProductCount(),bill.getTotalPrice(),bill.getIsPayment(),
                    bill.getProviderId(),bill.getModifyBy(),bill.getModifyDate(),bill.getId()};
            modifyNum=BaseDao.execute(connection,pstm,sql,params);
            BaseDao.closeResource(null,pstm,null);
        }
        return modifyNum;
    }


    @Override
    //通过供应商id得到该供应商总订单数(正确）
    public int getBillCountByProviderId(Connection connection, String providerId) throws Exception {
        int billcount=0;
        PreparedStatement pstm=null;
        ResultSet rs=null;
        if(connection!=null){
            String sql="SELECT COUNT(1) as billCount FROM `smbms_bill` WHERE `providerId`=?";
            Object[] params={providerId};
            rs = BaseDao.execute(connection,pstm,rs,sql,params);
            while(rs.next()){
                billcount=rs.getInt("billCount");
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return billcount;
    }


}