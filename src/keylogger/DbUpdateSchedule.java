package keylogger;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.ObservableList;
import objects.Keyboards;
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
    private KeyLogger keyLogger;
    private KeyLogData keyLogData;
    private int keyboardId;

    public DbUpdateSchedule(KeyLogger keyLogger, int keyboardId){
        this.keyLogger = keyLogger;
        this.keyboardId = keyboardId;
    }

    /**
     * Starts the Schedule that updates the Db every 30 Seconds.
     */
    public void startSchedule(){
        Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                updateDb();
                /*
                for (Map.Entry<String, Integer> entry : keyValues.entrySet())
                    System.out.println(entry.getKey() + ": " + entry.getValue()); */
            }
        };

        t.scheduleAtFixedRate(tt, 30000, 30000);
    }

    private void updateDb(){
        // get's the values for the key logger
        keyLogData = keyLogger.getKeyLogData();
        Map<String, Integer> keyValues = keyLogData.getKeyValues();
        float timePressed = keyLogData.getKeyPressedTime();
        int keyStrokes = keyLogData.getKeyStrokes();
        LocalDate createdDate = keyLogData.getDate();
        LocalDate currentDate = LocalDate.now();

        updateTotalTodayTable(currentDate, timePressed, keyStrokes);
    }

    private void updateKeyboardsTable(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards WHERE id = " + keyboardId;
        ObservableList<Keyboards> keyboardList = ReadDb.selectAllValuesKeyboard(sqlStmt);

        if (keyboardList != null) {
            Keyboards keyboard = keyboardList.get(0);

        }
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
}
