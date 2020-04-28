package gui;

import database.WriteDb;
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
    private String keyboardStyle = "split";
    protected Stage parentStage;

    @FXML
    private ImageView splitLayoutIv, standardLayoutIv;
    @FXML
    private TextField name;
    @FXML
    private TextField type;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label confirm;


    /**
     * Setup the Image View, click Listeners, and the date picker.
     */
    public void initialize() {
        // change the layout if the split Layout image is clicked
        splitLayoutIv.setOnMouseClicked(mouseEvent -> {
            keyboardStyle = "split";
            changesScenes(mouseEvent, "../fxml/splitSelectedWindow.fxml");
        });

        // change the layout if you standard Layout image is clicked
        standardLayoutIv.setOnMouseClicked(mouseEvent -> {
            keyboardStyle = "standard";
            changesScenes(mouseEvent, "../fxml/standardSelectedWindow.fxml");
        });


        // label to add the keyboard to the list
        confirm.setOnMouseClicked(mouseEvent -> {
            // keyboard name okay
            boolean nameOkay = checkTextFieldInput(name, "Enter a Keyboard Name");
            boolean typeOkay = checkTextFieldInput(type, "Enter a Keyboard Type");
            if (nameOkay && typeOkay){
                // get's the selected Date
                LocalDate chosenDate = datePicker.getValue();
                String date = chosenDate.toString();

                // creates a new database entrance for the keyboard
                String sqlStatement = "INSERT INTO keyboards(keyboardName, keyboardType, layout, totKeystrokes," +
                        "totTimePressed, usedSince, lastUsed) " +
                        "VALUES(?,?,?,?,?,?,?)";
                WriteDb.insertIntoTable(sqlStatement, name.getText(), type.getText(), keyboardStyle, "0",
                "0.0", date, "0000-00-00");

                reloadSelectKeyboardWindow();
                // closes the stage after the values are saved
                Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                stage.close();
            }
        });


        // gui handling for the name text field
        name.setOnMouseClicked(mouseEvent -> {
            clearTextField(name, "Enter a Keyboard Name");
            errorTextField(type, "Enter a Keyboard Type");
        });

        name.setOnKeyPressed(keyEvent -> clearTextField(name, "Enter a Keyboard Name"));

        name.setOnKeyReleased(keyEvent -> errorTextField(name, "Enter a Keyboard Name"));


        // Gui handling for the type text field
        type.setOnMouseClicked(mouseEvent -> {
            clearTextField(type, "Enter a Keyboard Type");
            errorTextField(name, "Enter a Keyboard Name");
        });

        type.setOnKeyPressed(keyEvent -> clearTextField(type, "Enter a Keyboard Type"));

        type.setOnKeyReleased(keyEvent -> errorTextField(type, "Enter a Keyboard Type"));


        // setup the appearance of the date picker
        setupDatePicker();
    }

    /**
     * Updates the Keyboard List after a Keyboard is added.
     */
    public void reloadSelectKeyboardWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/selectKeyboardWindow.fxml"));
            Parent root = (Parent) fxmlLoader.load();

            parentStage.setScene(new Scene(root));
            parentStage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Changes the Scenes in the current window.
     * Passes the Keyboard Style String
     * @param event The Mouse click event of the Image View
     * @param fxmlLayout Path to fxml file that should be loaded
     */
    private void changesScenes(MouseEvent event, String fxmlLayout) {
        // get's the current stage and changes the scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlLayout));
            Parent rootSplitSelected = fxmlLoader.load();
            // passes the keyboard Style Value to the new controller to save the value
            ControllerAddKeyboardWindow controller = fxmlLoader.getController();
            controller.keyboardStyle = this.keyboardStyle;
            controller.parentStage = this.parentStage;

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
                if (localDate != null) {
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

    /**
     * Clear the text Field from the error massage.
     * @param clearMassage Text ah with the Text Field should be cleared
     */
    private void clearTextField(TextField field, String clearMassage){
        // TODO not only name
        if (field.getText().equals(clearMassage)) {
            field.setText("");
        }
    }

    /**
     * Set's the Error massage for the text Field.
     * @param errorMassage Massage that should be displayed in the Text Field
     */
    private void errorTextField(TextField field, String errorMassage){
        if (field.getText().isEmpty()) {
            field.setText(errorMassage);
        }
    }

    /**
     * Checks if the text Field is not empty or the error massage is showed.
     * @param filed Text Field to check
     * @param errorText The error Massage for the text Field
     * @return Input of the text Field is okay
     */
    private boolean checkTextFieldInput(TextField filed, String errorText){
        if (!filed.getText().isEmpty() && !filed.getText().equals(errorText))
            return true;
        else{
            filed.setText(errorText);
            return false;
        }
    }
}