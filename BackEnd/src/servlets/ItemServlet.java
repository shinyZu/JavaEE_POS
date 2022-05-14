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
                    break;

                case "GET_COUNT":
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
                        item.add("itemCode",rst.getString(1));
                        item.add("description",rst.getString(2));
                        item.add("unitPrice",rst.getDouble(3));
                        item.add("qtyOnHand",rst.getInt(4));

                        allItems.add(item.build());
                    }
                    responseInfo = Json.createObjectBuilder();
                    resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                    responseInfo.add("status",200);
                    responseInfo.add("message","Received All Items");
                    responseInfo.add("data",allItems.build());
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
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");

            pstm.setObject(1,req.getParameter("itemCode"));
            pstm.setObject(2,req.getParameter("description"));
            pstm.setObject(3,req.getParameter("unitPrice"));
            pstm.setObject(4,req.getParameter("quantity"));

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                responseInfo.add("status",200);
                responseInfo.add("message","Item Saved Successfully...");
                responseInfo.add("data","");
                resp.getWriter().print(responseInfo.build());
            }

        } catch (ClassNotFoundException | SQLException e) {
            responseInfo = Json.createObjectBuilder();
            responseInfo.add("status",400);
            responseInfo.add("message","Something Went Wrong...");
            responseInfo.add("data", e.getLocalizedMessage());
            resp.getWriter().print(responseInfo.build());
            e.printStackTrace();
        }
    }
}
