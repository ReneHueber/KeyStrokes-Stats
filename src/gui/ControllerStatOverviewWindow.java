package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import objects.Component;
import objects.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;

public class ControllerStatOverviewWindow {

    private final ObservableList<String> dateOptions = FXCollections.observableArrayList(
            "last 7 days",
            "last 30 days",
            "this year",
            "custom Date"
    );

    private ObservableList<Keyboard> allKeyboards;
    private HashMap<Integer, ArrayList<Component>> keyboardComponents;

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

    public void initialize(){
        // get's all saved keyboards
        allKeyboards = getKeyboardValues();
        keyboardComponents = getKeyboardComponents();

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
     * Reads all KeySwitch Components from all available Keyboards.
     * Active and not Active Components.
     * @return HashMap with every KeySwitch Component for every Keyboard
     */
    private HashMap<Integer, ArrayList<Component>> getKeyboardComponents(){
        HashMap<Integer, ArrayList<Component>> keyboardComponents = new HashMap<>();
        // goes throw all the available keyboards and saves all keySwitch Components
        for (Keyboard keyboard : allKeyboards){
            String sqlStmt = "SELECT id, keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel," +
                    "keyStrokes, addDate, isActive FROM components WHERE keyboardId = " + keyboard.getKeyboardId() +
                    " AND componentType = 'Key Switches'";
            // list of all keySwitch components
            ObservableList<Component> readComponent = ReadDb.selectAllValuesComponents(sqlStmt);
            // saves all the keySwitch components with the current keyboardId in an hashmap
            if (readComponent.size() != 0)
                keyboardComponents.put(keyboard.getKeyboardId(), new ArrayList<>(readComponent));
            else
                keyboardComponents.put(keyboard.getKeyboardId(), null);

        }
        return keyboardComponents;
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
        setKeyTravelPressure(selectedKeyboard.getKeyboardId());
    }

    private void setKeyTravelPressure(int keyboardId){
        String[] totalValues = calculateTotalValues(keyboardId);

        if (Boolean.parseBoolean(totalValues[2])){
            keyTravel.setText(totalValues[0]);
            keyPressure.setText(totalValues[1]);
        }
        else {
            keyTravel.setText("No Data");
            keyPressure.setText("No Data");
        }
    }


    private String[] calculateTotalValues(int keyboardId){
        ArrayList<Component> components = keyboardComponents.get(keyboardId);
        int keyTravel = 0;
        float keyPressure = 0.0f;
        boolean keySwitchSelected = false;

        if (components != null){
            keySwitchSelected = true;
            for (Component component : components){
                keyTravel += component.getKeyStrokes() * component.getKeyTravel();
                keyPressure += component.getKeyStrokes() * component.getKeyPressure();
            }
        }

        return new String[] {Float.toString((float) keyTravel / 1000), Float.toString(keyPressure), Boolean.toString(keySwitchSelected)};
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

}
