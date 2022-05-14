package servlets;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET method invoked....");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root",
                    "shiny1234");
            resp.setContentType("application/json");

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            JsonObjectBuilder customer = Json.createObjectBuilder();

            String option = req.getParameter("option");
            ResultSet rst;
            PreparedStatement pstm;

            switch (option) {
                case "SEARCH":
                    pstm = connection.prepareStatement("SELECT * FROM Customer WHERE customerId = ?");
                    pstm.setObject(1, req.getParameter("customerID"));
                    rst = pstm.executeQuery();

                    while (rst.next()) {
                        customer.add("id", rst.getString(1));
                        customer.add("name", rst.getString(2));
                        customer.add("address", rst.getString(3));
                        customer.add("contact", rst.getString(4));
                    }
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Search Done");
                    responseInfo.add("data", customer.build());
                    resp.getWriter().print(responseInfo.build());

                    break;

                case "GET_COUNT":
                    rst = connection.prepareStatement("SELECT COUNT(customerId) FROM Customer").executeQuery();

                    if (rst.next()) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Customers Counted");
                        responseInfo.add("data", rst.getString(1));
                    }
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "LAST_ID":
                    rst = connection.prepareStatement("SELECT customerId FROM Customer ORDER BY customerId DESC LIMIT 1").executeQuery();

                    if (rst.next()) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Retrieved Last CustomerId...");
                        responseInfo.add("data", rst.getString(1));
                    }
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GET_ID_NAME":
                    rst = connection.prepareStatement("SELECT customerId, customerName FROM Customer").executeQuery();
                    while (rst.next()) {

                        customer.add("id", rst.getString(1));
                        customer.add("name", rst.getString(2));

                        allCustomers.add(customer.build());
                    }
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("data", allCustomers.build());
                    responseInfo.add("message", "Received all IDs & Names");
                    responseInfo.add("status", 200);

                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GETALL":
                    rst = connection.prepareStatement("SELECT * FROM Customer").executeQuery();
                    while (rst.next()) {
                        // String id = rst.getString(1);
                        // String name = rst.getString(2);
                        // String address = rst.getString(3);
                        // int contact = rst.getInt(4);
                        // resp.getWriter().write(id+" "+name+" "+address+" "+contact);

                        customer.add("id", rst.getString(1));
                        customer.add("name", rst.getString(2));
                        customer.add("address", rst.getString(3));
                        customer.add("contact", rst.getInt(4));

                        allCustomers.add(customer.build());
                    }
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("data", allCustomers.build());
                    responseInfo.add("message", "Done");
                    responseInfo.add("status", HttpServletResponse.SC_OK); // 200

                    resp.getWriter().print(responseInfo.build());
                    break;

            }
            connection.close();

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST method invoked....");

        String id = req.getParameter("customerID");
        String name = req.getParameter("customerName");
        String address = req.getParameter("customerAddress");
        String contact = req.getParameter("customerContact");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root",
                    "shiny1234");
            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");
            pstm.setObject(1, id);
            pstm.setObject(2, name);
            pstm.setObject(3, address);
            pstm.setObject(4, contact);

            if (pstm.executeUpdate() > 0) {
                System.out.println("Customer Added Successfully....");
                // resp.getWriter().write("Customer Added Successfully....");

                resp.setStatus(HttpServletResponse.SC_CREATED);// 201

                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Customer Saved Successfully...");
                responseInfo.add("status", 200);

                resp.getWriter().print(responseInfo.build());
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException throwables) {
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

        resp.setContentType("application/json");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root",
                    "shiny1234");

            PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET customerName=?,customerAddress=?,customerContact=? where customerId=?");
            pstm.setObject(1, customerName);
            pstm.setObject(2, customerAddress);
            pstm.setObject(3, customerContact);
            pstm.setObject(4, customerID);


            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", 200);
                objectBuilder.add("message", "Customer Updated Successfully...");
                objectBuilder.add("data", "");
                resp.getWriter().print(objectBuilder.build());

            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", 400);
                objectBuilder.add("message", "Error Occurred While Updating...");
                objectBuilder.add("data", "");
                resp.getWriter().print(objectBuilder.build());
            }

            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Updating...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("DELETE method invoked......");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE customerId = ?");
            pstm.setObject(1,req.getParameter("customerID"));

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Customer Deleted Successfully...");
                responseInfo.add("status", 200);
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Invalid Customer ID...");
                responseInfo.add("status", 200);
                resp.getWriter().print(responseInfo.build());
            }
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Updating...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();

        }
    }
}
