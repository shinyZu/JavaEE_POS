package servlets;

import business.custom.PurchaseOrderBO;
import business.custom.impl.PurchaseOrderBOImpl;
import dto.OrderDTO;
import dto.OrderDetailDTO;

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

@WebServlet(urlPatterns = "/orders")
public class PurchaseOrderServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pos")
    DataSource ds;

    PurchaseOrderBO purchaseOrderBO = new PurchaseOrderBOImpl();
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();
            resp.setContentType("application/json");

            JsonArrayBuilder allOrders = Json.createArrayBuilder();
            JsonObjectBuilder order = Json.createObjectBuilder();

            String option = req.getParameter("option");

            switch (option) {
                case "GET_COUNT":
                    String orderCount = purchaseOrderBO.getOrderCount(connection);
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Orders Counted");
                    responseInfo.add("data", orderCount);
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "LAST_ID":
                    String lastId = purchaseOrderBO.getLastId(connection);
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);

                    if (lastId != null) {
                        responseInfo.add("message", "Retrieved Last OrderId...");
                        responseInfo.add("data", lastId);

                    } else {
                        responseInfo.add("message", "No any Orders yet");
                        responseInfo.add("data", "null");
                    }
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GETALL":
                    ArrayList<OrderDTO> orders = purchaseOrderBO.getAllOrders(connection);
                    if (orders != null) {
                        for (OrderDTO dto : orders) {
                            order.add("orderId", dto.getOrderId());
                            order.add("orderDate", String.valueOf(dto.getOrderDate()));
                            order.add("orderCost", dto.getOrderCost());
                            order.add("discount", dto.getDiscount());
                            order.add("customerId", dto.getCustomerId());

                            allOrders.add(order.build());
                        }
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", HttpServletResponse.SC_OK); // 200
                        responseInfo.add("message", "Received All Orders");
                        responseInfo.add("data", allOrders.build());
                        resp.getWriter().print(responseInfo.build());
                    }
                    break;

                case "GET_DETAILS":
                    JsonObjectBuilder detail = Json.createObjectBuilder();
                    JsonArrayBuilder allDetails = Json.createArrayBuilder();

                    ArrayList<OrderDetailDTO> orderDetails = purchaseOrderBO.getOrderDetails(connection);

                    if (orderDetails != null) {
                        for (OrderDetailDTO dto : orderDetails) {
                            detail.add("orderId", dto.getOrderId());
                            detail.add("itemCode", dto.getItemCode());
                            detail.add("orderQty", dto.getOrderQty());
                            allDetails.add(detail.build());
                        }
                        responseInfo = Json.createObjectBuilder();
                        resp.setStatus(HttpServletResponse.SC_OK); // 200
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Received All Details");
                        responseInfo.add("data", allDetails.build());
                        resp.getWriter().print(responseInfo.build());
                    }
                    break;
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject1 = reader.readObject();

        String orderId = jsonObject1.getString("orderId");
        String orderDate = jsonObject1.getString("date");
        String orderCost = jsonObject1.getString("subTotal");
        String discount = jsonObject1.getString("discount");
        String customerId = jsonObject1.getString("customerId");
        JsonArray orderDetails = jsonObject1.getJsonArray("orderDetail");

        OrderDTO orderDTO = new OrderDTO(
                orderId,
                java.sql.Date.valueOf(orderDate),
                Double.parseDouble(orderCost),
                Integer.parseInt(discount),
                customerId
        );
        try {
            Connection connection = ds.getConnection();
            resp.setContentType("application/json");

            if (purchaseOrderBO.purchaseOrder(connection, orderDTO, orderDetails)) {
//            if (purchaseOrderBO.purchaseOrder(orderDTO, orderDetails)) {
                responseInfo = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Couldn't Save Order...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
            }
            /*PreparedStatement pstm1 = connection.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?,?)");

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

                    if (pstm2.executeUpdate() <= 0) {
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
            }*/
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
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
        try {
            Connection connection = ds.getConnection();
            resp.setContentType("application/json");

            if (purchaseOrderBO.deleteOrder(connection, new OrderDTO(req.getParameter("orderId")))) {
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

           /* PreparedStatement pstm = connection.prepareStatement("DELETE FROM Orders WHERE orderId = ?");
            pstm.setObject(1, req.getParameter("orderId"));

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
            }*/
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
