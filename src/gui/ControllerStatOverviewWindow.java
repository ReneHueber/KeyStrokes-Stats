package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
            "custom date"
    );

    private ObservableList<Keyboard> allKeyboards;
    private HashMap<Integer, ArrayList<Component>> keyboardComponents;
    ObservableList<LocalDate> selectedDates = FXCollections.observableArrayList();
    private LocalDate startDate;
    private LocalDate endDate;

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
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;

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
        setupStartDate();
        setupEndDate();

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

        // reloads the db values
        reload.setOnAction(event -> {
            allKeyboards = getKeyboardValues();
            setKeyboardTableValue(allKeyboards.get(selectKeyboardCB.getSelectionModel().getSelectedIndex()));
        });

        // show de detail information of the keyboard
        detail.setOnAction(event -> {
            ProcessFxmlFiles detailWindow = new ProcessFxmlFiles("../fxml/statDetailWindow.fxml", "Detail Statistic");
            ControllerStatDetailWindow controller = (ControllerStatDetailWindow) detailWindow.openInNewStage();
            controller.setSelectedKeyboard(allKeyboards.get(selectKeyboardCB.getSelectionModel().getSelectedIndex()));
            controller.reloadWindow(controller, "Key Strokes");
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
        setKeyStrokesAndTimeTyped(selectedKeyboard.getKeyboardId());
        setKeyTravelPressure(selectedKeyboard.getKeyboardId());
    }

    /**
     * Sum's the KeyStrokes and the timePressed from the totalToday Table, depending on the dateOption chosen.
     * @param keyboardId The id of the selected Keyboard
     */
    private void setKeyStrokesAndTimeTyped(int keyboardId){
        String dateOption = selectDateCB.getSelectionModel().getSelectedItem();
        String sqlKeyStrokesTimeStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE keyboardId = " + keyboardId;
        boolean readTable = true;

        // add's the right dates to the sql String
        switch(dateOption){
            case "today": sqlKeyStrokesTimeStmt += " AND date = '" + LocalDate.now().toString() + "'";
                          break;
            case "last 7 days": sqlKeyStrokesTimeStmt += " AND date >= '" + LocalDate.now().minusDays(7).toString() + "'";
                                break;
            case "last 30 days": sqlKeyStrokesTimeStmt += " AND date >= '" + LocalDate.now().minusDays(30).toString() + "'";
                                 break;
            case "this year": LocalDate date = LocalDate.now();
                              int dayOfYear = date.getDayOfYear();
                              sqlKeyStrokesTimeStmt += " AND date >= '" + date.minusDays(dayOfYear - 1).toString() + "'";
                              break;
            case "custom date": if (startDate != null && endDate != null)
                                    sqlKeyStrokesTimeStmt += " AND date >= '" + startDate.toString() + "' AND date <= '" + endDate.toString() + "'";
                                else if (startDate != null)
                                    sqlKeyStrokesTimeStmt += " AND date >= '" + startDate.toString() + "'";
                                else
                                    readTable = false;
                                break;
        }
        int keyStrokesValue = 0;
        float timePressedValue = 0.0f;
        if (readTable){
            keyStrokesValue = ReadDb.executeIntSumFunction(sqlKeyStrokesTimeStmt);
            sqlKeyStrokesTimeStmt = sqlKeyStrokesTimeStmt.replace("keyStrokes", "timePressed");
            timePressedValue = ReadDb.executeFloatSumFunction(sqlKeyStrokesTimeStmt);
        }

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
        if (selectDateCB.getSelectionModel().getSelectedItem().equals("custom date")){
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
            startLabel.setVisible(true);
            endLabel.setVisible(true);
        }
        else{
            startDatePicker.setVisible(false);
            endDatePicker.setVisible(false);
            startLabel.setVisible(false);
            endLabel.setVisible(false);
        }
    }

    /**
     * Set up the properties of the start datePicker.
     */
    private void setupStartDate(){
        startDatePicker.setConverter(datePickerConverter.getConverter());
        startDatePicker.setEditable(false);

        // set's the start value, opens the endDate DatePicker
        startDatePicker.setOnAction(event -> {
            startDate = startDatePicker.getValue();
            selectedDates.add(startDate);
            addDaysBetween();
            endDatePicker.show();
            setKeyStrokesAndTimeTyped(allKeyboards.get(selectKeyboardCB.getSelectionModel().getSelectedIndex()).getKeyboardId());
        });

        startDatePicker.setDayCellFactory((DatePicker param) -> new DateCell()
        {
            @Override
            public void updateItem(LocalDate item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item != null && !empty){
                    LocalDate currentDate = LocalDate.now();
                    if (item.getDayOfYear() > currentDate.getDayOfYear() || item.getYear() > currentDate.getYear()){
                        setDisable(true);
                    }
                    else {
                        // highlight the days that are selected
                        if (selectedDates.contains(item)) {
                            setStyle("-fx-background-color: rgba(88, 134, 209 0.7);");
                        }
                        else {
                            setStyle(null);
                            setStyle("-fx-text-fill: black;");
                        }
                    }
                }
            }
        });
    }

    /**
     * Setup the properties of the end datePicker.
     */
    private void setupEndDate(){
        endDatePicker.setConverter(datePickerConverter.getConverter());
        endDatePicker.setEditable(false);

        // event handler to keep the datePicker window open, a day has been chosen
        EventHandler<MouseEvent> mouseClickedEventHandler = (MouseEvent clickEvent) -> {
            boolean equalDates = false;
            if (clickEvent.getButton() == MouseButton.PRIMARY) {
                endDate = endDatePicker.getValue();
                equalDates = addDaysBetween();
            }
            // if dates are not equal the datePicker stays open
            if (!equalDates){
                endDatePicker.show();
                clickEvent.consume();
            }
            setKeyStrokesAndTimeTyped(allKeyboards.get(selectKeyboardCB.getSelectionModel().getSelectedIndex()).getKeyboardId());
        };

        // to add the eventHandler, disable the not possible days and set a different color for the selected days
        endDatePicker.setDayCellFactory((DatePicker param) -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !empty){
                    addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);

                    LocalDate currentDate = LocalDate.now();
                    // days that are smaller as the startDate get disabled
                    if ((item.getDayOfYear() < startDate.getDayOfYear() || item.getYear() < startDate.getYear()) ||
                            (item.getDayOfYear() > currentDate.getDayOfYear() || item.getYear() > currentDate.getYear())){
                        setDisable(true);
                    }
                    else {
                        setDisable(false);
                        // highlight the days that are selected
                        if (selectedDates.contains(item)) {
                            setStyle("-fx-background-color: rgba(88, 134, 209 0.7);");
                        }
                        else {
                            setStyle(null);
                            setStyle("-fx-text-fill: black;");
                        }
                    }
                }
                else
                    removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
            }
        });
    }

    /**
     * If the start & endDate are equal the Values of the endDatePicker get's cleared.
     * If the startDate is bigger than the endDate, the value of the endDate is the startDate plus one Day.
     * Otherwise all the days between start & endDate are selected.
     * @return True if the Dates are Equal
     */
    private boolean addDaysBetween() {
        // only check the days if both Dates are not null
        if (startDate != null && endDate != null){
            selectedDates.clear();
            // if the days are equal the vales of the end date are cleared
            if (startDate.toString().equals(endDate.toString())){
                endDate = null;
                endDatePicker.setValue(null);
                endDatePicker.getEditor().setText("");
                selectedDates.add(startDate);
                return true;
            }
            else {
                // if the start date is bigger than the end date, the end date is set to start date plus one day.
                if (startDate.getDayOfYear() > endDate.getDayOfYear() && (startDate.getYear() > endDate.getYear() || startDate.getYear() == endDate.getYear())){
                    selectedDates.add(startDate);
                    endDate = LocalDate.ofYearDay(startDate.getYear(), startDate.getDayOfYear() + 1);
                    endDatePicker.setValue(endDate);
                    selectedDates.add(endDate);
                }
                else{
                    for (int year = startDate.getYear(); year <= endDate.getYear(); year++){
                        // all values between the start & endDate are added to selected Dates
                        if (startDate.getYear() != endDate.getYear()){
                            int startDay = 1;
                            int endDay;

                            LocalDate currentYear = LocalDate.ofYearDay(year, 1);
                            if (year == startDate.getYear()){
                                startDay = startDate.getDayOfYear();
                                endDay = currentYear.lengthOfYear();
                            }
                            else if (year == endDate.getYear()){
                                endDay = endDate.getDayOfYear();
                            }
                            else {
                                endDay = currentYear.lengthOfYear();
                            }

                            for (int day = startDay; day <= endDay; day++){
                                LocalDate addDate = LocalDate.ofYearDay(year, day);
                                if (!selectedDates.contains(addDate)) {
                                    selectedDates.add(addDate);
                                }
                            }

                        }
                        else {
                            for (int i = startDate.getDayOfYear(); i <= endDate.getDayOfYear(); i++) {
                                LocalDate addDate = LocalDate.ofYearDay(year, i);
                                if (!selectedDates.contains(addDate)) {
                                    selectedDates.add(addDate);
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
        * after the values are equal, end date is set null
        * so the list has the be cleared because otherwise, if you only change the startDate,
        * the values are always added to the lis
        */
        else if (startDate != null){
            selectedDates.clear();
            selectedDates.add(startDate);
        }
        return false;
    }
}
