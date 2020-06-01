package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import keylogger.KeyLogger;
import objects.Component;
import objects.Keyboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ControllerStatOverviewWindow {

    private final ObservableList<String> dateOptions = FXCollections.observableArrayList(
            "all",
            "today",
            "last 7 days",
            "last 30 days",
            "this year",
            "custom Date"
    );

    private ObservableList<Keyboard> allKeyboards;
    private HashMap<Integer, ArrayList<Component>> keyboardComponents;

    private boolean keyLoggerStarted;
    private KeyLogger keyLogger;

    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem selectKeyboard;
    @FXML
    MenuItem reload;
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
        selectDateCB.setValue(dateOptions.get(0));

        selectKeyboardCB.setItems(createSelectKeyboardOptions());
        setupDatePicker();

        // changes the scene to the select keyboard window
        selectKeyboard.setOnAction(actionEvent -> {
            ProcessFxmlFiles selectKeyboardWindow = new ProcessFxmlFiles("../fxml/selectKeyboardWindow.fxml", "Select Keyboard");
            Stage stage = (Stage) menuBar.getScene().getWindow();
            ControllerSelectKeyboardWindow controller = (ControllerSelectKeyboardWindow) selectKeyboardWindow.openInExistingStage(stage);
            // only if the keylogger started we have to disable the menu bar items
            if (keyLoggerStarted)
                controller.disableAddKeyboardsAndComponents(true);
            controller.setKeyLogger(keyLogger);
        });

        reload.setOnAction(event -> {
            allKeyboards = getKeyboardValues();
            setKeyboardTableValue(allKeyboards.get(selectKeyboardCB.getSelectionModel().getSelectedIndex()));
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
            // set's the new stat values
            setStatValues(allKeyboards.get(selectKeyboardCB.getSelectionModel().getSelectedIndex()));
            // checks if the date picker should be displayed
            customDateSelected();
        });

        about.setOnAction(event -> {
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

    // TODO add date options
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
                    "keyStrokes, addDate, retiredDate, isActive FROM components WHERE keyboardId = " + keyboard.getKeyboardId() +
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
     * Set's the value if the keylogger is started at the select keyboard class.
     * @param keyLoggerStarted If keylogger started
     */
    protected void setKeyLoggerStarted(boolean keyLoggerStarted){
        this.keyLoggerStarted = keyLoggerStarted;
    }

    /**
     * Set's the instance of the keylogger class, to stop the keylogger at the select keyboard window.
     * @param keyLogger Instance of the keylogger class
     */
    protected void setKeyLogger(KeyLogger keyLogger){
        this.keyLogger = keyLogger;
    }

    /**
     * Sets the stat Values of the selected Keyboard.
     * @param selectedKeyboard Selected Keyboard Object.
     */
    private void setStatValues(Keyboard selectedKeyboard){
        keyboardName.setText(selectedKeyboard.getKeyboardName());
        keyboardType.setText(selectedKeyboard.getKeyboardType());
        getKeyStrokesAndTimeTyped(selectedKeyboard.getKeyboardId());
        setKeyTravelPressure(selectedKeyboard.getKeyboardId());
    }

    // TODO date option for custom date, date picker select to values
    /**
     * Sum's the KeyStrokes and the timePressed from the totalToday Table, depending on the dateOption chosen.
     * @param keyboardId The id of the selected Keyboard
     */
    private void getKeyStrokesAndTimeTyped(int keyboardId){
        String dateOption = selectDateCB.getSelectionModel().getSelectedItem();
        String sqlStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE keyboardId = " + keyboardId;

        // add's the right dates to the sql String
        switch(dateOption){
            case "today": sqlStmt += " AND date = '" + LocalDate.now().toString() + "'";
                          break;
            case "last 7 days": sqlStmt += " AND date >= '" + LocalDate.now().minusDays(7).toString() + "'";
                                break;
            case "last 30 days": sqlStmt += " AND date >= '" + LocalDate.now().minusDays(30).toString() + "'";
                                 break;
            case "this year": LocalDate date = LocalDate.now();
                              int dayOfYear = date.getDayOfYear();
                              sqlStmt += " AND date >= '" + date.minusDays(dayOfYear - 1).toString() + "'";
        }
        int keyStrokesValue = ReadDb.executeIntSumFunction(sqlStmt);
        sqlStmt = sqlStmt.replace("keyStrokes", "timePressed");
        float timePressedValue = ReadDb.executeFloatSumFunction(sqlStmt);

        // set's the labels
        keyStrokes.setText(Integer.toString(keyStrokesValue));
        timeTyped.setText(formatTimeTyped(timePressedValue));
    }

    /**
     * Set's the values for the key Travel and key Pressure Labels.
     * @param keyboardId The keyboard id to get the values from the selected Keyboard
     */
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


    /**
     * Calculates the key Travel and key Pressure values, with the values from the table.
     * @param keyboardId The keyboard id to get the values from the selected Keyboard
     * @return KeyTravel, KeyPressure and if components are existing (all as String)
     */
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
