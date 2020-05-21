package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import objects.Component;
import objects.Keyboard;

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
    private TableView<Component> componentTV;
    @FXML
    private TableColumn<Component, String> typeColumn;
    @FXML
    private TableColumn<Component, String> nameColumn;
    @FXML
    private TableColumn<Component, String> brandColumn;
    @FXML
    private TableColumn<Component, String> addedColumn;
    @FXML
    private TableColumn<Component, String> removedColumn;
    @FXML
    private TableColumn<Keyboard, Integer> keyStrokesColumn;


    public void initialize(){
        addComponentsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ProcessFxmlFiles addComponentWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Add Component");
                addComponentWindow.openInNewStage();
            }
        });
        ObservableList<Component> components = FXCollections.observableArrayList(
                new Component(1, "test", "test", "test", 12.0f, 10, "15", "",1150, true)
        );
        componentTV.setItems(components);
        setupTableView();
    }

    private void setupTableView(){
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("componentType"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("componentName"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("componentBrand"));
        addedColumn.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        removedColumn.setCellValueFactory(new PropertyValueFactory<>("removedDate"));
        keyStrokesColumn.setCellValueFactory(new PropertyValueFactory<>("keyStrokes"));
    }
}
