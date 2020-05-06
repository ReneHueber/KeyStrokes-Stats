package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.Keyboards;

import java.sql.*;

public class ReadDb {
    private static final String url = WriteDb.url;

    public ReadDb(String dbPath){
    }

    public int getSum(){
        String sql = "SELECT SUM(keystrokes) FROM KeyLogs";
        int result = 0;

        try (Connection conn = ConnectDb.connect(url)){
            Statement stmt = conn.createStatement();
            ResultSet sum = stmt.executeQuery(sql);

            result = sum.getInt(1);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return result;
    }

    /**
     * Reads the values for the keyboards Table.
     * @param sqlStatements What values should be read
     * @return An ObservableList<Keyboards> with all the Keyboards from the Db
     */
    public static ObservableList<Keyboards> selectValuesKeyboard(String sqlStatements){
        try (Connection conn = ConnectDb.connect(WriteDb.url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatements)){
                ObservableList<Keyboards> keyboardList = FXCollections.observableArrayList();
                while (rs.next()){
                    String name = rs.getString("keyboardName");
                    String type = rs.getString("keyboardType");
                    String layout = rs.getString("layout");
                    int totKeyStrokes = rs.getInt("totKeystrokes");
                    float totTimePressed = rs.getFloat("totTimePressed");
                    String usedSince = rs.getString("usedSince");
                    String lastUsed = rs.getString("lastUsed");

                    // checks if the keyboard has been used before
                    if (lastUsed.equals("00.00.0000"))
                        lastUsed = "never";

                    keyboardList.add(new Keyboards(name, type, totKeyStrokes, totTimePressed, lastUsed,
                            usedSince, layout));
            }
                return keyboardList;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
