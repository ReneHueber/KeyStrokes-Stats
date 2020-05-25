package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.Component;
import objects.Heatmap;
import objects.Keyboard;
import objects.TotalToday;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReadDb {
    private static final String url = WriteDb.url;

    /**
     * Reads the all values for the keyboards Table.
     * @param sqlStatements What values should be read
     * @return An ObservableList<Keyboards> with all the Keyboards from the Db
     */
    public static ObservableList<Keyboard> selectAllValuesKeyboard(String sqlStatements){
        try (Connection conn = ConnectDb.connect(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatements)){
                ObservableList<Keyboard> keyboardList = FXCollections.observableArrayList();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("keyboardName");
                    String type = rs.getString("keyboardType");
                    String layout = rs.getString("layout");
                    int totKeyStrokes = rs.getInt("totKeystrokes");
                    float totTimePressed = rs.getFloat("totTimePressed");
                    String usedSince = rs.getString("usedSince");
                    String lastUsed = rs.getString("lastUsed");

                    keyboardList.add(new Keyboard(name, type, totKeyStrokes, totTimePressed, lastUsed,
                            usedSince, layout, id));
            }
                return keyboardList;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Reads the all values for the total Today Table.
     * @param sqlStmt What values should be read
     * @return An Arraylist<TotalToday> with all the Values from the Db
     */
    public static ArrayList<TotalToday> selectAllValuesTotalToday(String sqlStmt){
        ArrayList<TotalToday> keyboardEntrance = new ArrayList<>();

        try (Connection conn = ConnectDb.connect(url);
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


    /**
     * Read all the Values for the Heatmap table.
     * @param sqlStmt What values should be read.
     * @return An ArrayList<Heatmap> with all the Heatmap values form the Db.
     */
    public static ArrayList<Heatmap> selectAllValueHeatmapTable(String sqlStmt){
        ArrayList<Heatmap> heatmapValues = new ArrayList<>();

        try (Connection conn = ConnectDb.connect(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStmt)){

            while (rs.next()){
                int keyboardId = rs.getInt("keyboardId");
                String date = rs.getString("date");
                String key = rs.getString("key");
                int timesPressed = rs.getInt("pressed");

                heatmapValues.add(new Heatmap(keyboardId, LocalDate.parse(date), key, timesPressed));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return heatmapValues;
    }

    /**
     * Sum's the Keystrokes form the totalToday Table since a specific Date.
     * @param sqlStmt Sql Statement with the selected start Date.
     * @return The Total Keystrokes since the specific Date.
     */
    public static int sumDateSpecificKeyStrokes(String sqlStmt){
        try(Connection conn = ConnectDb.connect(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStmt)){
            return rs.getInt(1);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Read all the Values for the Components table.
     * @param sqlStmt What values should be read.
     * @return An ObservableList<Component> with all the Component values form the Db.
     */
    public static ObservableList<Component> selectAllValuesComponents(String sqlStmt){
        ObservableList<Component> components = FXCollections.observableArrayList();

        try(Connection conn = ConnectDb.connect(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStmt)){

            while (rs.next()){
                int id = rs.getInt("id");
                int keyboardId = rs.getInt("keyboardId");
                String componentType = rs.getString("componentType");
                String componentName = rs.getString("componentName");
                String componentBrand = rs.getString("componentBrand");
                float keyPressure = rs.getFloat("keyPressure");
                int keyTravel = rs.getInt("keyTravel");
                int keyStrokes = rs.getInt("keyStrokes");
                String addDate = rs.getString("addDate");
                boolean isActive = rs.getBoolean("isActive");

                components.add(new Component(id, keyboardId, componentType, componentName, componentBrand,
                                keyPressure, keyTravel, addDate, "", keyStrokes, isActive));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return components;
    }
}
