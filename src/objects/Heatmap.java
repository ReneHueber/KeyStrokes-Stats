package objects;

import java.time.LocalDate;

public class Heatmap {
    private int id;
    private LocalDate date;
    private String key;
    private int timesPressed;

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
