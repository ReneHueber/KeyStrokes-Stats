package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.Keyboards;
import objects.TotalToday;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
     * Reads the all values for the keyboards Table.
     * @param sqlStatements What values should be read
     * @return An ObservableList<Keyboards> with all the Keyboards from the Db
     */
    public static ObservableList<Keyboards> selectAllValuesKeyboard(String sqlStatements){
        try (Connection conn = ConnectDb.connect(WriteDb.url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatements)){
                ObservableList<Keyboards> keyboardList = FXCollections.observableArrayList();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("keyboardName");
                    String type = rs.getString("keyboardType");
                    String layout = rs.getString("layout");
                    int totKeyStrokes = rs.getInt("totKeystrokes");
                    float totTimePressed = rs.getFloat("totTimePressed");
                    String usedSince = rs.getString("usedSince");
                    String lastUsed = rs.getString("lastUsed");

                    keyboardList.add(new Keyboards(name, type, totKeyStrokes, totTimePressed, lastUsed,
                            usedSince, layout, id));
            }
                return keyboardList;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static ArrayList<TotalToday> selectAllValuesTotalToday(String sqlStmt){
        ArrayList<TotalToday> keyboardEntrance = new ArrayList<>();

        try (Connection conn = ConnectDb.connect(WriteDb.url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStmt)){

            while (rs.next()){
                int keyboardId = rs.getInt("keyboardId");
                String date = rs.getString("date");
                int keyStrokes = rs.getInt("keyStrokes");
                float timePressed = rs.getFloat("timePressed");

                keyboardEntrance.add(new TotalToday(keyboardId, LocalDate.parse(date), keyStrokes, timePressed));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return keyboardEntrance;
    }
}
