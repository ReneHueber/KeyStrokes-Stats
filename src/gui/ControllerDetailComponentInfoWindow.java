package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import objects.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ControllerDetailComponentInfoWindow {

    @FXML
    private Label type;

    @FXML
    private Label name;
    @FXML
    private Label keyPressure;

    @FXML
    private Label brand;
    @FXML
    private Label keyTravel;

    @FXML
    private Label addedAt;
    @FXML
    private Label removedAt;

    @FXML
    private Label keyStrokes;


    /**
     * Set's the values of the Selected Component.
     * @param selectedComponent Selected Component
     */
    protected void setValues(Component selectedComponent){
        type.setText(selectedComponent.getComponentType());
        name.setText(selectedComponent.getComponentName());
        brand.setText(selectedComponent.getComponentBrand());
        keyPressure.setText(checkNumberZero(Float.toString(selectedComponent.getKeyPressure())));
        keyTravel.setText(checkNumberZero(Integer.toString(selectedComponent.getKeyTravel())));
        addedAt.setText(formatDate(selectedComponent.getAddedDate()));
        removedAt.setText(formatDate(selectedComponent.getRetiredDate()));
        keyStrokes.setText(Integer.toString(selectedComponent.getKeyStrokes()));
    }

    /**
     * Format's the date in the right format (dd.MM.yyyy) or return "" is the date is empty.
     * @param date Date to format as String
     * @return Formatted Date as String or ""
     */
    private String formatDate(String date){
        if (!date.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(date, formatter).toString();
        }
        else
            return "";
    }

    /**
     * Check if the passed number as String is zero, return empty String if the number is zero.
     * @param number Number to check as String
     * @return Number as String or empty String
     */
    private String checkNumberZero(String number){
        if (number.equals("0") || number.equals("0.0"))
            return "";
        else
            return number;
    }
}




