package objects;

import gui.ControllerMainWindow;
import javafx.scene.image.Image;

public class Keyboards {
    private final Image splitImage = new Image(ControllerMainWindow.class.getResource("../images/ergodox.png").toExternalForm());
    private final Image standardImage = new Image(ControllerMainWindow.class.getResource("../images/plank.png").toExternalForm());

    private Image keyboardImage;
    private String keyboardName;
    private String keyboardType;
    private int totalKeyStrokes;
    private float totalTimeKeyPressed;
    private String lastUsed;
    private String inUseSince;
    private String layout;

    /**
     * Initialize the Object with Values.
     * @param keyboardName Name of the Keyboard
     * @param totalKeyStrokes Total Key Strokes
     * @param totalTimeKeyPressed Total time key pressed
     * @param lastUsed Date the keyboard was last used
     * @param inUseSince When the keyboard was introduced
     * @param layout Keyboard Layout
     */
    public Keyboards(String keyboardName, String keyboardType, int totalKeyStrokes, float totalTimeKeyPressed,
                     String lastUsed, String inUseSince, String layout) {
        this.keyboardName = keyboardName;
        this.keyboardType = keyboardType;
        this.totalKeyStrokes = totalKeyStrokes;
        this.totalTimeKeyPressed = totalTimeKeyPressed;
        this.lastUsed = lastUsed;
        this.inUseSince = inUseSince;
        this.layout = layout;

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

    public String getLastUsed() {
        return lastUsed;
    }

    public String getInUseSince() {
        return inUseSince;
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
}
