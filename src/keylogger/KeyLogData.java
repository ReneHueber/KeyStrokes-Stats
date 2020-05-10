package keylogger;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class KeyLogData {
    private int keyStrokes = 0;
    private float keyPressedTime = 0.0f;
    private LocalDate date;
    private Map<String, Integer> keyValues = new HashMap<>();
    // special key Codes that are not working with the build in function
    private final Map<Integer, String> specialKeyCodes =  Map.ofEntries(
            new AbstractMap.SimpleEntry<>(65289,"Tab"),
            new AbstractMap.SimpleEntry<>(65288,"Backspace"),
            new AbstractMap.SimpleEntry<>(65505,"Shift"),
            new AbstractMap.SimpleEntry<>(65506,"Shift"),
            new AbstractMap.SimpleEntry<>(32,"Space"),
            new AbstractMap.SimpleEntry<>(65509,"Caps"),
            new AbstractMap.SimpleEntry<>(65535,"Delete"),
            new AbstractMap.SimpleEntry<>(65379,"Insert"),
            new AbstractMap.SimpleEntry<>(65307,"Esc"),
            new AbstractMap.SimpleEntry<>(65293,"Enter"),
            new AbstractMap.SimpleEntry<>(65362,"Up"),
            new AbstractMap.SimpleEntry<>(65364,"Down"),
            new AbstractMap.SimpleEntry<>(65361,"Left"),
            new AbstractMap.SimpleEntry<>(65363,"Right"),
            new AbstractMap.SimpleEntry<>(65514,"Alt"),
            new AbstractMap.SimpleEntry<>(65507,"Ctrl")
            );

    public KeyLogData(LocalDate date){
        this.date = date;
    }

    /**
     * Increases the total key strokes by one.
     */
    protected void increaseKeyStroke(){
        keyStrokes++;
    }

    /**
     * Increases the total time key pressed, by the time the new key is pressed.
     * @param time time the new key is pressed
     */
    protected void increaseKeyPressedTime(float time){
        keyPressedTime += time;
    }

    /**
     * Insert description for special key values like "Space"
     * If the Key Value is not existing is it set to 1.
     * If the Key Values exist it is increased by one.
     * @param key Value of the pressed Key
     */
    protected void addKeyValue(String key, int keyCode){
        if (keyCode == 32)
            key = "Space";
        else if (keyCode > 10000){
            key = specialKeyCodes.get(keyCode);
        }

        if (!keyValues.containsKey(key)){
            keyValues.put(key, 1);
        }
        else{
            int currentValue = keyValues.get(key);
            keyValues.put(key, ++currentValue);
        }
    }

    public int getKeyStrokes() {
        return keyStrokes;
    }

    public float getKeyPressedTime() {
        return keyPressedTime;
    }

    public Map<String, Integer> getKeyValues() {
        return keyValues;
    }

    public LocalDate getDate(){
        return date;
    }

    /**
     * Clears the key Values, because the are already written in the Db.
     */
    public void clearKeyValues(){
        keyValues.clear();
    }
}
