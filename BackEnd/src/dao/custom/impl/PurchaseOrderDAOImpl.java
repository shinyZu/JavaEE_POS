package dao.custom.impl;

import entity.OrderDetails;
import entity.Orders;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PurchaseOrderDAOImpl {
    public String getCount(Connection connection) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT COUNT(orderId) FROM Orders");
        if (rst.next()) {
            return rst.getString(1);
        }
        return "0";
    }

    public String getLastId(Connection connection) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT orderId FROM Orders ORDER BY orderId DESC LIMIT 1");
        if (rst.next()) {
            return rst.getString(1);
        }
        return null;
    }

    public ArrayList<Orders> getAll(Connection connection) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> allOrders = new ArrayList<>();
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Orders");
        while (rst.next()) {
            allOrders.add(new Orders(
                    rst.getString(1),
                    rst.getDate(2),
                    rst.getDouble(3),
                    rst.getInt(4),
                    rst.getString(5)
            ));
        }
        return allOrders;
    }

    public ArrayList<OrderDetails> getAllOrderDetails(Connection connection) throws SQLException, ClassNotFoundException {
        ArrayList<OrderDetails> allOrderDetails = new ArrayList<>();
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM OrderDetails");
        while (rst.next()) {
            allOrderDetails.add(new OrderDetails(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getInt(3)
            ));
        }
        return allOrderDetails;

    }

    public boolean addOrder(Connection connection, Orders orders) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "INSERT INTO Orders VALUES (?,?,?,?,?)",
                orders.getOrderId(),
                orders.getOrderDate(),
                orders.getOrderCost(),
                orders.getDiscount(),
                orders.getCustomerId()
        );
    }

    public boolean addOrderDetail(Connection connection, OrderDetails detail) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "INSERT INTO OrderDetails VALUES (?,?,?)",
                detail.getOrderId(),
                detail.getItemCode(),
                detail.getOrderQty()
        );
    }

    public boolean delete(Connection connection, Orders orders) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "DELETE FROM Orders WHERE orderId = ?",
                orders.getOrderId()
        );
    }
}
