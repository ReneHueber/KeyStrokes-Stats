package keylogger;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.ObservableList;
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

    private Timer t;

    public DbUpdateSchedule(KeyLogger keyLogger, int keyboardId){
        this.keyLogger = keyLogger;
        this.keyboardId = keyboardId;

        getTotalValuesKeyboard();
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
        // get's the values for the key logger
        KeyLogData keyLogData = keyLogger.getKeyLogData();
        Map<String, Integer> keyValues = keyLogData.getKeyValues();
        float timePressed = keyLogData.getKeyPressedTime();
        int keyStrokes = keyLogData.getKeyStrokes();
        LocalDate currentDate = LocalDate.now();

        updateTotalTodayTable(currentDate, timePressed, keyStrokes);
        updateKeyboardsTable(currentDate, timePressed, keyStrokes);
        updateHeatmapTable(currentDate, keyValues);

        System.out.println("update db");
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
        }
        else {
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
}
