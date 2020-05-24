package gui;

import database.ReadDb;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import objects.Component;
import objects.Keyboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ControllerComponentWindow {

    private Keyboard selectedKeyboard;

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
        // opens a window to add a component
        addComponentsBtn.setOnAction(event -> {
            ProcessFxmlFiles addComponentWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Add Component");
            ControllerAddComponentWindow controller = (ControllerAddComponentWindow) addComponentWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
        });
        setupTableView();
    }

    /**
     * Pass the selected Keyboard from another controller.
     * @param selectedKeyboard Passed selected Keyboard Object.
     */
    public void setSelectedKeyboard(Keyboard selectedKeyboard){
        this.selectedKeyboard = selectedKeyboard;
    }

    /**
     * Set all the Cell Values for the Table view.
     */
    private void setupTableView(){
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("componentType"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("componentName"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("componentBrand"));
        addedColumn.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        removedColumn.setCellValueFactory(new PropertyValueFactory<>("removedDate"));
        keyStrokesColumn.setCellValueFactory(new PropertyValueFactory<>("keyStrokes"));
    }

    /**
     * Set the values for the Table view, from the controller that opens the window.
     */
    protected void setValuesTableView(){
        String sqlStmt = "SELECT keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel, keyStrokes, addDate, " +
                "isActive FROM components WHERE keyboardId = " + selectedKeyboard.getKeyboardId();

        ObservableList<Component> components = ReadDb.getAllValuesComponents(sqlStmt);
        formatDates(components);
        componentTV.setItems(components);
    }


    /**
     * Formats the add Date and remove Date values in the whole list.
     * @param components Passed List
     */
    private void formatDates(ObservableList<Component> components){
        for (Component component : components){
            component.setAddedDate(formatDate(component.getAddedDate()));
            component.setRemovedDate(formatDate(component.getRemovedDate()));
        }
    }

    /**
     * Formats the Data into a new Format.
     * @param passDate Date as String in the Locale Date format (MM-dd-yyyy)
     * @return Data as String in the format (dd.MM.yyyy)
     */
    private String formatDate(String passDate){
        String formattedDate = "";
        if (!passDate.isEmpty()) {
            LocalDate date = LocalDate.parse(passDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formattedDate = formatter.format(date);
        }
        return formattedDate;
    }

    /**
     * Set's the info Label with the keyboard Values from the Controller that opens the Window.
     */
    public void setInfoLabels(){
        keyboardName.setText(selectedKeyboard.getKeyboardName());
        keyboardType.setText(selectedKeyboard.getKeyboardType());
        keyStrokes.setText(Integer.toString(selectedKeyboard.getTotalKeyStrokes()));
        keyPressedTime.setText(Float.toString(selectedKeyboard.getTotalTimeKeyPressed()));
    }

}
