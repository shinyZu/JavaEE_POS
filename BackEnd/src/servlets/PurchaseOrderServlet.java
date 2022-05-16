package servlets;

import javax.json.*;
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
                        order.add("orderCost", rst.getDouble(3));
                        order.add("discount", rst.getDouble(4));
                        order.add("customerId", rst.getString(5));

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

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Purchase Order POST method....");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String orderId = jsonObject.getString("orderId");
        String orderDate = jsonObject.getString("date");
        String orderCost = jsonObject.getString("subTotal");
        String discount = jsonObject.getString("discount");
        String customerId = jsonObject.getString("customerId");
//        String commit = jsonObject.getString("setAutoCommit");




        try {
            System.out.println("inside try...");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            /*boolean autoCommit = connection.getAutoCommit();
            System.out.println("autoCommit : "+autoCommit);

            if (commit.equals("TRUE")) {
                connection.commit();
                responseInfo = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
                return;
            }*/

//            if (commit.equals("FALSE")) {
//                connection.setAutoCommit(false);

                PreparedStatement pstm = connection.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?,?)");

                pstm.setObject(1, orderId);
                pstm.setObject(2, orderDate);
                pstm.setObject(3, String.format("%.2f", new Double(orderCost)));
                pstm.setObject(4, Integer.parseInt(discount));
                pstm.setObject(5, customerId);

                if (pstm.executeUpdate() > 0) {
                    responseInfo = Json.createObjectBuilder();
//                    resp.setStatus(HttpServletResponse.SC_ACCEPTED); // 202
                    resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                    responseInfo.add("status", 200);
//                    responseInfo.add("message", "O_ACCEPTED");
                    responseInfo.add("message", "Order Saved Successfully...");
                    responseInfo.add("data", "");
                    resp.getWriter().print(responseInfo.build());
                }
//            }

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 400);
            responseInfo.add("message", "Errorrrrrrrrrrrrrrrrrrrrrrrrr...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();
        }
    }
}
