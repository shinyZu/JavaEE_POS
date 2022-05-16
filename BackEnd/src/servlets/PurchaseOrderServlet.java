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
                    //TODO
                    break;

                case "CHECK_FOR_DUPLICATE":
                    //TODO
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
        JsonObject jsonObject1 = reader.readObject();
        String orderId = jsonObject1.getString("orderId");
        String orderDate = jsonObject1.getString("date");
        String orderCost = jsonObject1.getString("subTotal");
        String discount = jsonObject1.getString("discount");
        String customerId = jsonObject1.getString("customerId");

        /*JsonObject jsonObject2 = jsonObject1.getJsonObject("orderDetail");
        System.out.println(jsonObject2);
        String itemCode = jsonObject2.getString("itemCode");
        String qty = jsonObject2.getString("qty");*/

        JsonArray orderDetails = jsonObject1.getJsonArray("orderDetail");
        System.out.println("orderDetails : "+orderDetails);

        try {
            System.out.println("inside try...");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            connection.setAutoCommit(false);

            PreparedStatement pstm1 = connection.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?,?)");

            pstm1.setObject(1, orderId);
            pstm1.setObject(2, orderDate);
            pstm1.setObject(3, String.format("%.2f", new Double(orderCost)));
            pstm1.setObject(4, Integer.parseInt(discount));
            pstm1.setObject(5, customerId);

            if (pstm1.executeUpdate() > 0) {

                for (JsonValue value : orderDetails) {
                    String itemCode = value.asJsonObject().getString("itemCode");
                    String qty = value.asJsonObject().getString("qty");

                    PreparedStatement pstm2 = connection.prepareStatement("INSERT INTO OrderDetails VALUES (?,?,?)");
                    pstm2.setObject(1, orderId);
                    pstm2.setObject(2, itemCode);
                    pstm2.setObject(3, Integer.parseInt(qty));

                    if (pstm2.executeUpdate() > 0) {
                        connection.commit();
                        responseInfo = Json.createObjectBuilder();
                        resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Order Saved Successfully...");
                        responseInfo.add("data", "");
                        resp.getWriter().print(responseInfo.build());

                    } else {
                        connection.rollback();
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 400);
                        responseInfo.add("message", "Rollback1");
                        responseInfo.add("data", "");
                        resp.getWriter().print(responseInfo.build());
                        return;
                    }
                }
                connection.commit();
                responseInfo = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
            } else {
                connection.rollback();
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Rollback2");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
                return;
            }

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 400);
            responseInfo.add("message", "Something Went Wrong...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Purchase Order DELETE method....");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Orders WHERE orderId = ?");
            pstm.setObject(1,req.getParameter("orderId"));

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Deleted Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Couldn't Delete Order..");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
            }
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Deleting...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();
        }
    }
}
