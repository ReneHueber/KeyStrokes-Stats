package gui;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomDatePicker{
    private final DatePicker datePicker;
    private final Label dateError;

    public CustomDatePicker(DatePicker datePicker, Label dateError){
        this.datePicker = datePicker;
        this.dateError = dateError;
        setupDatePicker();
    }

    private void setupDatePicker(){
        datePicker.setConverter(new datePickerConverter());
        datePicker.setOnKeyReleased(keyEvent -> {
            getDateTime();
        });
        datePicker.setOnAction(event -> {
            ControllerAddKeyboardWindow.hideInputError(dateError);
        });
        datePicker.setPromptText("dd.MM.yyyy hh:mm");
    }

    static class datePickerConverter extends  StringConverter<LocalDate>{
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public String toString(LocalDate localDate) {
            if (localDate != null) {
                LocalDateTime dateTime = localDate.atTime(0, 0, 0);
                return dateFormatter.format(dateTime);
            } else {
                return "";
            }
        }

        @Override
        public LocalDate fromString(String s) {
            if (s != null && !s.isEmpty()) {
                return LocalDate.parse(s, dateFormatter);
            } else {
                return null;
            }
        }
    }

    protected LocalDateTime getDateTime(){
        try {
            String date = datePicker.getEditor().getText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(date.strip(), formatter);
            ControllerAddKeyboardWindow.hideInputError(dateError);
            return dateTime;
        } catch (DateTimeParseException e) {
            ControllerAddKeyboardWindow.displayInputError(dateError, "Wrong Format or Date!");
            return null;
        }
        catch (NullPointerException e){
            ControllerAddKeyboardWindow.displayInputError(dateError, "Enter a Date!");
            return null;
        }
    }

}

