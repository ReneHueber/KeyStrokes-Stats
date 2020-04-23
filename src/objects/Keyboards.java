package objects;

import javafx.scene.image.Image;

public class Keyboards {
    private Image keyboardImage;
    private String keyboardName;
    private int totalKeyStrokes;
    private float totalTimeKeyPressed;

    /**
     * Initialize the Object with Values.
     * @param keyboardImage Image of the Keyboard
     * @param keyboardName Name of the Keyboard
     * @param totalKeyStrokes Total Key Strokes
     * @param totalTimeKeyPressed Total time key pressed
     */
    public Keyboards(Image keyboardImage, String keyboardName, int totalKeyStrokes, float totalTimeKeyPressed) {
        this.keyboardImage = keyboardImage;
        this.keyboardName = keyboardName;
        this.totalKeyStrokes = totalKeyStrokes;
        this.totalTimeKeyPressed = totalTimeKeyPressed;
    }

    public Image getKeyboardImage() {
        return keyboardImage;
    }

    public String getKeyboardName() {
        return keyboardName;
    }

    public int getTotalKeyStrokes() {
        return totalKeyStrokes;
    }

    public float getTotalTimeKeyPressed() {
        return totalTimeKeyPressed;
    }
}
