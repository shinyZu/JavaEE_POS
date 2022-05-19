package servlets;

import business.BOFactory;
import business.custom.CustomerBO;
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
import java.util.LinkedHashMap;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pos")
    DataSource ds;

    private final CustomerBO customerBO = (CustomerBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.CUSTOMER);
    private final LinkedHashMap<String, Object> mapResponseInfo = new LinkedHashMap<>();
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
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
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Customer Search With ID");
                        responseInfo.add("data", customer.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Customer Search With ID", customer.build()));
                    }
                    break;

                case "CHECK_FOR_DUPLICATE":
                    String id = req.getParameter("customerId");
                    String input = req.getParameter("input");
                    String contact = input.split("0")[1];

                    String checkStatus = customerBO.isDuplicateContact(connection, new CustomerDTO(id, Integer.parseInt(contact)));

                    if (checkStatus.equals("Match")) {
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", checkStatus);
                        responseInfo.add("data", contact);
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, checkStatus, contact));

                    } else if (checkStatus.equals("Duplicate")) {
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", checkStatus);
                        responseInfo.add("data", contact);
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, checkStatus, contact));

                    } else {
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", checkStatus);
                        responseInfo.add("data", contact);
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, checkStatus, contact));
                    }
                    break;

                case "GET_COUNT":
                    String customerCount = customerBO.getCustomerCount(connection);
                   /* responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Customers Counted");
                    responseInfo.add("data", customerCount);
                    resp.getWriter().print(responseInfo.build());*/
                    resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Customers Counted", customerCount));
                    break;

                case "LAST_ID":
                    String lastId = customerBO.getLastId(connection);
                    /*responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);*/

                    if (lastId != null) {
                        /*responseInfo.add("message", "Retrieved Last CustomerId...");
                        responseInfo.add("data", lastId);*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Retrieved Last CustomerId...", lastId));

                    } else {
                        /*responseInfo.add("message", "No any Customers yet");
                        responseInfo.add("data", "null");*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "No any Customers yet.", "null"));
                    }
//                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GET_ID_NAME":
                    ArrayList<CustomerDTO> customerIdNames = customerBO.getIdNames(connection);

                    if (customerIdNames != null) {
                        for (CustomerDTO dto : customerIdNames) {
                            customer.add("id", dto.getCustomerId());
                            customer.add("name", dto.getCustomerName());

                            allCustomers.add(customer.build());
                        }
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Received all IDs & Names");
                        responseInfo.add("data", allCustomers.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Received all IDs & Names", allCustomers.build()));
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
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", HttpServletResponse.SC_OK); // 200
                        responseInfo.add("message", "Received All Customers");
                        responseInfo.add("data", allCustomers.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Received All Customers", allCustomers.build()));
                    }
                    break;
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Occurred", e.getLocalizedMessage()));
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

            if (customerBO.addCustomer(connection, customerDTO)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);// 201
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Customer Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Customer Saved Successfully...", ""));

            }
            connection.close();

        } catch (SQLException | ClassNotFoundException throwables) {
            /*responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 400);
            responseInfo.add("message", "Something Went Wrong...");
            responseInfo.add("data", throwables.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            throwables.printStackTrace();*/
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Something Went Wrong...", throwables.getLocalizedMessage()));

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

            if (customerBO.updateCustomer(connection, customerDTO)) {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Customer Updated Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Customer Updated Successfully...", ""));

            } else {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Error Occurred While Updating...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Error Occurred While Updating...", ""));
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            /*responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Updating...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();*/
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Occurred While Updating...", e.getLocalizedMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();

            if (customerBO.deleteCustomer(connection, new CustomerDTO(req.getParameter("customerID")))) {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Customer Deleted Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Customer Deleted Successfully...", ""));

            } else {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Invalid Customer ID...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid Customer ID...", ""));
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            /*responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Deleting...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();*/
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Occurred While Deleting...", e.getLocalizedMessage()));
        }
    }
}
