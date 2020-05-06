package keylogger;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Update the Database every 30 Seconds.
 */
public class DbUpdateSchedule {
    private KeyLogger keyLogger;
    private KeyLogData keyLogData;

    public DbUpdateSchedule(KeyLogger keyLogger){
        this.keyLogger = keyLogger;
    }

    /**
     * Starts the Schedule that updates the Db every 30 Seconds.
     */
    public void startSchedule(){
        Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                // get's the values for the key logger
                keyLogData = keyLogger.getKeyLogData();
                Map<String, Integer> keyValues = keyLogData.getKeyValues();
                for (Map.Entry<String, Integer> entry : keyValues.entrySet())
                    System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        };

        t.scheduleAtFixedRate(tt, 30000, 30000);
    }
}
