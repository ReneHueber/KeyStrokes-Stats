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

    public void selectAll(){
        String sql = "SELECT keystrokes FROM KeyLogs";

        try (Connection conn = this.connect()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                System.out.println(rs.getInt("keystrokes"));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
