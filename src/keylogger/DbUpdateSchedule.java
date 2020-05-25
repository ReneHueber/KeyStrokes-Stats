package keylogger;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.ObservableList;
import objects.Component;
import objects.Heatmap;
import objects.Keyboard;
import objects.TotalToday;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Update the Database every 30 Seconds.
 */
public class DbUpdateSchedule {
    private final KeyLogger keyLogger;

    private final int keyboardId;
    private int oldTotal;
    private float oldTimePressed;
    private boolean createdThisRun = false;
    private boolean firstUpdate;

    private int componentsKeyStrokesStart = 0;
    private float componentTimeStart = 0.0f;

    private Timer t;

    public DbUpdateSchedule(KeyLogger keyLogger, int keyboardId){
        this.keyLogger = keyLogger;
        this.keyboardId = keyboardId;

        getTotalValuesKeyboard();
        firstUpdate = true;
    }

    /**
     * Starts the Schedule that updates the Db every 30 Seconds.
     */
    public void startSchedule(){
        t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                updateDb();
            }
        };

        t.scheduleAtFixedRate(tt, 30000, 30000);
    }

    public void stopSchedule(){
        t.cancel();
    }
    
    private void updateDb(){
        // Key strokes and time pressed doesn't get reset, the are summed until the keylogger stops
        // get's the values for the key logger
        KeyLogData keyLogData = keyLogger.getKeyLogData();
        Map<String, Integer> keyValues = keyLogData.getKeyValues();
        float timePressed = keyLogData.getKeyPressedTime();
        int keyStrokes = keyLogData.getKeyStrokes();
        LocalDate currentDate = LocalDate.now();

        updateTotalTodayTable(currentDate, timePressed, keyStrokes);
        updateKeyboardsTable(currentDate, timePressed, keyStrokes);
        updateHeatmapTable(currentDate, keyValues);
        updateKeyStrokesComponents();

        System.out.println("update db");
        firstUpdate = false;
    }

    /**
     * Get's the total key strokes and total time key pressed values.
     */
    private void getTotalValuesKeyboard(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards WHERE id = " + keyboardId;
        ObservableList<Keyboard> keyboardList = ReadDb.selectAllValuesKeyboard(sqlStmt);

        if (keyboardList != null) {
            Keyboard keyboard = keyboardList.get(0);
            oldTotal = keyboard.getTotalKeyStrokes();
            oldTimePressed = keyboard.getTotalTimeKeyPressed();
        }
    }

    /**
     * Updates the total key strokes, last used and total time pressed in the keyboard table.
     * @param currentDate Current Date
     * @param timePressed Total time key pressed
     * @param keyStrokes Total key strokes
     */
    private void updateKeyboardsTable(LocalDate currentDate, float timePressed, int keyStrokes){
        String sqlStmt = "UPDATE keyboards SET lastUsed = ?, totKeystrokes = ?, totTimePressed = ? WHERE id = " + keyboardId;
        WriteDb.executeSqlStmt(sqlStmt, currentDate.toString(), Integer.toString((oldTotal + keyStrokes)), Float.toString((oldTimePressed + timePressed)));
    }

    /**
     * Update the total key strokes and time pressed in the total Today table.
     * @param currentDate Current Data
     * @param timePressed Total time Keys pressed
     * @param keyStrokes Total key strokes
     */
    private void updateTotalTodayTable(LocalDate currentDate, Float timePressed, int keyStrokes){
        String date = currentDate.toString();
        String sqlGetStmt = "SELECT keyboardId, date, keyStrokes, timePressed FROM totalToday WHERE keyboardId = " + keyboardId +
                " AND date = '" + date + "'";
        ArrayList<TotalToday> totalKeyboardValues = ReadDb.selectAllValuesTotalToday(sqlGetStmt);

        if (totalKeyboardValues.size() == 0) {
            String sqlSetStmt = "INSERT INTO totalToday(keyboardId, date, keyStrokes, timePressed) VALUES(?,?,?,?)";
            WriteDb.executeSqlStmt(sqlSetStmt, Integer.toString(keyboardId), date, Integer.toString(keyStrokes), Float.toString(timePressed));
            createdThisRun = true;
        }
        else {
            // at the first db update the value of the existing entrance is saved, because this has to be added to the current tracked key strokes
            if (firstUpdate){
                componentsKeyStrokesStart = totalKeyboardValues.get(0).getKeyStrokes();
                componentTimeStart = totalKeyboardValues.get(0).getTimePressed();
            }
            // but only if this entrance is not created in the same period, because in this case the are no keystrokes tracked before.
            if (!createdThisRun){
                keyStrokes += componentsKeyStrokesStart;
                timePressed += componentTimeStart;
            }
            String sqlSetStmt = "UPDATE totalToday SET keyStrokes = ?, timePressed = ? WHERE keyboardId = " + keyboardId + " AND date = '" + date + "'";
            WriteDb.executeSqlStmt(sqlSetStmt, Integer.toString(keyStrokes), Float.toString(timePressed));
        }
    }

    // TODO complete the function, map the differences and than update the db
    private void updateHeatmapTable(LocalDate currentDate, Map<String, Integer> keyValues){
        String date = currentDate.toString();
        String sqlGetStmt = "SELECT keyboardId, date, key, pressed FROM heatmap WHERE keyboardId = " + keyboardId +
                " AND date = '" + date + "'";
        ArrayList<Heatmap> heatmapValues = ReadDb.selectAllValueHeatmapTable(sqlGetStmt);

        if (heatmapValues.size() == 0) {
            for (Map.Entry<String, Integer> value : keyValues.entrySet()) {
                String sqlPutStmt = "INSERT INTO heatmap(keyboardId, date, key, pressed) VALUES(?,?,?,?)";
                WriteDb.executeSqlStmt(sqlPutStmt, Integer.toString(keyboardId), date, value.getKey(), Integer.toString(value.getValue()));
            }
        }
        else {
            for (Map.Entry<String, Integer> value : keyValues.entrySet()) {
                boolean insert = true;
                for (Heatmap heatmapValue : heatmapValues){
                    if (value.getKey().equals(heatmapValue.getKey())){
                        String sqlStmt = "UPDATE heatmap SET pressed = ? WHERE keyboardId = " + keyboardId +
                                " AND date = '" + date + "' AND key = '" + heatmapValue.getKey() + "'";
                        int sumPressed = value.getValue() + heatmapValue.getTimesPressed();
                        WriteDb.executeSqlStmt(sqlStmt, Integer.toString(sumPressed));
                        insert = false;
                        break;
                    }
                }
                if (insert) {
                    String sqlPutStmt = "INSERT INTO heatmap(keyboardId, date, key, pressed) VALUES(?,?,?,?)";
                    WriteDb.executeSqlStmt(sqlPutStmt, Integer.toString(keyboardId), date, value.getKey(), Integer.toString(value.getValue()));
                }
            }
        }

        keyLogger.getKeyLogData().clearKeyValues();
    }

    /**
     * Updates the KeyStrokes for the Components table.
     * Adds all the relevant values of the totalToday Table.
     */
    private void updateKeyStrokesComponents(){
        // TODO date add hour, because at the same date all keystrokes are added (add component at evening)
        // get's all the active components of the selected keyboard
        String selectSql = "SELECT id, keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel, keyStrokes, addDate, " +
                "isActive FROM components WHERE keyboardId = " + keyboardId + " AND isActive = True";
        ObservableList<Component> components = ReadDb.selectAllValuesComponents(selectSql);

        // get's the updated keyStrokes for the single components and updates them in the components table
        for(Component component : components){
            String sumSql = "SELECT SUM(keyStrokes) FROM totalToday WHERE date >= '" + component.getAddedDate() + "' AND keyboardId = " + keyboardId;
            int keyStrokes = ReadDb.sumDateSpecificKeyStrokes(sumSql);
            System.out.println(keyStrokes);

            String updateSql = "UPDATE components SET keyStrokes = ? WHERE id = " + component.getId();
            WriteDb.executeSqlStmt(updateSql, Integer.toString(keyStrokes));
        }
    }
}
