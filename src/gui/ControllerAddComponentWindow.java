package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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
            "Tilt Set"
    );

    @FXML
    private ComboBox<String> componentType;

    @FXML
    private TextField componentName;
    @FXML
    private TextField componentBrand;

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
        chooseDateDatePicker.setVisible(false);
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

        // display and hides the date picker
        addedDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String option = addedDate.getSelectionModel().getSelectedItem();
                if (option.equals("Choose Date")){
                    chooseDateDatePicker.setVisible(true);
                }
                else {
                    chooseDateDatePicker.setVisible(false);
                    chooseDateDatePicker.setValue(null);
                }
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
    private boolean checkValues(){
        boolean valuesOkay = false;

        if (componentName.getText().isEmpty() || componentName.getText() != null) {
        
        }

        return valuesOkay;
    }
}
