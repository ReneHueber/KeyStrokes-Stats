package objects;

import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Keyboard {
    private final Image splitImage = new Image(getClass().getResource("../images/ergodox.png").toExternalForm());
    private final Image standardImage = new Image(getClass().getResource("../images/plank.png").toExternalForm());

    private Image keyboardImage;
    private final String keyboardName;
    private final String keyboardType;
    private final int totalKeyStrokes;
    private float totalTimeKeyPressed;
    private String lastUsed;
    private String inUseSince;
    private final String layout;
    private final int id;

    /**
     * Initialize the Object with Values.
     * @param keyboardName Name of the Keyboard
     * @param totalKeyStrokes Total Key Strokes
     * @param totalTimeKeyPressed Total time key pressed
     * @param lastUsed Date the keyboard was last used
     * @param inUseSince When the keyboard was introduced
     * @param layout Keyboard Layout
     */
    public Keyboard(String keyboardName, String keyboardType, int totalKeyStrokes, float totalTimeKeyPressed,
                    String lastUsed, String inUseSince, String layout, int id) {
        this.keyboardName = keyboardName;
        this.keyboardType = keyboardType;
        this.totalKeyStrokes = totalKeyStrokes;
        this.totalTimeKeyPressed = totalTimeKeyPressed;
        this.lastUsed = lastUsed;
        this.inUseSince = inUseSince;
        this.layout = layout;
        this.id = id;

        setImage();
    }

    public Image getKeyboardImage() {
        return keyboardImage;
    }

    public String getKeyboardName() {
        return keyboardName;
    }

    public String getKeyboardType(){
        return keyboardType;
    }

    public int getTotalKeyStrokes() {
        return totalKeyStrokes;
    }

    public float getTotalTimeKeyPressed() {
        return totalTimeKeyPressed;
    }

    public float getRoundedTotalTimeKeyPressed(){
        return roundFloat(totalTimeKeyPressed);
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public String getFormattedLastUsed(){
        if (lastUsed.equals("0000-00-00"))
            return "never";
        else
            return formatStringDate(lastUsed);
    }

    public String getInUseSince() {
        return inUseSince;
    }

    public String getFormattedInUseSince(){
        return formatStringDate(inUseSince);
    }

    public String getLayout(){
        return layout;
    }

    public int getKeyboardId(){
        return id;
    }

    public void setLastUsed(String lastUsed){
        this.lastUsed = lastUsed;
    }

    public void setInUseSince(String inUseSince){
        this.inUseSince = inUseSince;
    }

    public void setTotalTimeKeyPressed(float timeKeyPressed) {
        this.totalTimeKeyPressed = timeKeyPressed;
    }

    /**
     * Set's the right Image depending on the layout.
     */
    private void setImage(){
        if (layout.equals("split"))
            keyboardImage = splitImage;
        else
            keyboardImage = standardImage;
    }

    /**
     * Get's a string date and formats it, returns it as a String in the format dd.MM.yyyy
     * @param date Date as String
     * @return Formatted Date as String
     */
    public static String formatStringDate(String date){
        try {
            // formats the date in the right format and returns it as a String
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return formatter.format(LocalDate.parse(date));
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private float roundFloat(float number){
        DecimalFormat df = new DecimalFormat("#.00");
        String roundNumber = df.format(number);
        return Float.parseFloat(roundNumber.replace(",", "."));
    }
}
