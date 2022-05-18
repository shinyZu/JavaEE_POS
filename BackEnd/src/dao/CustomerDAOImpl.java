package dao;

import dto.CustomerDTO;
import entity.Customer;
import org.apache.commons.dbcp2.BasicDataSource;
import util.CrudUtil;

import javax.annotation.Resource;
import javax.json.Json;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class CustomerDAOImpl {

    public ArrayList<Customer> search(ServletContext servletContext, Customer customer) throws ClassNotFoundException, SQLException {
        ArrayList<Customer> customerDetails = new ArrayList<>();

        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();

        if (!customer.getCustomerId().equals("")) {
            ResultSet rst = CrudUtil.executeQuery(connection,"SELECT * FROM Customer WHERE customerId = ?", customer.getCustomerId());

            while (rst.next()) {
                customerDetails.add(
                        new Customer(
                                rst.getString(1),
                                rst.getString(2),
                                rst.getString(3),
                                rst.getInt(4)
                        )
                );
            }
            connection.close();
            return  customerDetails;

        } else if (!customer.getCustomerName().equals("")) {
            ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Customer WHERE customerName = ?", customer.getCustomerName());

            while (rst.next()) {
                customerDetails.add(
                        new Customer(
                                rst.getString(1),
                                rst.getString(2),
                                rst.getString(3),
                                rst.getInt(4)
                        )
                );
            }
            return  customerDetails;
        }
        connection.close();
        return null;
    }

    public String isDuplicateContact(ServletContext servletContext, Customer customer) throws SQLException, ClassNotFoundException {

        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();

        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT customerId, customerContact FROM Customer");

        while (rst.next()) {
            if (rst.getString(2).equals(String.valueOf(customer.getCustomerContact()))) {
                if(rst.getString(1).equals(customer.getCustomerId())) {
                    return "Match";
                } else {
                    return "Duplicate";
                }
            }
        }
        connection.close();
        return "Unique";
    }

    public String getCount(ServletContext servletContext) throws SQLException, ClassNotFoundException {

        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();

        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT COUNT(customerId) FROM Customer");

        if (rst.next()) {
            return rst.getString(1);
        }
        connection.close();
        return "0";
    }

    public String getLastId(ServletContext servletContext) throws SQLException, ClassNotFoundException {

        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();

        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT customerId FROM Customer ORDER BY customerId DESC LIMIT 1");

        if (rst.next()) {
            return rst.getString(1);
        }
        connection.close();
        return null;
    }

    public ArrayList<Customer> getIdNames(ServletContext servletContext) throws SQLException, ClassNotFoundException {

        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();
        ArrayList<Customer> customerIdNames = new ArrayList<>();

        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT customerId, customerName FROM Customer");

        while (rst.next()) {
            customerIdNames.add(new Customer(
                    rst.getString(1),
                    rst.getString(2)
            ));
        }
        connection.close();
        return customerIdNames;
    }

    public ArrayList<Customer> getAll(ServletContext servletContext) throws SQLException, ClassNotFoundException {
        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();
        ArrayList<Customer> allCustomers = new ArrayList<>();

        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Customer");
        while (rst.next()) {
            allCustomers.add(new Customer(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getInt(4)
            ));
        }
        connection.close();
        return allCustomers;
    }

    public boolean addCustomer(ServletContext servletContext, Customer customer) throws SQLException, ClassNotFoundException {
        BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
        Connection connection = bds.getConnection();

        return CrudUtil.executeUpdate(connection, "INSERT INTO Customer VALUES (?,?,?,?)",
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getCustomerAddress(),
                customer.getCustomerContact()
        );
    }
}
