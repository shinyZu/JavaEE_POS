package servlets;

import business.BOFactory;
import business.custom.ItemBO;
import business.custom.impl.ItemBOImpl;
import dto.ItemDTO;
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

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pos")
    DataSource ds;

    ItemBO itemBO = (ItemBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.ITEM);
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();
            String option = req.getParameter("option");

            JsonObjectBuilder item = Json.createObjectBuilder();
            JsonArrayBuilder allItems = Json.createArrayBuilder();

            switch (option) {
                case "SEARCH":
                    String itemCode = req.getParameter("itemCode");
                    String description = req.getParameter("description");

                    ItemDTO itemDTO = new ItemDTO(req.getParameter("itemCode"), req.getParameter("description"));
                    ArrayList<ItemDTO> itemDetails = itemBO.searchItem(connection, itemDTO);

                    if (itemDetails != null) {
                        for (ItemDTO dto : itemDetails) {
                            item.add("itemCode", dto.getItemCode());
                            item.add("description", dto.getDescription());
                            item.add("unitPrice", String.format("%.2f", dto.getUnitPrice()));
                            item.add("qtyOnHand", dto.getQtyOnHand());
                        }
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Item Search With Code");
                        responseInfo.add("data", item.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Item Search With Code", item.build()));
                    }
                    break;

                case "GET_COUNT":
                    String itemCount = itemBO.getItemCount(connection);
                    /*responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Items Counted");
                    responseInfo.add("data", itemCount);
                    resp.getWriter().print(responseInfo.build());*/
                    resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Items Counted", itemCount));
                    break;

                case "LAST_CODE":
                    String lastCode = itemBO.getLastCode(connection);
                    /*responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);*/

                    if (lastCode != null) {
                        /*responseInfo.add("message", "Retrieved Last ItemCode...");
                        responseInfo.add("data", lastCode)*/;
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Retrieved Last ItemCode...", lastCode));

                    } else {
                        /*responseInfo.add("message", "No any Items yet");
                        responseInfo.add("data", "null");*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "No any Items yet", "null"));
                    }
//                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GET_CODE_DESCRIP":

                    ArrayList<ItemDTO> customerCodeDescriptions = itemBO.getCodeDescriptions(connection);

                    if (customerCodeDescriptions != null) {
                        for (ItemDTO dto : customerCodeDescriptions) {
                            item.add("itemCode", dto.getItemCode());
                            item.add("description", dto.getDescription());
                            allItems.add(item.build());
                        }
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Received Codes & Descriptions");
                        responseInfo.add("data", allItems.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Received Codes & Descriptions", allItems.build()));
                    }
                    break;

                case "GETALL":
                    ArrayList<ItemDTO> items = itemBO.getAllItems(connection);
                    if (items != null) {
                        for (ItemDTO dto : items) {
                            item.add("itemCode", dto.getItemCode());
                            item.add("description", dto.getDescription());
                            item.add("unitPrice", String.format("%.2f", dto.getUnitPrice()));
                            item.add("qtyOnHand", dto.getQtyOnHand());

                            /*System.out.println("BigDecimal : "+ rst.getBigDecimal(3)); // 44.0
                            System.out.println("Double : "+ rst.getDouble(3)); // 44.0
                            System.out.println("Double Format : "+ String.format("%.2f",rst.getDouble(3))); // 44.00
                            System.out.println("String : "+ rst.getString(3)); // 44.0
                            System.out.println("Float : "+ rst.getFloat(3)); // 44.0*/

                            allItems.add(item.build());
                        }
                        /*responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", HttpServletResponse.SC_OK); // 200
                        responseInfo.add("message", "Received All Items");
                        responseInfo.add("data", allItems.build());
                        resp.getWriter().print(responseInfo.build());*/
                        resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Received All Items", allItems.build()));
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
        ItemDTO itemDTO = new ItemDTO(
                req.getParameter("itemCode"),
                req.getParameter("description"),
                Double.parseDouble(req.getParameter("unitPrice")),
                Integer.parseInt(req.getParameter("quantity"))
        );
//        String.format("%.2f", new BigDecimal(f) // 0.00
//        String.format("%.2f", new Double(req.getParameter("unitPrice")) // 0.00
//
//        pstm.setDouble(3, Double.parseDouble(req.getParameter("unitPrice")));
//        pstm.setFloat(3, Float.parseFloat((req.getParameter("unitPrice"))));
//        pstm.setObject(3, String.format("%.2f", new Double(req.getParameter("unitPrice"))));
//        pstm.setObject(3, new BigDecimal(req.getParameter("unitPrice")));
//
//        System.out.println("parseDouble : " + Double.parseDouble(req.getParameter("unitPrice"))); // 75 --> 75.0,   75.05 --> 75.05
//        System.out.println("parseFloat : " + Float.parseFloat(req.getParameter("unitPrice"))); // 75 --> 75.0,   75.05 --> 75.05
//        System.out.println("String Format : " + String.format("%.2f", new Double(req.getParameter("unitPrice")))); // 75 --> 75.00,   75.05 --> 75.05
        try {
            Connection connection = ds.getConnection();

            if (itemBO.addItem(connection, itemDTO)) {
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Item Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Item Saved Successfully...", ""));
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        /*String itemCode = jsonObject.getString("itemCode");
        String description = jsonObject.getString("description");
        String unitPrice = jsonObject.getString("unitPrice");
        String qtyOnHand = jsonObject.getString("qty");*/

        ItemDTO itemDTO = new ItemDTO(
                jsonObject.getString("itemCode"),
                jsonObject.getString("description"),
                Double.parseDouble(jsonObject.getString("unitPrice")),
                Integer.parseInt(jsonObject.getString("qty"))
        );

        try {
            Connection connection = ds.getConnection();

            if (itemBO.updateItem(connection, itemDTO)) {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Item Updated Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Item Updated Successfully...", ""));

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
            resp.getWriter().print(responseInfo.build());*/
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Occurred While Updating...", ""));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();

            if (itemBO.deleteItem(connection, new ItemDTO(req.getParameter("itemCode")))) {
               /* responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Item Deleted Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "Item Deleted Successfully...", ""));

            } else {
                /*responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Invalid Item Code...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());*/
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid Item Code...", ""));
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
