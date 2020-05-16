package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

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
        // adds the lists to the combo boxes and sets a value
        addedDate.setItems(addedDateOptions);
        addedDate.setValue(addedDateOptions.get(0));

        componentType.setItems(componentsOptions);
        componentType.setValue(componentsOptions.get(1));
    }

}
