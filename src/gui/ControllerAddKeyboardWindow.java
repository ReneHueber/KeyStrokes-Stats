package gui;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import objects.Keyboard;

import java.io.IOException;
import java.time.LocalDate;


public class ControllerAddKeyboardWindow {
    private String keyboardStyle = "split";
    protected Stage parentStage;
    private ObservableList<Keyboard> allKeyboards = FXCollections.observableArrayList();

    @FXML
    private ImageView splitLayoutIv, standardLayoutIv;

    @FXML
    private Label nameError;
    @FXML
    private TextField name;

    @FXML
    private Label typeError;
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
        getSavedKeyboards();
        setupDatePicker();

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
            // checks if the inputs are okay
            checkInput(name, nameError, "Enter a Keyboard Name");
            checkInput(type, typeError, "Enter a Keyboard Type");
            // check if no error are shown
            if (!nameError.isVisible() && !typeError.isVisible() &&
                    !checkKeyboardNameExisting(name.getText())){
                String date = datePicker.getValue().toString();

                // creates a new database entrance for the keyboard
                String sqlStatement = "INSERT INTO keyboards(keyboardName, keyboardType, layout, totKeystrokes," +
                        "totTimePressed, usedSince, lastUsed) " +
                        "VALUES(?,?,?,?,?,?,?)";
                WriteDb.executeWriteSqlStmt(sqlStatement, name.getText(), type.getText(), keyboardStyle, "0",
                "0.0", date, "0000-00-00");

                reloadSelectKeyboardWindow();
                // closes the stage after the values are saved
                Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                stage.close();
            } // error is the name already exists
            else if (checkKeyboardNameExisting(name.getText())){
                displayInputError(nameError, "Name already exists!");
                name.setText("");
            }
        });


        // gui handling for the name text field
        name.setOnMouseClicked(mouseEvent -> {
            checkInput(type, typeError, "Enter a Keyboard Type");
            hideInputError(nameError);
        });

        name.setOnKeyReleased(keyEvent -> checkInput(name, nameError, "Enter a Keyboard Name"));


        // Gui handling for the type text field
        type.setOnMouseClicked(mouseEvent -> {
            checkInput(name, nameError, "Enter a Keyboard Name");
            hideInputError(typeError);
        });

        type.setOnKeyReleased(keyEvent -> checkInput(type, typeError, "Enter a Keyboard Type"));

    }

    /**
     * Updates the Keyboard List after a Keyboard is added.
     */
    public void reloadSelectKeyboardWindow() {
        ProcessFxmlFiles selectKeyboardWindow = new ProcessFxmlFiles("../fxml/selectKeyboardWindow.fxml", "Select Keyboard");
        selectKeyboardWindow.openInExistingStage(parentStage);
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
        datePicker.setConverter(datePickerConverter.getConverter());
        datePicker.setValue(LocalDate.now());
        // user can only choose real dates
        datePicker.setEditable(false);
    }

    /**
     * Set's the error Label visible and set's the error massage.
     * @param errorLabel Wished error Label
     * @param errorMassage Wished error Massage
     */
    protected static void displayInputError(Label errorLabel, String errorMassage){
        errorLabel.setVisible(true);
        errorLabel.setText(errorMassage);
    }

    /**
     * Clears and hides the error Label.
     * @param errorLabel Wished error Label
     */
    protected static void hideInputError(Label errorLabel){
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    /**
     * Checks if the input is empty and displays or hides the error label.
     * @param input Check text field
     * @param errorLabel wished error label
     * @param errorMassage wished error massage
     */
    private void checkInput(TextField input, Label errorLabel, String errorMassage){
        if (input.getText().isEmpty())
            displayInputError(errorLabel, errorMassage);
        else
            hideInputError(errorLabel);
    }

    /**
     * Get's all the saved keyboards of the db, needed to check the names.
     */
    private void getSavedKeyboards(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards";
        allKeyboards = ReadDb.selectAllValuesKeyboard(sqlStmt);
    }

    /**
     * Checks if the Keyboard name is already existing.
     * @param keyboardName Keyboard name to check
     * @return True if the name exist, false if not
     */
    private boolean checkKeyboardNameExisting(String keyboardName){
        if (allKeyboards.size() != 0){
            for (Keyboard keyboard : allKeyboards){
                if (keyboard.getKeyboardName().equals(keyboardName))
                    return true;
            }
            return false;
        }
        return false;
    }
}