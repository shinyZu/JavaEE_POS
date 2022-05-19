package servlets;

import business.custom.impl.CustomerBOImpl;
import dto.CustomerDTO;
import util.JsonUtil;

import javax.annotation.Resource;
import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pos")
    DataSource ds;

    CustomerBOImpl customerBO = new CustomerBOImpl();
    JsonObjectBuilder responseInfo = JsonUtil.getJsonObjectBuilder();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            JsonObjectBuilder customer = Json.createObjectBuilder();
            String option = req.getParameter("option");
            Connection connection = ds.getConnection();

            switch (option) {
                case "SEARCH":
                    String customerID = req.getParameter("customerID");
                    String customerName = req.getParameter("customerName");

                    CustomerDTO customerDTO = new CustomerDTO(customerID, customerName);
                    ArrayList<CustomerDTO> customerDetails = customerBO.search(connection, customerDTO);

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

                    String checkStatus = customerBO.isDuplicateContact(connection, new CustomerDTO(id, Integer.parseInt(contact)));

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
                    String customerCount = customerBO.getCustomerCount(connection);
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Customers Counted");
                    responseInfo.add("data", customerCount);
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "LAST_ID":
                    String lastId = customerBO.getLastId(connection);
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
                    ArrayList<CustomerDTO> customerIdNames = customerBO.getIdNames(connection);

                    if (customerIdNames != null) {
                        for (CustomerDTO dto : customerIdNames) {
                            customer.add("id", dto.getCustomerId());
                            customer.add("name", dto.getCustomerName());

                            allCustomers.add(customer.build());
                        }
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Received all IDs & Names");
                        responseInfo.add("data", allCustomers.build());

                        resp.getWriter().print(responseInfo.build());
                    }
                    break;

                case "GETALL":
                    ArrayList<CustomerDTO> customers = customerBO.getAllCustomers(connection);
                    if (customers != null) {
                        for (CustomerDTO dto : customers) {
                            customer.add("id", dto.getCustomerId());
                            customer.add("name", dto.getCustomerName());
                            customer.add("address", dto.getCustomerAddress());
                            customer.add("contact", dto.getCustomerContact());

                            allCustomers.add(customer.build());
                        }
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", HttpServletResponse.SC_OK); // 200
                        responseInfo.add("message", "Received All Customers");
                        responseInfo.add("data", allCustomers.build());
                        resp.getWriter().print(responseInfo.build());

//                        resp.getWriter().print(JsonUtil.generateResponse(200, "Done", allCustomers. build()));
                    }
                    break;
            }
            connection.close();

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
            Connection connection = ds.getConnection();
            resp.setContentType("application/json");

            if (customerBO.addCustomer(connection, customerDTO)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);// 201

                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Customer Saved Successfully...");
                responseInfo.add("status", 200);
                resp.getWriter().print(responseInfo.build());

//                resp.getWriter().print(JsonUtil.generateResponse(200, "Customer Saved Successfully", ""));
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException throwables) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 400);
            responseInfo.add("message", "Something Went Wrong...");
            responseInfo.add("data", throwables.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
//            resp.getWriter().print(JsonUtil.generateResponse(400, "Something Went Wrong...", throwables.getLocalizedMessage()));
            throwables.printStackTrace();

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        CustomerDTO customerDTO = new CustomerDTO(
                jsonObject.getString("id"),
                jsonObject.getString("name"),
                jsonObject.getString("address"),
                Integer.parseInt(jsonObject.getString("contact"))
        );

        try {
            Connection connection = ds.getConnection();
            resp.setContentType("application/json");

            if (customerBO.updateCustomer(connection, customerDTO)) {
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

        } catch (SQLException | ClassNotFoundException e) {
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
            Connection connection = ds.getConnection();
            resp.setContentType("application/json");

            if (customerBO.deleteCustomer(connection, new CustomerDTO(req.getParameter("customerID")))) {
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

        } catch (SQLException | ClassNotFoundException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Deleting...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();

        }
    }
}
