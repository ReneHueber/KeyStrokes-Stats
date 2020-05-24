package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import objects.Keyboard;

import java.util.ArrayList;

public class ControllerStatOverviewWindow {

    private final ObservableList<String> dateOptions = FXCollections.observableArrayList(
            "last 7 days",
            "last 30 days",
            "this year",
            "custom Date"
    );

    private ObservableList<Keyboard> allKeyboards;

    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem selectKeyboard;
    @FXML
    MenuItem detail;
    @FXML
    MenuItem about;

    @FXML
    private ComboBox<String> selectDateCB;
    @FXML
    private ComboBox<String> selectKeyboardCB;
    @FXML
    private DatePicker customDate;

    @FXML
    private Label keyboardType;
    @FXML
    private Label keyboardName;

    @FXML
    private Label timeTyped;

    @FXML
    private Label keyStrokes;
    @FXML
    private Label keyTravel;
    @FXML
    private Label keyPressure;

    private int keyboardId;

    public void initialize(){
        // get's all saved keyboards
        allKeyboards = getKeyboardValues();

        // set's the options for the checkboxes
        selectDateCB.setItems(dateOptions);
        selectDateCB.setValue(dateOptions.get(1));

        selectKeyboardCB.setItems(createSelectKeyboardOptions());
        setupDatePicker();

        // changes the scene to the select keyboard window
        selectKeyboard.setOnAction(actionEvent -> {
            ProcessFxmlFiles selectKeyboardWindow = new ProcessFxmlFiles("../fxml/selectKeyboardWindow.fxml", "Select Keyboard");
            Stage stage = (Stage) menuBar.getScene().getWindow();
            selectKeyboardWindow.openInExistingStage(stage);
        });

        // updates the stat values if the keyboard is changed
        selectKeyboardCB.setOnAction(event -> {
            String selectedKeyboard = selectKeyboardCB.getSelectionModel().getSelectedItem();
            Keyboard keyboard = getKeyboard(selectedKeyboard);
            if (keyboard != null){
                setStatValues(keyboard);
            }
        });

        selectDateCB.setOnAction(event -> {
            // checks if the date picker should be displayed
            customDateSelected();
        });
    }

    /**
     * Creates the List for the select Keyboard Combo box.
     * @return List of all Keyboard names.
     */
    private ObservableList<String> createSelectKeyboardOptions(){
        ObservableList<String> keyboardOptions = FXCollections.observableArrayList();

        for (Keyboard keyboard : allKeyboards){
            keyboardOptions.add(keyboard.getKeyboardName());
        }

        return keyboardOptions;
    }

    /**
     * Reads the Database and get's all the Keyboard Values.
     * @return List of all Keyboard Objects saved.
     */
    private ObservableList<Keyboard> getKeyboardValues(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards";
        return ReadDb.selectAllValuesKeyboard(sqlStmt);
    }

    /**
     * Gets the Keyboard Object with the passed Keyboard name.
     * @param selectedKeyboard The name of the searched Keyboard.
     * @return The searched Keyboard object.
     */
    private Keyboard getKeyboard(String selectedKeyboard){
        for(Keyboard keyboard : allKeyboards){
            if (keyboard.getKeyboardName().equals(selectedKeyboard)){
                return keyboard;
            }
        }
        return null;
    }

    /**
     * Sets the values from the select keyboard Controller.
     * @param selectedKeyboard Selected Keyboard Object.
     */
    protected void setKeyboardTableValue(Keyboard selectedKeyboard){
        setStatValues(selectedKeyboard);
        selectKeyboardCB.setValue(selectedKeyboard.getKeyboardName());
    }

    /**
     * Sets the stat Values of the selected Keyboard.
     * @param selectedKeyboard Selected Keyboard Object.
     */
    private void setStatValues(Keyboard selectedKeyboard){
        keyboardName.setText(selectedKeyboard.getKeyboardName());
        keyboardType.setText(selectedKeyboard.getKeyboardType());
        timeTyped.setText(formatTimeTyped(selectedKeyboard.getTotalTimeKeyPressed()));
        keyStrokes.setText(Integer.toString(selectedKeyboard.getTotalKeyStrokes()));
    }

    /**
     * Formats the Time typed(s) in hh:mm:ss.
     * @param timeTyped time typed in seconds
     * @return string of time typed in the format hh:mm:ss
     */
    private String formatTimeTyped(float timeTyped){
        ArrayList<String> timeValues = new ArrayList<>();
        // time units (hh, mm)
        int[] timeUnits = {3600, 60};

        // calculates biggest possible values of the given time units
        for(int divisor : timeUnits){
            float result = timeTyped / divisor;
            if (result >= 1){
                timeValues.add(String.format("%02d", (int) result));
                timeTyped -= divisor;
            }
            else {
                timeValues.add("00");
            }
        }
        // the rest are seconds
        timeValues.add(String.format("%02d", (int) timeTyped));

        return String.join(":", timeValues);
    }

    /**
     * Displays or hides the date picker.
     */
    private void customDateSelected(){
        if (selectDateCB.getSelectionModel().getSelectedItem().equals("custom Date")){
            customDate.setVisible(true);
        }
        else{
            customDate.setVisible(false);
            customDate.setValue(null);
        }
    }

    /**
     * Set up the properties of the date picker.
     */
    private void setupDatePicker(){
        customDate.setConverter(datePickerConverter.getConverter());
        customDate.setEditable(false);
    }


    public void setKeyboardId(int keyboardId){
        this.keyboardId = keyboardId;
    }
}
