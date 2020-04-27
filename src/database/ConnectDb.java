package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * All function that have to do with the Database connection.
 */
public class ConnectDb {

    /**
     * Creates the Connection to the Database
     * @param dbPath Path to the Database
     * @return Connection from the Database
     */
    public static Connection connect(String dbPath){
        Connection conn = null;
        String url = "jdbc:sqlite:" + dbPath;
        try{

            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
