package objects;

import java.time.LocalDate;

public class Heatmap {
    private final int id;
    private final LocalDate date;
    private final String key;
    private final int timesPressed;

    public Heatmap(int id, LocalDate date, String key, int timesPressed) {
        this.id = id;
        this.date = date;
        this.key = key;
        this.timesPressed = timesPressed;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public int getTimesPressed() {
        return timesPressed;
    }
}
