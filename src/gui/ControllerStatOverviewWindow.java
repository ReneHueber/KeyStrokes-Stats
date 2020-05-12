package gui;

import database.ReadDb;
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

public class ControllerStatOverviewWindow {

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
        // changes the scene to the select keyboard window
        selectKeyboard.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    Scene rootSelectKeyboard = new Scene(FXMLLoader.load(getClass().getResource("../fxml/selectKeyboardWindow.fxml")));

                    Stage stage = (Stage) menuBar.getScene().getWindow();
                    stage.setScene(rootSelectKeyboard);
                    stage.show();
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }

            }
        });

        //setKeyboardTableValue();
    }

    private ObservableList<Keyboards> getKeyboardValues(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards WHERE id = " + keyboardId;
        return ReadDb.selectAllValuesKeyboard(sqlStmt);
    }

    protected void setKeyboardTableValue(Keyboards selectedKeyboard){
        keyboardName.setText(selectedKeyboard.getKeyboardName());
        keyboardType.setText(selectedKeyboard.getKeyboardType());
        timeTyped.setText(Float.toString(selectedKeyboard.getTotalTimeKeyPressed()));
        keyStrokes.setText(Integer.toString(selectedKeyboard.getTotalKeyStrokes()));
    }

    public void setKeyboardId(int keyboardId){
        this.keyboardId = keyboardId;
    }
}
