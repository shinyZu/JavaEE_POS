package business;

import dao.CustomerDAOImpl;
import dto.CustomerDTO;
import entity.Customer;

import javax.servlet.ServletContext;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerBOImpl {

    CustomerDAOImpl customerDAO = new CustomerDAOImpl();

    public ArrayList<CustomerDTO> search(ServletContext servletContext, CustomerDTO dto) throws ClassNotFoundException, SQLException {
        ArrayList<CustomerDTO> customerDetails = new ArrayList<>();
        for (Customer c : customerDAO.search(servletContext, new Customer(dto.getCustomerId(), dto.getCustomerName()))) {
            customerDetails.add(new CustomerDTO(
                    c.getCustomerId(),
                    c.getCustomerName(),
                    c.getCustomerAddress(),
                    c.getCustomerContact()
            ));
        }
        return customerDetails;
    }

    public String isDuplicateContact(ServletContext servletContext, CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.isDuplicateContact(servletContext,  new Customer(dto.getCustomerId(),dto.getCustomerContact()));
    }

    public String getCustomerCount(ServletContext servletContext) throws SQLException, ClassNotFoundException {
        return customerDAO.getCount(servletContext);
    }

    public String getLastId(ServletContext servletContext) throws SQLException, ClassNotFoundException {
        return customerDAO.getLastId(servletContext);
    }

    public ArrayList<CustomerDTO> getIdNames(ServletContext servletContext) throws SQLException, ClassNotFoundException {
        ArrayList<CustomerDTO> idNames = new ArrayList<>();
        for (Customer c : customerDAO.getIdNames(servletContext)) {
            idNames.add(new CustomerDTO(
                    c.getCustomerId(),
                    c.getCustomerName()
            ));
        }
        return idNames;
    }

    public ArrayList<CustomerDTO> getAllCustomers(ServletContext servletContext) throws SQLException, ClassNotFoundException {
        ArrayList<CustomerDTO> allCustomers = new ArrayList<>();
        for (Customer c : customerDAO.getAll(servletContext)) {
            allCustomers.add(new CustomerDTO(
                    c.getCustomerId(),
                    c.getCustomerName(),
                    c.getCustomerAddress(),
                    c.getCustomerContact()
            ));
        }
        return allCustomers;
    }

    public boolean addCustomer(ServletContext servletContext, CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.addCustomer(servletContext, new Customer(
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getCustomerAddress(),
                dto.getCustomerContact()
        ));

    }
}
