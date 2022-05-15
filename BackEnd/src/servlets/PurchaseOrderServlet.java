package servlets;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/orders")
public class PurchaseOrderServlet extends HttpServlet {
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Purchase Order GET method....");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root",
                    "shiny1234");
            resp.setContentType("application/json");

            JsonArrayBuilder allOrders = Json.createArrayBuilder();
            JsonObjectBuilder order = Json.createObjectBuilder();

            String option = req.getParameter("option");
            ResultSet rst;
            PreparedStatement pstm;

            switch (option) {
                case "SEARCH":

                    break;

                case "CHECK_FOR_DUPLICATE":

                    break;

                case "GET_COUNT":
                    rst = connection.prepareStatement("SELECT COUNT(orderId) FROM Orders").executeQuery();

                    if (rst.next()) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Orders Counted");
                        responseInfo.add("data", rst.getString(1));
                    }
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "LAST_ID":
                    rst = connection.prepareStatement("SELECT orderId FROM Orders ORDER BY orderId DESC LIMIT 1").executeQuery();

                    if (rst.next()) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Retrieved Last OrderId...");
                        responseInfo.add("data", rst.getString(1));
                        resp.getWriter().print(responseInfo.build());
                    } else {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "No any Orders yet");
                        responseInfo.add("data", "null");
                        resp.getWriter().print(responseInfo.build());
                    }
                    break;


                case "GETALL":
                    rst = connection.prepareStatement("SELECT * FROM Orders").executeQuery();
                    while (rst.next()) {

                        order.add("orderId", rst.getString(1));
                        order.add("orderDate", rst.getString(2));
                        order.add("orderCost", rst.getString(3));
                        order.add("discount", rst.getInt(4));
                        order.add("customerId", rst.getInt(5));

                        allOrders.add(order.build());
                    }
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("data", allOrders.build());
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
}
