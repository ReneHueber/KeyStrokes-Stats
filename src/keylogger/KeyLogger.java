package keylogger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/** Logs all the key strokes and the time of the key strokes **/
public class KeyLogger implements NativeKeyListener {
    Map<Integer, Long> keyStrokes = new HashMap<>();

    /** setup and starts the key listener **/
    public static void setupKeyListener(){
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
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    /** a Key is pressed **/
    public void nativeKeyPressed(NativeKeyEvent key) {
        // get's the keycode an the time, stores it in an hash map
        int keyCode = key.getKeyCode();
        Long keyPressedMillis = System.currentTimeMillis();
        keyStrokes.put(keyCode, keyPressedMillis);
    }

    /** a Key is released **/
    public void nativeKeyReleased(NativeKeyEvent key) {
        // get's the keycode
        int keyCode = key.getKeyCode();
        float timeSec = calculateSecondsPressed(keyCode);
        System.out.println(timeSec);
    }

    /** calculate the difference between the pressed and released time
     * returns the time the key is pressed in seconds **/
    private float calculateSecondsPressed(int keyCode){
        Long keyReleasedMillis = System.currentTimeMillis();
        int timeMilli = (int) (keyReleasedMillis - keyStrokes.get(keyCode));
        // delete's the entrance because it is not needed anymore
        keyStrokes.remove(keyCode);
        return (float) timeMilli / 1000;
    }
}