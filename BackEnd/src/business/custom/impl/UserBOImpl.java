package business.custom.impl;

import business.custom.UserBO;
import dao.DAOFactory;
import dao.custom.CustomerDAO;
import dao.custom.UserDAO;
import dto.CustomerDTO;
import dto.UserDetailsDTO;
import entity.Customer;
import entity.UserDetails;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserBOImpl implements UserBO {
    UserDAO userDAO = (UserDAO) DAOFactory.getDAOFactoryInstance().getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public ArrayList<UserDetailsDTO> getDetails(Connection connection, UserDetailsDTO dto) throws SQLException, ClassNotFoundException {
        ArrayList<UserDetailsDTO> userDetails = new ArrayList<>();
        for (UserDetails u : userDAO.getUserDetails(connection, new UserDetails(dto.getEmail(), dto.getPassword()))) {
            userDetails.add(new UserDetailsDTO(
                    u.getEmail(),
                    u.getPassword()
            ));
        }
        return userDetails;
    }

    @Override
    public boolean addUser(Connection connection, UserDetailsDTO dto) throws SQLException, ClassNotFoundException {
        return userDAO.add(connection, new UserDetails(
                dto.getEmail(),
                dto.getPassword()
        ));
    }
}
