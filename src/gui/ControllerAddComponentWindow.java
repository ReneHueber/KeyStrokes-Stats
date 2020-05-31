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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

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
    private Component selectedComponent;

    // today is the default value
    private String addDate = LocalDate.now().toString();

    private boolean editComponent = false;
    private boolean dateAdded = false;

    @FXML
    private Label heading;

    @FXML
    private ComboBox<String> componentType;
    @FXML
    private Label typeError;
    @FXML
    private Label typeDescription;

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
    private Label dateLabel;

    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;


    /**
     * Set's values for the gui elements.
     */
    public void initialize(){
        setupDatePicker();

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

        dateLabel.setOnMouseClicked(mouseEvent -> {
            if (editComponent && addedDate.isDisabled()){
                addedDate.setDisable(false);
                dateLabel.setText("Added At");
                dateAdded = true;
            }
            else if (editComponent && !addedDate.isDisabled()){
                addedDate.setDisable(true);
                dateLabel.setText("Change Date");
                dateAdded = false;
            }
        });

        // disable or enable the key pressure and key travel inputs
        componentType.setOnAction(event -> {
            String selectedOption = componentType.getSelectionModel().getSelectedItem();

            // if the component is not existing
            if (!checkComponentExisting(selectedOption)){
                hideInputError(typeError);
                saveBtn.setDisable(false);
                updateInputsOptions(selectedOption);
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

        chooseDateDatePicker.setOnAction(event -> hideInputError(dateError));

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
     * Checks if the passed component type is already existing.
     * Only multiple components of the type "Other" are allowed.
     * @param componentName Component name to check
     * @return True if component is already existing, false if not
     */
    protected boolean checkComponentExisting(String componentName){
        if (getComponentTypes().contains(componentName) && (!componentName.equals("Other"))){
            disableGuiComponentExists();
            return true;
        }
        return false;
    }

    /**
     * Disables all inputs, the date picker and the save button, if a component already exists.
     */
    private void disableGuiComponentExists(){
        displayInputError(typeError, "This component already exists!");
        componentName.setDisable(true);
        componentBrand.setDisable(true);
        keyPressure.setDisable(true);
        keyTravel.setDisable(true);
        addedDate.setDisable(true);
        saveBtn.setDisable(true);
    }

    /**
     * Updates the possible inputs depending on the component types that is chosen.
     * @param componentType Selected Component option
     */
    protected void updateInputsOptions(String componentType){
        if (componentType.equals("Key Switches")){
            enableAllOtherInputs();
        }
        else if (componentType.equals("Other")){
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
     * Formats the Date like "dd.MM.yyyy" and set's the current Date.
     */
    private void setupDatePicker(){
        chooseDateDatePicker.setConverter(datePickerConverter.getConverter());
        chooseDateDatePicker.setPromptText("dd.MM.yyyy");
        // user can only choose real dates
        chooseDateDatePicker.setEditable(false);
    }

    /**
     * Checks if the input vales are Correct.
     * If the are Correct the values get's saved to the db.
     * @return If the Values are Correct or not.
     */
    private boolean checkValues(){
       checkTextInput(componentBrand, brandError, "Enter a Brand");
       checkDatePicker();
       addDate = getDate();

       if (!nameError.isVisible() && !brandError.isVisible() && !pressureError.isVisible() && !travelError.isVisible() && !dateError.isVisible()){
           // saves a new component
           if (!editComponent){
               String sqlStmt = "INSERT INTO components(keyboardId, componentType, componentName, componentBrand, " +
                       "keyPressure, keyTravel, keyStrokes, addDate, retiredDate) VALUES(?,?,?,?,?,?,?,?,?)";
               WriteDb.executeWriteSqlStmt(sqlStmt, Integer.toString(selectedKeyboard.getKeyboardId()),
                       componentType.getSelectionModel().getSelectedItem(), componentName.getText(),
                       componentBrand.getText(), keyPressure.getText(), keyTravel.getText(),
                       Integer.toString(getComponentKeyStrokes()), addDate, "0000-00-00");
           }
           // updates an existing component
           else{
               String sqlStmt = "UPDATE components SET ";
               // get's the changes that have been made, first String is the db variable, second the new value
               HashMap<String, String> changes = getEditChanges();
               // adds the db variables to the sql string
               sqlStmt += addEditToSqlString(changes) + " WHERE id = " + selectedComponent.getId();
               // passes all the new values
               WriteDb.executeWriteSqlStmt(sqlStmt, changes.values().toArray(new String[0]));
           }
           return true;
       }
       else{
           return false;
       }
    }

    /**
     * Get's the Date option chosen and returns the corresponding date.
     * @return Chosen Date as String
     */
    private String getDate(){
        String dateOption = addedDate.getSelectionModel().getSelectedItem();

        switch(dateOption){
            case "Today": return LocalDate.now().toString();
            case "Since Beginning": return selectedKeyboard.getInUseSince();
            case "Choose Date": if (!chooseDateDatePicker.getEditor().getText().isEmpty())
                                    return chooseDateDatePicker.getValue().toString();
                                else
                                    return null;
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
                return getDateSpecificKeyStrokes(chooseDateDatePicker.getValue().toString());

            // "Today" is the default case
            default:
                return 0;
        }
    }

    /**
     * Reads the sum of the keystrokes form the totalToday Tables since a specific Date.
     * @return The total keyStrokes since a specific Date.
     */
    private int getDateSpecificKeyStrokes(String selectedDate){
        String sqlStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE date >= '" + selectedDate + "' AND keyboardId = " + selectedKeyboard.getKeyboardId();
        return ReadDb.sumDateSpecificKeyStrokes(sqlStmt);
    }

    /**
     * Get's all the Components from the selected Keyboard, and saves the component types in a list.
     * @return List of the component types
     */
    private ArrayList<String> getComponentTypes(){
        ArrayList<String> componentTypes = new ArrayList<>();
        // get's all the components objects
        String sqlStmt = "SELECT id, keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel, keyStrokes, addDate, retiredDate, " +
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

    /**
     * Check if changes have been made, and saves the database variable Name and the values of the changes.
     * @return Database variable Name, Changed Value
     */
    private HashMap<String, String> getEditChanges(){
        // TODO key stokes for the date, if the date is changed
        HashMap<String, String> changes = new HashMap<>();
        HashMap<TextField, String> inputs = new HashMap<>();
        inputs.put(componentName, selectedComponent.getComponentName());
        inputs.put(componentBrand, selectedComponent.getComponentBrand());
        inputs.put(keyTravel, Integer.toString(selectedComponent.getKeyTravel()));
        inputs.put(keyPressure, Float.toString(selectedComponent.getKeyPressure()));

        // goes throw all the input fields, and check if the current value is different to the already saved value
        // if it is different, the name of the input is saved and the new value
        for (TextField key : inputs.keySet()){
            if (!key.isDisabled()){
                if (!key.getText().equals(inputs.get(key))){
                    changes.put(key.getId(), key.getText());
                }
            }
        }
        // checks if the date has been changed
        if (!addedDate.isDisabled()){
            String chosenDate = getDate();
            changes.put("addDate", chosenDate);
            changes.put("keyStrokes", Integer.toString(getComponentKeyStrokes()));
        }
        return changes;
    }

    /**
     * Get all the database Variables that are chancing and returns them in the format:
     * "Value = ?, Value = ?"
     * @param changes The changes made
     * @return String with the changes in the format for Sql
     */
    private String addEditToSqlString(HashMap<String, String> changes){
        // adds all the changes database variables to the sql String
        String sqlValues = changes.entrySet()
                            .stream()
                            .map(e -> e.getKey() + " = ?")
                            .collect(Collectors.joining(", "));
        return sqlValues;
    }

    /**
     * Chances the gui to use the addComponentWindow as a editComponentWindow
     */
    protected void changeGuiEditComponent(){
        componentType.setVisible(false);
        typeDescription.setVisible(false);
        addedDate.setDisable(true);
        heading.setText("Edit Component");
        saveBtn.setText("Edit");
        editComponent = true;
        dateLabel.setText("Changed Date");
    }

    /**
     * Set's the Values for the saved Component.
     * @param selectedComponent Selected Component Object
     */
    protected void setComponentValues(Component selectedComponent){
        this.selectedComponent = selectedComponent;
        componentBrand.setText(selectedComponent.getComponentBrand());
        componentName.setText(selectedComponent.getComponentName());
        keyTravel.setText(Integer.toString(selectedComponent.getKeyTravel()));
        keyPressure.setText(Float.toString(selectedComponent.getKeyPressure()));
    }
}
