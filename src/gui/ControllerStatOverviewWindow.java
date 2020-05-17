package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import objects.Keyboards;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

public class ControllerStatOverviewWindow {

    private ObservableList<String> dateOptions = FXCollections.observableArrayList(
            "last 7 days",
            "last 30 days",
            "this year",
            "custom Date"
    );

    private ObservableList<Keyboards> allKeyboards;

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

        // changes the scene to the select keyboard window
        selectKeyboard.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ProcessFxmlFiles selectKeyboardWindow = new ProcessFxmlFiles("../fxml/selectKeyboardWindow.fxml", "Select Keyboard");
                Stage stage = (Stage) menuBar.getScene().getWindow();
                selectKeyboardWindow.openInExistingStage(stage);
            }
        });

        // updates the stat values if the keyboard is changed
        selectKeyboardCB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selectedKeyboard = selectKeyboardCB.getSelectionModel().getSelectedItem();
                Keyboards keyboard = getKeyboard(selectedKeyboard);
                if (keyboard != null){
                    setStatValues(keyboard);
                }
            }
        });
    }

    /**
     * Creates the List for the select Keyboard Combo box.
     * @return List of all Keyboard names.
     */
    private ObservableList<String> createSelectKeyboardOptions(){
        ObservableList<String> keyboardOptions = FXCollections.observableArrayList();

        for (Keyboards keyboard : allKeyboards){
            keyboardOptions.add(keyboard.getKeyboardName());
        }

        return keyboardOptions;
    }

    /**
     * Reads the Database and get's all the Keyboard Values.
     * @return List of all Keyboard Objects saved.
     */
    private ObservableList<Keyboards> getKeyboardValues(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards";
        return ReadDb.selectAllValuesKeyboard(sqlStmt);
    }

    /**
     * Gets the Keyboard Object with the passed Keyboard name.
     * @param selectedKeyboard The name of the searched Keyboard.
     * @return The searched Keyboard object.
     */
    private Keyboards getKeyboard(String selectedKeyboard){
        for(Keyboards keyboard : allKeyboards){
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
    protected void setKeyboardTableValue(Keyboards selectedKeyboard){
        setStatValues(selectedKeyboard);
        selectKeyboardCB.setValue(selectedKeyboard.getKeyboardName());
    }

    /**
     * Sets the stat Values of the selected Keyboard.
     * @param selectedKeyboard Selected Keyboard Object.
     */
    private void setStatValues(Keyboards selectedKeyboard){
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
        int[] divisors = {3600, 60};

        for(int divisor : divisors){
            float result = timeTyped / divisor;
            if (result >= 1){
                timeValues.add(String.format("%02d", (int) result));
                timeTyped -= divisor;
            }
            else {
                timeValues.add("00");
            }
        }
        timeValues.add(String.format("%02d", (int) timeTyped));

        return String.join(":", timeValues);
    }


    public void setKeyboardId(int keyboardId){
        this.keyboardId = keyboardId;
    }
}
