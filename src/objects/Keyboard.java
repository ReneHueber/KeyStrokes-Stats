package objects;

import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Keyboard {
    private final Image splitImage = new Image(getClass().getResource("../images/ergodox.png").toExternalForm());
    private final Image standardImage = new Image(getClass().getResource("../images/plank.png").toExternalForm());

    private Image keyboardImage;
    private final String keyboardName;
    private final String keyboardType;
    private final int totalKeyStrokes;
    private final float totalTimeKeyPressed;
    private final String lastUsed;
    private final String inUseSince;
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

    public int getKeyboardId(){
        return id;
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
     * Get's a string date and formats it, returns it as a String.
     * @param date Date as String
     * @return Formatted Date as String
     */
    private String formatStringDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return formatter.format(LocalDate.parse(date));
    }

    /**
     * Rounds a Float number to two digits.
     * @param number Number to Round
     * @return Rounded Number
     */
    private float roundFloat(float number){
        DecimalFormat df = new DecimalFormat("#.00");
        String roundNumber = df.format(number);
        return Float.parseFloat(roundNumber.replace(",", "."));
    }
}
