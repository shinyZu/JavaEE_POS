package servlets;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/orderDetails")
public class OrderDetailServlet extends HttpServlet {
    JsonObjectBuilder responseInfo;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Detail's GET invoked...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");

            resp.setContentType("application/json");

            String option = req.getParameter("option");
//            ResultSet rst;
//            PreparedStatement pstm;

            JsonObjectBuilder detail = Json.createObjectBuilder();
            JsonArrayBuilder allDetails = Json.createArrayBuilder();

            ResultSet rst = connection.prepareStatement("SELECT * FROM OrderDetails").executeQuery();

            while (rst.next()) {
                detail.add("orderId", rst.getString(1));
                detail.add("itemCode", rst.getString(2));
                detail.add("orderQty", rst.getInt(3));
                allDetails.add(detail.build());
            }

            responseInfo = Json.createObjectBuilder();
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            responseInfo.add("status", 200);
            responseInfo.add("message", "Received All Details");
            responseInfo.add("data", allDetails.build());
            resp.getWriter().print(responseInfo.build());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Detail's POST invoked...");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String orderId = jsonObject.getString("orderId");
        String itemCode = jsonObject.getString("itemCode");
        String orderQty = jsonObject.getString("orderQty");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            System.out.println("details commit status : "+connection.getAutoCommit());

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO OrderDetails VALUES (?,?,?)");

            System.out.println(orderId+" "+itemCode+" "+orderQty);

            pstm.setObject(1, orderId);
            pstm.setObject(2, itemCode);
            pstm.setObject(3, orderQty);

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
//                resp.setStatus(HttpServletResponse.SC_ACCEPTED); // 202
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                responseInfo.add("status", 200);
                responseInfo.add("message", "Order Detail Saved Successfully...");
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Detail's DELETE invoked...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS", "root", "shiny1234");
            resp.setContentType("application/json");

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM OrderDetails WHERE orderId = ?");
            pstm.setObject(1,req.getParameter("orderId"));

            if (pstm.executeUpdate() > 0) {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 200);
                responseInfo.add("message", "OrderDetails Deleted Successfully...");
                responseInfo.add("data", "");
                resp.getWriter().print(responseInfo.build());

            } else {
                responseInfo = Json.createObjectBuilder();
                responseInfo.add("status", 400);
                responseInfo.add("message", "Couldn't Delete Order Details..");
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
