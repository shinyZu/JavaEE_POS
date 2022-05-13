package servlets;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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

            switch (option) {
                case "SEARCH":
                    PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE customerId = ?");
                    pstm.setObject(1, req.getParameter("customerID"));
                    rst = pstm.executeQuery();

                    while (rst.next()) {
                        customer.add("id", rst.getString(1));
                        customer.add("name", rst.getString(2));
                        customer.add("address", rst.getString(3));
                        customer.add("salary", rst.getString(4));
                    }
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Search Done");
                    responseInfo.add("data", customer.build());
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
                    responseInfo.add("message", "All IDs Done");
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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
