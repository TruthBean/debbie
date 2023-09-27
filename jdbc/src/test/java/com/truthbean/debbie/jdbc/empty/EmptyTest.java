package com.truthbean.debbie.jdbc.empty;

import java.sql.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class EmptyTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.truthbean.debbie.jdbc.mock.MockDriver");

        Connection connection = DriverManager.getConnection("jdbc:debbie://localhost/mock", "root", "debbie");
        PreparedStatement statement = connection.prepareStatement("select * from test");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(0));
        }
    }
}
