package com.zhao.dao;

import com.zhao.pojo.Bill;

import java.sql.Connection;
import java.util.List;

public interface BillDao {

    //增加订单
    int add(Connection connection, Bill bill) throws Exception;

    //通过查询条件获取供应商列表-模糊查询-getBillList
    List<Bill> getBillList(Connection connection, Bill bill) throws Exception;

    //通过delId删除Bill
    int deleteBillById(Connection connection, String delId) throws Exception;

    //通过billId获取Bill
    Bill getBillById(Connection connection, String id) throws Exception;

    //修改订单信息
    int modify(Connection connection, Bill bill) throws Exception;

    //根据供应商ID查询订单数量
    int getBillCountByProviderId(Connection connection, String providerId) throws Exception;

}
