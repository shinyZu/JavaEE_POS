package servlets;

import business.CustomerBOImpl;
import dto.CustomerDTO;
import entity.Customer;
import org.apache.commons.dbcp2.BasicDataSource;
import util.JsonUtil;

import javax.json.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    CustomerBOImpl customerBO = new CustomerBOImpl();
    //    JsonObjectBuilder responseInfo;
    JsonObjectBuilder responseInfo = JsonUtil.getJsonObjectBuilder();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            ServletContext servletContext = req.getServletContext();
            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            JsonObjectBuilder customer = Json.createObjectBuilder();
            String option = req.getParameter("option");

            switch (option) {
                case "SEARCH":
                    String customerID = req.getParameter("customerID");
                    String customerName = req.getParameter("customerName");

                    CustomerDTO customerDTO = new CustomerDTO(customerID, customerName);
                    ArrayList<CustomerDTO> customerDetails = customerBO.search(servletContext, customerDTO);

                    if (customerDetails != null) {
                        for (CustomerDTO dto : customerDetails) {
                            customer.add("id", dto.getCustomerId());
                            customer.add("name", dto.getCustomerName());
                            customer.add("address", dto.getCustomerAddress());
                            customer.add("contact", dto.getCustomerContact());
                        }
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Customer Search With ID");
                        responseInfo.add("data", customer.build());
                        resp.getWriter().print(responseInfo.build());
                    }
                    break;

                case "CHECK_FOR_DUPLICATE":
                    String id = req.getParameter("customerId");
                    String input = req.getParameter("input");
                    String contact = input.split("0")[1];

                    String checkStatus = customerBO.isDuplicateContact(servletContext, new CustomerDTO(id, Integer.parseInt(contact)));

                    if (checkStatus.equals("Match")) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", checkStatus);
                        responseInfo.add("data", contact);
                        resp.getWriter().print(responseInfo.build());

                    } else if (checkStatus.equals("Duplicate")) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", checkStatus);
                        responseInfo.add("data", contact);
                        resp.getWriter().print(responseInfo.build());

                    } else {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", checkStatus);
                        responseInfo.add("data", contact);
                        resp.getWriter().print(responseInfo.build());
                    }
                    break;

                case "GET_COUNT":
                    String customerCount = customerBO.getCustomerCount(servletContext);
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Customers Counted");
                    responseInfo.add("data", customerCount);
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "LAST_ID":
                    String lastId = customerBO.getLastId(servletContext);
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);

                    if (lastId != null) {
                        responseInfo.add("message", "Retrieved Last CustomerId...");
                        responseInfo.add("data", lastId);

                    } else {
                        responseInfo.add("message", "No any Customers yet");
                        responseInfo.add("data", "null");
                    }
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GET_ID_NAME":
                    ArrayList<CustomerDTO> customerIdNames = customerBO.getIdNames(servletContext);

                    if (customerIdNames != null) {
                        for (CustomerDTO dto : customerIdNames) {
                            customer.add("id", dto.getCustomerId());
                            customer.add("name", dto.getCustomerName());

                            allCustomers.add(customer.build());
                        }
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("data", allCustomers.build());
                        responseInfo.add("message", "Received all IDs & Names");
                        responseInfo.add("status", 200);

                        resp.getWriter().print(responseInfo.build());
                    }
                    break;

                case "GETALL":
                    ArrayList<CustomerDTO> customers = customerBO.getAllCustomers(servletContext);
                    if (customer != null) {
                        for (CustomerDTO dto : customers) {
                            customer.add("id", dto.getCustomerId());
                            customer.add("name", dto.getCustomerName());
                            customer.add("address", dto.getCustomerAddress());
                            customer.add("contact", dto.getCustomerContact());

                            allCustomers.add(customer.build());
                        }
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("data", allCustomers.build());
                        responseInfo.add("message", "Done");
                        responseInfo.add("status", HttpServletResponse.SC_OK); // 200

                        resp.getWriter().print(responseInfo.build());
                    }
                    break;
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CustomerDTO customerDTO = new CustomerDTO(
                req.getParameter("customerID"),
                req.getParameter("customerName"),
                req.getParameter("customerAddress"),
                Integer.parseInt(req.getParameter("customerContact"))
        );

        try {
            resp.setContentType("application/json");
            ServletContext servletContext = req.getServletContext();
            BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
            Connection connection = bds.getConnection();

            if (customerBO.addCustomer(servletContext,customerDTO)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);// 201
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Customer Saved Successfully...");
                responseInfo.add("status", 200);
                resp.getWriter().print(responseInfo.build());
                System.out.println("Saved Successfully....");
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 400);
            responseInfo.add("message", "Something Went Wrong...");
            responseInfo.add("data", throwables.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            throwables.printStackTrace();

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String customerID = jsonObject.getString("id");
        String customerName = jsonObject.getString("name");
        String customerAddress = jsonObject.getString("address");
        String customerContact = jsonObject.getString("contact");

        try {
            ServletContext servletContext = req.getServletContext();
            BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
            Connection connection = bds.getConnection();

            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET customerName=?,customerAddress=?,customerContact=? WHERE customerId=?");
            pstm.setObject(1, customerName);
            pstm.setObject(2, customerAddress);
            pstm.setObject(3, customerContact);
            pstm.setObject(4, customerID);


            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Customer Updated Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Error Occurred While Updating...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
            }

            connection.close();

        } catch (SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Updating...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ServletContext servletContext = req.getServletContext();
            BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
            Connection connection = bds.getConnection();

            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE customerId = ?");
            pstm.setObject(1, req.getParameter("customerID"));

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Customer Deleted Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Invalid Customer ID...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
            }
            connection.close();

        } catch (SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Deleting...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();

        }
    }
}
