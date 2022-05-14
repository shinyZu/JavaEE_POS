package servlets;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Item's GET invoked...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");

            resp.setContentType("application/json");

            String option = req.getParameter("option");
            ResultSet rst;
            PreparedStatement pstm;

            JsonObjectBuilder item = Json.createObjectBuilder();
            JsonArrayBuilder allItems = Json.createArrayBuilder();

            switch (option) {
                case "SEARCH":
                    pstm = connection.prepareStatement("SELECT * FROM Item WHERE itemCode = ?");
                    pstm.setObject(1, req.getParameter("itemCode"));
                    rst = pstm.executeQuery();

                    while (rst.next()) {
                        item.add("itemCode", rst.getString(1));
                        item.add("description", rst.getString(2));
                        item.add("unitPrice", String.format("%.2f",rst.getDouble(3)));
                        item.add("qtyOnHand", rst.getString(4));
                    }

                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Item Search Done");
                    responseInfo.add("data", item.build());
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "GET_COUNT":
                    rst = connection.prepareStatement("SELECT COUNT(itemCode) FROM Item").executeQuery();

                    if (rst.next()) {
                        responseInfo = Json.createObjectBuilder();
                        responseInfo.add("status", 200);
                        responseInfo.add("message", "Items Counted");
                        responseInfo.add("data", rst.getString(1));
                    }
                    resp.getWriter().print(responseInfo.build());
                    break;

                case "LAST_CODE":
                    break;

                case "GET_CODE_DESCRIP":
                    rst = connection.prepareStatement("SELECT itemCode, description FROM Item").executeQuery();
                    while (rst.next()) {

                        item.add("itemCode", rst.getString(1));
                        item.add("description", rst.getString(2));

                        allItems.add(item.build());
                    }
                    responseInfo = Json.createObjectBuilder();
                    responseInfo.add("data", allItems.build());
                    responseInfo.add("message", "Received Codes & Descriptions");
                    responseInfo.add("status", 200);
                    resp.getWriter().print(responseInfo.build());

                    break;

                case "GETALL":
                    rst = connection.prepareStatement("SELECT * FROM Item").executeQuery();
                    while (rst.next()) {
                        item.add("itemCode", rst.getString(1));
                        item.add("description", rst.getString(2));
                        item.add("unitPrice", String.format("%.2f",rst.getDouble(3)));
                        item.add("qtyOnHand", rst.getInt(4));

//                        System.out.println("BigDecimal : "+ rst.getBigDecimal(3)); // 44.0
//                        System.out.println("Double : "+ rst.getDouble(3)); // 44.0
//                        System.out.println("Double Format : "+ String.format("%.2f",rst.getDouble(3))); // 44.00
//                        System.out.println("String : "+ rst.getString(3)); // 44.0
//                        System.out.println("Float : "+ rst.getFloat(3)); // 44.0
                        allItems.add(item.build());
                    }

                    responseInfo = Json.createObjectBuilder();
                    resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                    responseInfo.add("status", 200);
                    responseInfo.add("message", "Received All Items");
                    responseInfo.add("data", allItems.build());
                    resp.getWriter().print(responseInfo.build());
                    break;
            }
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Item's POST invoked...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");

            pstm.setObject(1, req.getParameter("itemCode"));
            pstm.setObject(2, req.getParameter("description"));
            pstm.setObject(3, String.format("%.2f", new Double(req.getParameter("unitPrice"))));
            pstm.setObject(4, req.getParameter("quantity"));

//            String.format("%.2f", new BigDecimal(f) // 0.00
//            String.format("%.2f", new Double(req.getParameter("unitPrice")) // 0.00

//            pstm.setDouble(3, Double.parseDouble(req.getParameter("unitPrice")));
//            pstm.setFloat(3, Float.parseFloat((req.getParameter("unitPrice"))));
//            pstm.setObject(3, String.format("%.2f", new Double(req.getParameter("unitPrice"))));
//            pstm.setObject(3, new BigDecimal(req.getParameter("unitPrice")));

//            System.out.println("parseDouble : "+Double.parseDouble(req.getParameter("unitPrice"))); // 75 --> 75.0,   75.05 --> 75.05
//            System.out.println("parseFloat : "+Float.parseFloat(req.getParameter("unitPrice"))); // 75 --> 75.0,   75.05 --> 75.05
//            System.out.println("String Format : "+String.format("%.2f", new Double(req.getParameter("unitPrice")))); // 75 --> 75.00,   75.05 --> 75.05


            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                responseInfo.add("status", 200);
                responseInfo.add("message", "Item Saved Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Item's PUT invoked...");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String itemCode = jsonObject.getString("itemCode");
        String description = jsonObject.getString("description");
        String unitPrice = jsonObject.getString("unitPrice");
        String qtyOnHand = jsonObject.getString("qty");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root",
                    "shiny1234");

            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?");
            pstm.setObject(1, description);
            pstm.setObject(2, String.format("%.2f", new Double(unitPrice)));
            pstm.setObject(3, qtyOnHand);
            pstm.setObject(4, itemCode);

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "Item Updated Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Error Occurred While Updating...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());
            }

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status", 500);
            responseInfo.add("message", "Error Occurred While Updating...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Item's DELETE invoked...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Item WHERE itemCode = ?");
            pstm.setObject(1,req.getParameter("itemCode"));

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Item Deleted Successfully...");
                responseInfo.add("status", 200);
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("data", "");
                responseInfo.add("message", "Invalid Item Code...");
                responseInfo.add("status", 200);
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
