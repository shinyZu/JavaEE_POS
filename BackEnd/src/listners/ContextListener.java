package listners;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Context Initialized");

        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        bds.setUrl("jdbc:mysql://localhost:3306/JavaEE_POS");
        bds.setUsername("root");
        bds.setPassword("shiny1234");
        bds.setMaxTotal(100);
        bds.setInitialSize(100);

        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute("bds",bds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Context Destroyed...");
        try {
            ServletContext servletContext = servletContextEvent.getServletContext();
            BasicDataSource bds = (BasicDataSource) servletContext.getAttribute("bds");
            bds.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
