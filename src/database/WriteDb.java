package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WriteDb {

    public static void createNewDb(String dbName, String dbPath){
        String url = "jdbc:sqlite:" + dbPath + dbName;

        try(Connection conn = DriverManager.getConnection(url)){
            if (conn != null){
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
