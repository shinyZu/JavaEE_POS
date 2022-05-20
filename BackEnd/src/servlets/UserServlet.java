package servlets;

import business.BOFactory;
import business.custom.UserBO;
import dto.UserDetailsDTO;
import util.JsonUtil;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
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

@WebServlet(urlPatterns = "/user")
public class UserServlet extends HttpServlet {

    private final UserBO userBO = (UserBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.USER);
    @Resource(name = "java:comp/env/jdbc/pos")
    DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // admin@gmail.com
        /*String email = req.getParameter("email");
        String pwd = req.getParameter("pwd");
        System.out.println("1 : "+ email + " " + pwd);*/

        JsonObjectBuilder details = Json.createObjectBuilder();
        try {
            Connection connection = ds.getConnection();

            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(req.getParameter("email"), req.getParameter("pwd"));
            ArrayList<UserDetailsDTO> userDetails = userBO.getDetails(connection, userDetailsDTO);

            if (userDetails != null) {
                for (UserDetailsDTO dto : userDetails) {
                    details.add("email", dto.getEmail());
                    details.add("pwd", dto.getPassword());
                }
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "UserDetails Retrieved", details.build()));
            }
            /*PreparedStatement pstm = connection.prepareStatement("SELECT * FROM UserDetails WHERE email = ?");
            pstm.setObject(1,email);
            ResultSet rst = pstm.executeQuery();

            if (rst.next()) {
                email = rst.getString(1);
                pwd = rst.getString(2);
                System.out.println("2 : "+email+" : "+pwd);

                details.add("email",email);
                details.add("pwd",pwd);
            }
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "UserDetails Retrieved", details.build()));*/
            connection.close();

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*String email = req.getParameter("email");
        String pwd = req.getParameter("pwd");
        System.out.println("1 : "+ email + " " + pwd);*/

        try {
            Connection connection = ds.getConnection();

            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                    req.getParameter("email"),
                    req.getParameter("pwd")
            );

            if (userBO.addUser(connection, userDetailsDTO)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "User Saved Successfully...", "SUCCESS"));
            }

            /*PreparedStatement pstm = connection.prepareStatement("INSERT INTO UserDetails VALUES (?,?)");
            pstm.setObject(1,email);
            pstm.setObject(2,pwd);

            if (pstm.executeUpdate() > 0) {
                resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_OK, "SUCCESS", ""));
            }*/

            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            resp.getWriter().print(JsonUtil.generateResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error", e.getLocalizedMessage()));
//            e.printStackTrace();
        }
    }
}
