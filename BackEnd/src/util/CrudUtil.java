package util;

import listners.ContextListener;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudUtil {

    private static PreparedStatement getPreparedStatement(Connection conn, String sql, Object... args) throws SQLException, ClassNotFoundException {
//        System.out.println(7);
        PreparedStatement pstm = conn.prepareStatement(sql);

        for (int i = 0; i < args.length; i++) {
            pstm.setObject(i+1,args[i]);
        }
//        System.out.println(8);
//        conn.close();
//        System.out.println(9);
        return pstm;
    }

    public static boolean executeUpdate(Connection conn, String sql, Object... args) throws SQLException, ClassNotFoundException {
        return getPreparedStatement(conn, sql,args).executeUpdate() > 0;
    }

    public static ResultSet executeQuery(Connection conn, String sql, Object... args) throws SQLException, ClassNotFoundException {
//        System.out.println(6);
        return getPreparedStatement(conn, sql,args).executeQuery();
    }
}
