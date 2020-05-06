package keylogger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs all the key strokes and the time of the key strokes.
 * If you got a Keyboard with layers, the time you hold der layer switch down,
 * is not recorded correct.
 */
public class KeyLogger implements NativeKeyListener {
    private static KeyLogData keyLogData;
    // to calculate the length of the key pressed
    private static Map<Integer, Long> keyStrokes;

    /**
     * setup and starts the key listener
     */
    public void setupKeyListener(){
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e){
            e.printStackTrace();
        }

        // add the global Listener
        GlobalScreen.addNativeKeyListener(new KeyLogger());

        // Disables the Logs, because the are not needed
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        // initialize the Hash map
        keyStrokes = new HashMap<>();
        // initialize the object
        keyLogData = new KeyLogData();

        // start's the update schedule
        DbUpdateSchedule dpUpdate = new DbUpdateSchedule(this);
        dpUpdate.startSchedule();
    }

    /**
     * Saves how often every specific key is pressed
     * @param key the pressed key
     */
    public void nativeKeyTyped(NativeKeyEvent key) {
        // System.out.println("Key Typed: " + key.getKeyChar());
        keyLogData.addKeyValue(String.valueOf(key.getKeyChar()), key.getRawCode());
        // System.out.println(keyLogData.getKeyValues());
    }

    /**
     * Saves the time once the key is pressed,d
     * so the time difference at the release function can be calculated
     * @param key the pressed key
     */
    public void nativeKeyPressed(NativeKeyEvent key) {
        // get's the keycode an the time, stores it in an hash map
        int keyCode = key.getKeyCode();
        Long keyPressedMillis = System.currentTimeMillis();
        keyStrokes.put(keyCode, keyPressedMillis);
    }

    /**
     * Calculates the time of the key strokes
     * increases the total key strokes and total key pressed time
     * @param key the pressed key
     */
    public void nativeKeyReleased(NativeKeyEvent key) {
        // get's the keycode
        int keyCode = key.getKeyCode();
        float timeSec = calculateSecondsPressed(keyCode);

        // increases the total values
        keyLogData.increaseKeyPressedTime(timeSec);
        keyLogData.increaseKeyStroke();
    }

    /**
     * Return the KeyLogData object
     * @return KeyLogData object
     */
    protected KeyLogData getKeyLogData(){
        return keyLogData;
    }

    /**
     * Calculate the difference between the pressed and released time
     * @param keyCode the keycode of the clicked key
     * @return time the key is pressed in seconds
     */
    private float calculateSecondsPressed(int keyCode){
        Long keyReleasedMillis = System.currentTimeMillis();
        int timeMilli = (int) (keyReleasedMillis - keyStrokes.get(keyCode));
        // delete's the entrance because it is not needed anymore
        keyStrokes.remove(keyCode);
        return (float) timeMilli / 1000;
    }
}