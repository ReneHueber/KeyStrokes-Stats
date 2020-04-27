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
     * @param url SQLite connection String
     * @return Connection to the Database
     */
    public static Connection connect(String url){
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Closes the Connection to the Db
     * @param conn Connection that should be closed
     */
    public static void closeConnection(Connection conn){
        try{
            conn.close();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
