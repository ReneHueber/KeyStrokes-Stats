package keylogger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Update the Database every 30 Seconds.
 */
public class DbUpdateSchedule {

    /**
     * Starts the Schedule that updates the Db every 30 Seconds.
     */
    public static void startSchedule(){
        Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Update Db");
            }
        };

        t.scheduleAtFixedRate(tt, 30000, 30000);
    }
}
