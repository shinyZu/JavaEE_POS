package business.custom;

import business.SuperBO;
import dto.UserDetailsDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface UserBO extends SuperBO {
    ArrayList<UserDetailsDTO> getDetails(Connection connection, UserDetailsDTO userDetailsDTO) throws SQLException, ClassNotFoundException;

    boolean addUser(Connection connection, UserDetailsDTO userDetailsDTO) throws SQLException, ClassNotFoundException;

}
