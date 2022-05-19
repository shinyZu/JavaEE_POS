package business;

import dao.CustomerDAOImpl;
import dto.CustomerDTO;
import entity.Customer;
import util.CrudUtil;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerBOImpl {

    CustomerDAOImpl customerDAO = new CustomerDAOImpl();

    public ArrayList<CustomerDTO> search(Connection connection, CustomerDTO dto) throws ClassNotFoundException, SQLException {
        ArrayList<CustomerDTO> customerDetails = new ArrayList<>();
        for (Customer c : customerDAO.search(connection, new Customer(dto.getCustomerId(), dto.getCustomerName()))) {
            customerDetails.add(new CustomerDTO(
                    c.getCustomerId(),
                    c.getCustomerName(),
                    c.getCustomerAddress(),
                    c.getCustomerContact()
            ));
        }
//        CrudUtil.getConnection().close();
        return customerDetails;
    }

    public String isDuplicateContact(Connection connection, CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.isDuplicateContact(connection, new Customer(dto.getCustomerId(),dto.getCustomerContact()));
    }

    public String getCustomerCount(Connection connection) throws SQLException, ClassNotFoundException {
        return customerDAO.getCount(connection);
    }

    public String getLastId(Connection connection) throws SQLException, ClassNotFoundException {
        return customerDAO.getLastId(connection);
    }

    public ArrayList<CustomerDTO> getIdNames(Connection connection) throws SQLException, ClassNotFoundException {
        ArrayList<CustomerDTO> idNames = new ArrayList<>();
        for (Customer c : customerDAO.getIdNames(connection)) {
            idNames.add(new CustomerDTO(
                    c.getCustomerId(),
                    c.getCustomerName()
            ));
        }
        return idNames;
    }

    public ArrayList<CustomerDTO> getAllCustomers(Connection connection) throws SQLException, ClassNotFoundException {
        ArrayList<CustomerDTO> allCustomers = new ArrayList<>();
        for (Customer c : customerDAO.getAll(connection)) {
            allCustomers.add(new CustomerDTO(
                    c.getCustomerId(),
                    c.getCustomerName(),
                    c.getCustomerAddress(),
                    c.getCustomerContact()
            ));
        }
        return allCustomers;
    }

    public boolean addCustomer(Connection connection, CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.add(connection, new Customer(
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getCustomerAddress(),
                dto.getCustomerContact()
        ));

    }

    public boolean updateCustomer(Connection connection, CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.update(connection,new Customer(
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getCustomerAddress(),
                dto.getCustomerContact()
        ));
    }

    public boolean deleteCustomer(Connection connection, CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.delete(connection, new Customer(dto.getCustomerId()));
    }
}
