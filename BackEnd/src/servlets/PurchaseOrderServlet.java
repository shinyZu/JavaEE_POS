package servlets;

import business.BOFactory;
import business.custom.PurchaseOrderBO;
import business.custom.impl.PurchaseOrderBOImpl;
import dto.OrderDTO;
import dto.OrderDetailDTO;
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

@WebServlet(urlPatterns = "/orders")
public class PurchaseOrderServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pos")
    DataSource ds;

    PurchaseOrderBO purchaseOrderBO = (PurchaseOrderBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.PURCHASE_ORDER);
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();

            JsonArrayBuilder allOrders = Json.createArrayBuilder();
            JsonObjectBuilder order = Json.createObjectBuilder();

            String option = req.getParameter("option");

            switch (option) {
                case "GET_COUNT":
                    String orderCount = purchaseOrderBO.getOrderCount(connection);
                    /*responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Orders Counted");
                    responseInfo.add("data", orderCount);
                    resp.getWriter().print(responseInfo.build());*/
                    resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Orders Counted", orderCount));
                    break;

                case "LAST_ID":
                    String lastId = purchaseOrderBO.getLastId(connection);
                    /*responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);*/

                    if (lastId != null) {
                        /*responseInfo.add("message", "Retrieved Last OrderId...");
                        responseInfo.add("data", lastId);*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Retrieved Last OrderId...", lastId));

                    } else {
                        /*responseInfo.add("message", "No any Orders yet");
                        responseInfo.add("data", "null");*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "No any Orders yet", "null"));
                    }
//                    resp.getWriter().print(responseInfo.build());
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
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", HttpServletResponse.SC_OK); // 200
                        responseInfo.add("message", "Received All Orders");
                        responseInfo.add("data", allOrders.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Received All Orders", allOrders.build()));
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
                        /*responseInfo = Json.createObjectBuilder();
                        resp.setStatus(HttpServletResponse.SC_OK); // 200
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Received All Details");
                        responseInfo.add("data", allDetails.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Received All Details", allDetails.build()));
                    }
                    break;
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Occurred", e.getLocalizedMessage()));
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

            if (purchaseOrderBO.purchaseOrder(connection, orderDTO, orderDetails)) {
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Order Saved Successfully...", ""));

            } else {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Couldn't Save Order...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Couldn't Save Order...", ""));
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            /*responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 400);
            responseInfo.add("message", "Something Went Wrong...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();*/
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Something Went Wrong...", e.getLocalizedMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();

            if (purchaseOrderBO.deleteOrder(connection, new OrderDTO(req.getParameter("orderId")))) {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Deleted Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Order Deleted Successfully...", ""));

            } else {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Couldn't Delete Order..");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Couldn't Delete Order..", ""));
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
