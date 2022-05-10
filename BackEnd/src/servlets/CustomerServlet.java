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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET method invoked....");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS","root","shiny1234");
            ResultSet rst = connection.prepareStatement("SELECT * FROM Customer").executeQuery();

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            JsonObjectBuilder customer = Json.createObjectBuilder();

            while (rst.next()){
//                String id = rst.getString(1);
//                String name = rst.getString(2);
//                String address = rst.getString(3);
//                int contact = rst.getInt(4);
//                resp.getWriter().write(id+" "+name+" "+address+" "+contact);

                customer.add("id",rst.getString(1));
                customer.add("name",rst.getString(2));
                customer.add("address",rst.getString(3));
                customer.add("contact",rst.getInt(4));

                allCustomers.add(customer.build());
            }
            JsonObjectBuilder responseInfo = Json.createObjectBuilder();
            responseInfo.add("data",allCustomers.build());
            responseInfo.add("message","Done");
            responseInfo.add("status",HttpServletResponse.SC_OK); //200

            resp.getWriter().print(responseInfo.build());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST method invoked....");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaEE_POS","root","shiny1234");
//            connection.prepareStatement("INSERT INTO Customer VALUES ()")
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
