package keylogger;

import java.util.Date;
import java.util.HashMap;

public class KeyLogData {
    private int keyStrokes = 0;
    private float keyPressedTime = 0.0f;
    private Date date;
    private HashMap<String, HashMap<String, String>> keyValues;

    /**
     * increases the total key strokes by one
     */
    protected void increaseKeyStroke(){
        keyStrokes++;
    }

    /**
     * increases the total time key pressed, by the time the new key is pressed
     * @param time time the new key is pressed
     */
    protected void increaseKeyPressedTime(float time){
        keyPressedTime += time;
    }
}
