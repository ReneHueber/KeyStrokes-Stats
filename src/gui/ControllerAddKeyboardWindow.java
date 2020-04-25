package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ControllerAddKeyboardWindow {
    @FXML
    ImageView splitLayoutIv, standardLayoutIv;
    @FXML
    TextField name;
    @FXML
    DatePicker datePicker;
    @FXML
    Label confirm;

    /**
     * Setup the Image View click Listeners, and the date picker.
     */
    public void initialize() {
        // change the layout if the split Layout image is clicked
        splitLayoutIv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                changesScenes(mouseEvent, "../fxml/splitSelectedWindow.fxml");
            }
        });

        // change the layout if you standard Layout image is clicked
        standardLayoutIv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                changesScenes(mouseEvent, "../fxml/standardSelectedWindow.fxml");
            }
        });

        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // keyboardConfirmed(mouseEvent);
                checkInputs();
            }
        });

        setupDatePicker();
    }

    /**
     * Passes the entered Values to the SelectKeyboard Controller
     * Closes the Window if finished
     * @param event The Mouse Click event of the confirm label
     */
    public void keyboardConfirmed(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/selectKeyboardWindow.fxml"));
            fxmlLoader.load();

            ControllerSelectKeyboardWindow controller = fxmlLoader.getController();
            controller.getKeyboardObject("Test", "Text");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Changes the Scenes in the current window.
     * @param event The Mouse click event of the Image View
     * @param fxmlLayout Path to fxml file that should be loaded
     */
    private void changesScenes(MouseEvent event, String fxmlLayout) {
        // get's the current stage and changes the scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlLayout));
            Parent rootSplitSelected = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(rootSplitSelected));
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Formats the Date like "dd.MM.yyyy" and set's the current Date.
     */
    private void setupDatePicker(){
        // creates the formatter for the date of the date picker
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            @Override
            public String toString(LocalDate localDate) {
                if (localDate != null){
                    return dateFormatter.format(localDate);
                } else{
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String s) {
                if (s != null && !s.isEmpty()){
                    return LocalDate.parse(s, dateFormatter);
                }
                else{
                    return null;
                }
            }
        };
        datePicker.setConverter(converter);
        datePicker.setValue(LocalDate.now());
        // user can only choose real dates
        datePicker.setEditable(false);
    }

    // TODO finish this function
    private void checkInputs(){
        String keyboardName = name.getText();
        if (keyboardName.isEmpty()){
            System.out.println("False");
        }
        LocalDate useDate = datePicker.getValue();
        System.out.println(String.format("%d.%d.%d", useDate.getDayOfMonth(), useDate.getMonthValue(), useDate.getYear()));
    }

}