package objects;

import java.time.LocalDate;

public class TotalToday {
    private int keyboardId;
    private LocalDate date;
    private int keyStrokes;
    private float timePressed;

    public TotalToday(int keyboardId, LocalDate date, int keyStrokes, float timePressed) {
        this.keyboardId = keyboardId;
        this.date = date;
        this.keyStrokes = keyStrokes;
        this.timePressed = timePressed;
    }

    public int getKeyboardId() {
        return keyboardId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getKeyStrokes() {
        return keyStrokes;
    }

    public float getTimePressed() {
        return timePressed;
    }
}
