package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ControllerComponentWindow {

    @FXML
    private Label keyboardName;
    @FXML
    private Label keyboardType;
    @FXML
    private Label keyStrokes;
    @FXML
    private Label keyPressedTime;

    @FXML
    private Button addComponentsBtn;
    @FXML
    private ListView<String> componentsListView;

    public void initialize(){
        addComponentsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ProcessFxmlFiles addComponentWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Add Component");
                addComponentWindow.openInNewStage();
            }
        });
    }
}
