package gui;

import database.WriteDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import objects.Keyboards;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ControllerAddComponentWindow {

    private final ObservableList<String> addedDateOptions = FXCollections.observableArrayList(
            "Today",
            "Since Beginning",
            "Choose Date"
    );

    private final ObservableList<String> componentsOptions = FXCollections.observableArrayList(
            "Key Caps",
            "Key Switches",
            "Tilt Set",
            "Other"
    );

    private Keyboards selectedKeyboard;

    // today is the default value
    private String addDate = LocalDate.now().toString();

    @FXML
    private ComboBox<String> componentType;

    @FXML
    private TextField componentName;
    @FXML
    private TextField componentBrand;
    @FXML
    private TextField keyPressure;
    @FXML
    private TextField keyTravel;

    @FXML
    private Label nameError;
    @FXML
    private Label brandError;
    @FXML
    private Label pressureError;
    @FXML
    private Label travelError;
    @FXML
    private Label dateError;

    @FXML
    private ComboBox<String> addedDate;
    @FXML
    private DatePicker chooseDateDatePicker;

    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;


    /**
     * Set's values for the gui elements.
     */
    public void initialize(){
        // adds the lists to the combo boxes and sets a value
        addedDate.setItems(addedDateOptions);
        addedDate.setValue(addedDateOptions.get(0));

        componentType.setItems(componentsOptions);
        componentType.setValue(componentsOptions.get(1));

        // closes the stage
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        });

        // disable or enable the date picker
        addedDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String option = addedDate.getSelectionModel().getSelectedItem();
                if (option.equals("Choose Date")){
                    chooseDateDatePicker.setDisable(false);
                }
                else {
                    chooseDateDatePicker.setDisable(true);
                    chooseDateDatePicker.getEditor().clear();

                    if (option.equals("Today")){
                        addDate = LocalDate.now().toString();
                    }
                    else if (option.equals("Since Beginning")){
                        // formats the date given by the object
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        LocalDate date = LocalDate.parse(selectedKeyboard.getInUseSince(), formatter);
                        addDate = date.toString();
                    }
                }
            }
        });

        // if a date at the datePicker is selected
        chooseDateDatePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hideInputError(dateError);
               addDate = chooseDateDatePicker.getValue().toString();
            }
        });

        // disable or enable the key pressure and key travel inputs
        componentType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (componentType.getSelectionModel().getSelectedItem().equals("Key Switches")){
                    enableAllOtherInputs();
                }
                else if (componentType.getSelectionModel().getSelectedItem().equals("Other")){
                    enableAllOtherInputs();
                }
                else {
                    disableTextField(keyPressure);
                    disableTextField(keyTravel);
                    disableTextField(componentName);
                }
                // reset's all the error labels and inputs if the component type is changed
                Label[] errorLabels = {nameError, brandError, pressureError, travelError, dateError};
                TextField[] textFields = {componentName, componentBrand, keyPressure, keyTravel};
                // resets the error labels
                for (Label errorLabel : errorLabels){
                    hideInputError(errorLabel);
                    errorLabel.setText("");
                }
                // resets the inputs
                for (TextField textField : textFields){
                    if (!textField.getText().isEmpty())
                        textField.setText("");
                }
                chooseDateDatePicker.getEditor().clear();
            }
        });


        componentName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hideInputError(nameError);
                checkTextInput(componentBrand, brandError, "Enter a Brand");
                checkDatePicker();
            }
        });

        componentName.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
               checkTextInput(componentName, nameError, "Enter a Name");
            }
        });

        componentBrand.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hideInputError(brandError);
                checkNameInput(componentName, nameError, "Enter a Name");
                checkDatePicker();
            }
        });

        componentBrand.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                checkTextInput(componentBrand, brandError, "Enter a Brand");
            }
        });

        keyPressure.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hideInputError(pressureError);
                checkTextInput(componentBrand, brandError, "Enter a Brand");
                checkNameInput(componentName, nameError, "Enter a Name");
                checkDatePicker();
            }
        });

        keyPressure.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!keyPressure.getText().isEmpty())
                    checkNumberInput(keyPressure, pressureError);
            }
        });


        keyTravel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hideInputError(travelError);
                checkTextInput(componentBrand, brandError, "Enter a Brand");
                checkNameInput(componentName, nameError, "Enter a Name");
                checkDatePicker();
            }
        });

        keyTravel.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!keyTravel.getText().isEmpty())
                    checkNumberInput(keyTravel, travelError);
            }
        });


        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (checkValues()){
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                }
            }
        });

        setUpDatePicker();
    }

    /**
     * Passes the selected Keyboard from the Select Keyboard Window.
     * @param selectedKeyboard Keyboard that has been selected in the Window before
     */
    public void setSelectedKeyboard(Keyboards selectedKeyboard){
        this.selectedKeyboard = selectedKeyboard;
    }

    /**
     * Clears the Text and disables the TextField.
     * @param input Wished TextField
     */
    private void disableTextField(TextField input){
        input.setText("");
        input.setDisable(true);
    }

    /**
     * Enables all the TextFields.
     */
    private void enableAllOtherInputs(){
        keyPressure.setDisable(false);
        keyTravel.setDisable(false);
        componentName.setDisable(false);
    }

    /**
     * Name is disables sometimes so, we don't need to check.
     * Only checks the Input if it is not disabled.
     * @param input Input TextField
     * @param error Error Label
     * @param errorMassage Error Massage to display
     */
    private void checkNameInput(TextField input, Label error, String errorMassage){
        if (!input.isDisabled()){
            checkTextInput(input, error, errorMassage);
        }
    }

    /**
     * Setup the date picker
     */
    private void setUpDatePicker(){
        chooseDateDatePicker.setConverter(datePickerConverter.getConverter());
        chooseDateDatePicker.setPromptText("dd.MM.yyyy");
        chooseDateDatePicker.setEditable(false);
    }

    private boolean checkValues(){
       checkTextInput(componentBrand, brandError, "Enter a Brand");
       checkDatePicker();

       if (!nameError.isVisible() && !brandError.isVisible() && !pressureError.isVisible() && !travelError.isVisible()){
            String sqlStmt = "INSERT INTO components(keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel," +
                    "addDate) VALUES(?,?,?,?,?,?,?)";
           WriteDb.executeSqlStmt(sqlStmt, Integer.toString(selectedKeyboard.getKeyboardId()), componentType.getSelectionModel().getSelectedItem(),
                   componentName.getText(), componentBrand.getText(), keyPressure.getText(), keyTravel.getText(), addDate);
           return true;
       }
       else{
           return false;
       }
    }

    /**
     * Set's the error Label visible and set's the error massage.
     * @param errorLabel Wished error Label
     * @param errorMassage Wished error Massage
     */
    private void displayInputError(Label errorLabel, String errorMassage){
        errorLabel.setVisible(true);
        errorLabel.setText(errorMassage);
    }

    /**
     * Clears and hides the error Label.
     * @param errorLabel Wished error Label
     */
    private void hideInputError(Label errorLabel){
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    /**
     * Checks if a Date is selected at the DatePicker.
     */
    private void checkDatePicker(){
        if (!chooseDateDatePicker.isDisabled() && chooseDateDatePicker.getEditor().getText().isEmpty()){
            displayInputError(dateError, "Select a Date");
        }
    }

    /**
     * Check if the text of the given TextField is a valid number.
     * @param inputLabel Checked TextField
     * @param errorLabel Label for the Error massage
     */
    private void checkNumberInput(TextField inputLabel, Label errorLabel){
        try{
            Integer.parseInt(inputLabel.getText());
            hideInputError(errorLabel);
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            displayInputError(errorLabel, "Enter a Number");
        }
    }

    /**
     * Check if the TextField is not empty
     * @param inputLabel Checked TextField
     * @param errorLabel Label for the Error massage
     * @param errorMassage Error Massage to display
     */
    private void checkTextInput(TextField inputLabel, Label errorLabel, String errorMassage){
        if (inputLabel.getText().isEmpty())
            displayInputError(errorLabel, errorMassage);
        else
            hideInputError(errorLabel);
    }
}
