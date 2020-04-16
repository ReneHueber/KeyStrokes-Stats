package keylogger;

import java.sql.*;

public class DbConnection {
    private String dbPath;

    public DbConnection(String dbPath){
        this.dbPath = dbPath;
    }

    public Connection connect(){
        Connection conn = null;
        String url = "jdbc:sqlite:" + dbPath;
        try{

            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public int getSum(){
        String sql = "SELECT SUM(keystrokes) FROM KeyLogs";
        int result = 0;

        try (Connection conn = this.connect()){
            Statement stmt = conn.createStatement();
            ResultSet sum = stmt.executeQuery(sql);

            result = sum.getInt(1);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return result;
    }

}
