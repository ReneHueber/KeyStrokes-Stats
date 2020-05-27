package gui;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import objects.Component;
import objects.Keyboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    private Keyboard selectedKeyboard;

    // today is the default value
    private String addDate = LocalDateTime.now().toString();

    private CustomDatePicker customDatePicker;

    @FXML
    private ComboBox<String> componentType;
    @FXML
    private Label typeError;

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
        customDatePicker = new CustomDatePicker(chooseDateDatePicker, dateError);

        // adds the lists to the combo boxes and sets a value
        addedDate.setItems(addedDateOptions);
        addedDate.setValue(addedDateOptions.get(0));

        componentType.setItems(componentsOptions);
        componentType.setValue(componentsOptions.get(1));

        // closes the stage
        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        });

        // disable or enable the date picker
        addedDate.setOnAction(event -> {
            String option = addedDate.getSelectionModel().getSelectedItem();
            if (option.equals("Choose Date")){
                chooseDateDatePicker.setDisable(false);
            }
            else {
                chooseDateDatePicker.setDisable(true);
                chooseDateDatePicker.getEditor().clear();

                if (option.equals("Today")){
                    addDate = LocalDateTime.now().toString();
                }
                else if (option.equals("Since Beginning")){
                    // formats the date given by the object
                    addDate = Keyboard.formatStringDate(selectedKeyboard.getInUseSince());
                }
            }
        });

        // disable or enable the key pressure and key travel inputs
        componentType.setOnAction(event -> {
            String selectedOption = componentType.getSelectionModel().getSelectedItem();

            // component already exists and is not key switch or other
            if (getComponentTypes().contains(selectedOption) && (!selectedOption.equals("Key Switches") && !selectedOption.equals("Other"))){
                displayInputError(typeError, "This component already exists!");
                disableAllInputs();
                saveBtn.setDisable(true);
            }
            else{
                hideInputError(typeError);
                saveBtn.setDisable(false);
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
                    componentBrand.setDisable(false);
                    addedDate.setDisable(false);
                }
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
        });


        // clears the error for the text field and, checks the other necessary textFields
        componentName.setOnMouseClicked(mouseEvent -> {
            hideInputError(nameError);
            checkTextInput(componentBrand, brandError, "Enter a Brand");
            checkDatePicker();
        });

        // check if the input is correct
        componentName.setOnKeyReleased(keyEvent -> checkTextInput(componentName, nameError, "Enter a Name"));

        // clears the error for the text field and, checks the other necessary textFields
        componentBrand.setOnMouseClicked(mouseEvent -> {
            hideInputError(brandError);
            checkNameInput(componentName, nameError);
            checkDatePicker();
        });

        // check if the input is correct
        componentBrand.setOnKeyReleased(keyEvent -> checkTextInput(componentBrand, brandError, "Enter a Brand"));

        // clears the error for the text field and, checks the other necessary textFields
        keyPressure.setOnMouseClicked(mouseEvent -> {
            hideInputError(pressureError);
            checkTextInput(componentBrand, brandError, "Enter a Brand");
            checkNameInput(componentName, nameError);
            checkDatePicker();
        });

        // check if the input is correct
        keyPressure.setOnKeyReleased(keyEvent -> {
            // because the field is not necessary
            if (!keyPressure.getText().isEmpty())
                checkNumberInput(keyPressure, pressureError, false);
            else
                hideInputError(pressureError);
        });

        // clears the error for the text field and, checks the other necessary textFields
        keyTravel.setOnMouseClicked(mouseEvent -> {
            hideInputError(travelError);
            checkTextInput(componentBrand, brandError, "Enter a Brand");
            checkNameInput(componentName, nameError);
            checkDatePicker();
        });

        // check if the input is correct
        keyTravel.setOnKeyReleased(keyEvent -> {
            // because the filed is not necessary
            if (!keyTravel.getText().isEmpty())
                checkNumberInput(keyTravel, travelError, true);
            else
                hideInputError(travelError);
        });

        // closes the stage if the vales are correct and saved to the db
        saveBtn.setOnAction(event -> {
            if (checkValues()){
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * Passes the selected Keyboard from the Select Keyboard Window.
     * @param selectedKeyboard Keyboard that has been selected in the Window before
     */
    public void setSelectedKeyboard(Keyboard selectedKeyboard){
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
        componentBrand.setDisable(false);
        addedDate.setDisable(false);
    }

    /**
     * Disables all inputs and the date picker, if a component already exists.
     */
    private void disableAllInputs(){
        componentName.setDisable(true);
        componentBrand.setDisable(true);
        keyPressure.setDisable(true);
        keyTravel.setDisable(true);
        addedDate.setDisable(true);
    }

    /**
     * Name is disables sometimes so, we don't need to check.
     * Only checks the Input if it is not disabled.
     * @param input Input TextField
     * @param error Error Label
     */
    private void checkNameInput(TextField input, Label error){
        if (!input.isDisabled()){
            checkTextInput(input, error, "Enter a Name");
        }
    }

    /**
     * Checks if the input vales are Correct.
     * If the are Correct the values get's saved to the db.
     * @return If the Values are Correct or not.
     */
    private boolean checkValues(){
        // TODO check if the component is already in use, only key switches allowed
       checkTextInput(componentBrand, brandError, "Enter a Brand");
       addDate = getDateTime();
       System.out.println(addDate);
       if (!nameError.isVisible() && !brandError.isVisible() && !pressureError.isVisible() && !travelError.isVisible() && !dateError.isVisible()){
            String sqlStmt = "INSERT INTO components(keyboardId, componentType, componentName, componentBrand, " +
                    "keyPressure, keyTravel, keyStrokes, addDate) VALUES(?,?,?,?,?,?,?,?)";
           WriteDb.executeSqlStmt(sqlStmt, Integer.toString(selectedKeyboard.getKeyboardId()),
                   componentType.getSelectionModel().getSelectedItem(), componentName.getText(),
                   componentBrand.getText(), keyPressure.getText(), keyTravel.getText(),
                   Integer.toString(getComponentKeyStrokes()), addDate);
           return true;
       }
       else{
           return false;
       }
    }

    private String getDateTime(){
        String dateOption = addedDate.getSelectionModel().getSelectedItem();

        switch(dateOption){
            case "Today": return LocalDateTime.now().toString();
            case "Since Beginning": return selectedKeyboard.getInUseSince();
            case "Choose Date": return customDatePicker.getDateTime().toString();
            default: return null;
        }
    }

    /**
     * Get's the keyStrokes depending on the date option chosen at the date picker.
     * @return The KeyStokes depending on the Date option.
     */
    private int getComponentKeyStrokes(){
        switch(addedDate.getSelectionModel().getSelectedItem()){
            // get's all keyStrokes
            case "Since Beginning":
                return selectedKeyboard.getTotalKeyStrokes();

             // get's the keyStrokes since e specific date
            case "Choose Date":
                return getDateSpecificKeyStrokes();

            // "Today" is the default case
            default:
                return 0;
        }
    }

    /**
     * Reads the sum of the keystrokes form the totalToday Tables since a specific Date.
     * @return The total keyStrokes since a specific Date.
     */
    private int getDateSpecificKeyStrokes(){
        String selectedDate = chooseDateDatePicker.getValue().toString();
        String sqlStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE date >= '" + selectedDate + "' AND keyboardId = " + selectedKeyboard.getKeyboardId();
        return ReadDb.sumDateSpecificKeyStrokes(sqlStmt);
    }

    private ArrayList<String> getComponentTypes(){
        ArrayList<String> componentTypes = new ArrayList<>();
        // get's all the components objects
        String sqlStmt = "SELECT id, keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel, keyStrokes, addDate, " +
                "isActive FROM components WHERE keyboardId = " + selectedKeyboard.getKeyboardId();
        ObservableList<Component> components = ReadDb.selectAllValuesComponents(sqlStmt);

        for(Component component : components)
            componentTypes.add(component.getComponentType());

        return componentTypes;
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
    private void checkNumberInput(TextField inputLabel, Label errorLabel, boolean isInt){
        try{
            if (isInt)
                Integer.parseInt(inputLabel.getText());
            else
                Float.parseFloat(inputLabel.getText());

            hideInputError(errorLabel);

        } catch (NumberFormatException e){
            System.out.println(e.getMessage());

           if (isInt && !inputLabel.getText().isEmpty() && inputLabel.getText().contains("."))
                displayInputError(errorLabel, "Only full Numbers");
           else if (!inputLabel.getText().isEmpty())
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
