package gui;

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

public class ControllerAddComponentWindow {

    private final ObservableList<String> addedDateOptions = FXCollections.observableArrayList(
            "Today",
            "Since Beginning",
            "Choose Date"
    );

    private final ObservableList<String> componentsOptions = FXCollections.observableArrayList(
            "Key Caps",
            "Key Switches",
            "Tilt Set"
    );

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
                    chooseDateDatePicker.setValue(null);
                }
            }
        });

        // disable or enable the key pressure and key travel inputs
        componentType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (componentType.getSelectionModel().getSelectedItem().equals("Key Switches")){
                    keyPressure.setDisable(false);
                    keyTravel.setDisable(false);
                }
                else {
                    keyPressure.setText("");
                    keyPressure.setDisable(true);
                    keyTravel.setText("");
                    keyTravel.setDisable(true);
                }
                // reset's the error label and inputs
                hideInputError(nameError);
                hideInputError(brandError);
                hideInputError(pressureError);
                hideInputError(travelError);

                componentName.setText("");
                componentBrand.setText("");
                keyPressure.setText("");
                keyTravel.setText("");
            }
        });


        componentName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hideInputError(nameError);
                checkTextInput(componentBrand, brandError, "Enter a Brand");
                checkTextInput(keyPressure, pressureError, "Enter a Number");
                checkTextInput(keyTravel, travelError, "Enter a Number");
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
                checkTextInput(componentName, nameError, "Enter a Name");
                checkTextInput(keyPressure, pressureError, "Enter a Number");
                checkTextInput(keyTravel, travelError, "Enter a Number");
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
                checkTextInput(componentName, nameError, "Enter a Name");
                checkTextInput(componentBrand, brandError, "Enter a Brand");
                checkTextInput(keyTravel, travelError, "Enter a Number");
            }
        });

        keyPressure.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                checkTextInput(keyPressure, pressureError, "Enter a Number");
            }
        });

        keyTravel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hideInputError(travelError);
                checkTextInput(componentName, nameError, "Enter a Name");
                checkTextInput(componentBrand, brandError, "Enter a Brand");
                checkTextInput(keyPressure, pressureError, "Enter a Number");
            }
        });

        keyTravel.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                checkTextInput(keyTravel, travelError, "Enter a Number");
            }
        });


        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkValues();
            }
        });

        setUpDatePicker();
    }

    /**
     * Setup the date picker
     */
    private void setUpDatePicker(){
        chooseDateDatePicker.setConverter(datePickerConverter.getConverter());
        chooseDateDatePicker.setPromptText("dd.MM.yyyy");
        chooseDateDatePicker.setEditable(false);
    }

    // TODO finis the function
    private void checkValues(){
       checkNumberInput(keyPressure, pressureError);
       checkNumberInput(keyTravel, travelError);
       checkTextInput(componentName, nameError, "Enter a Name");
       checkTextInput(componentBrand, brandError, "Enter a Brand");

       if (!nameError.isVisible() && !brandError.isVisible() && !pressureError.isVisible() && !travelError.isVisible()){
            // TODO save to Db
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
