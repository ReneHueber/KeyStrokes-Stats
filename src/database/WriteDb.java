package database;

import java.sql.*;

public class WriteDb {
    protected static String url;
    /**
     * Creates a new Database
     * @param dbName Name of the new Db
     * @param dbPath Path of the new Db
     */
    public static void createNewDb(String dbName, String dbPath){
        String url = "jdbc:sqlite:" + dbPath + dbName;
        WriteDb.url = url;

        // create a new Database an closes the connection
        try(Connection conn = DriverManager.getConnection(url)){
            if (conn != null){
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                conn.close();
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates a new Table, in the Db that has been created before.
     * @param sqlStatement SQL Statement for the New Table
     */
    public static void createNewTable(String sqlStatement){
        try (Connection conn = ConnectDb.connect(url); Statement stmt = conn.createStatement()){
            stmt.execute(sqlStatement);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserts Values into an Table
     * @param sqlStatement Sql Statement for the Table
     * @param values Required Parameters for the Sql Statement
     */
    public static void insertIntoTable(String sqlStatement, String ... values){
        try (Connection conn = ConnectDb.connect(WriteDb.url); PreparedStatement prepStatement =
                conn.prepareStatement(sqlStatement)){
            // goes throw all the passed Parameter's
            for(int i = 0; i < values.length; i++){
                prepStatement.setString(i + 1, values[i]);
            }
            prepStatement.execute();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
