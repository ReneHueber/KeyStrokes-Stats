package gui;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import objects.Component;
import objects.Keyboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ControllerComponentWindow {

    private final ObservableList<String> componentFilterOptionsList = FXCollections.observableArrayList(
            "Active Components",
            "Retired Components",
            "Active & Retired"
    );

    private Keyboard selectedKeyboard;
    private Stage stage;

    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem selectKeyboard;
    @FXML
    MenuItem overview;
    @FXML
    MenuItem detail;
    @FXML
    MenuItem about;

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
    private ComboBox<String> componentFilterCB;

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
    private TableColumn<Component, String> retiredColumn;
    @FXML
    private TableColumn<Keyboard, Integer> keyStrokesColumn;


    public void initialize(){

        selectKeyboard.setOnAction(event -> {
            ProcessFxmlFiles selectKeyboardWindow = new ProcessFxmlFiles("../fxml/selectKeyboardWindow.fxml", "Select Keyboard");
            selectKeyboardWindow.openInExistingStage((Stage) menuBar.getScene().getWindow());
        });

        overview.setOnAction(event -> {
            ProcessFxmlFiles statOverviewWindow = new ProcessFxmlFiles("../fxml/statOverviewWindow.fxml", "Statistic Overview");
            ControllerStatOverviewWindow controller = (ControllerStatOverviewWindow) statOverviewWindow.openInExistingStage((Stage) menuBar.getScene().getWindow());
            controller.setKeyboardTableValue(selectedKeyboard);
        });

        // opens a window to add a component
        addComponentsBtn.setOnAction(event -> {
            ProcessFxmlFiles addComponentWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Add Component");
            ControllerAddComponentWindow controller = (ControllerAddComponentWindow) addComponentWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
            controller.checkComponentExisting("Key Switches");
        });

        componentFilterCB.setOnAction(event -> {
            updateTableView();
        });


        setupTableView();
        componentFilterCB.setItems(componentFilterOptionsList);
        componentFilterCB.setValue(componentFilterOptionsList.get(0));

    }

    /**
     * Pass the selected Keyboard from another controller.
     * @param selectedKeyboard Passed selected Keyboard Object.
     */
    public void setSelectedKeyboard(Keyboard selectedKeyboard){
        this.selectedKeyboard = selectedKeyboard;
    }

    /**
     * Pass if the keylogger started from another Controller.
     * To disable the add Component Button.
     * @param keyLoggerStarted KeyLogger started
     */
    protected void setKeyLoggerStarted(boolean keyLoggerStarted){
        addComponentsBtn.setDisable(keyLoggerStarted);
    }

    /**
     * Set all the Cell Values for the Table view.
     */
    private void setupTableView(){
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("componentType"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("componentName"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("componentBrand"));
        addedColumn.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        retiredColumn.setCellValueFactory(new PropertyValueFactory<>("retiredDate"));
        keyStrokesColumn.setCellValueFactory(new PropertyValueFactory<>("keyStrokes"));
        componentTV.setContextMenu(createContextMenu());
    }

    /**
     * Creates the Context Menu for the Table View.
     * @return Context Menu for Table View
     */
    private ContextMenu createContextMenu(){
        // TODO finish contextMenu change style
        MenuItem retire = new MenuItem("Retire");
        MenuItem delete = new MenuItem("Delete");
        MenuItem edit = new MenuItem("Edit");
        MenuItem details = new MenuItem("Details");

        // functions for the menuItems
        retire.setOnAction(ActionEvent -> {
            // get's the selected Component and retires it
            Component selectedComponent = componentTV.getSelectionModel().getSelectedItem();
            String sqlStmt = "UPDATE components SET retiredDate = ?, isActive = false"
                            + " WHERE id = " + selectedComponent.getId();
            WriteDb.executeWriteSqlStmt(sqlStmt, LocalDate.now().toString());
            updateTableView();
        });

        delete.setOnAction(ActionEvent -> {
            // get's the selected Item and deletes it from the table
            Component selectedComponent = componentTV.getSelectionModel().getSelectedItem();
            WriteDb.deleteById("components", selectedComponent.getId());
            updateTableView();

        });

        edit.setOnAction(actionEvent -> {
            Component selectedComponent = componentTV.getSelectionModel().getSelectedItem();
            ProcessFxmlFiles editComponentWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Edit Component");
            ControllerAddComponentWindow controller = (ControllerAddComponentWindow) editComponentWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
            controller.setComponentValues(selectedComponent);
            controller.updateInputsOptions(selectedComponent.getComponentType());
            controller.changeGuiEditComponent();

        });

        details.setOnAction(actionEvent -> {
            Component selectedComponent = componentTV.getSelectionModel().getSelectedItem();
            ProcessFxmlFiles componentDetails = new ProcessFxmlFiles("../fxml/detailComponentInfoWindow.fxml", "Component Detail");
            ControllerDetailComponentInfoWindow controller = (ControllerDetailComponentInfoWindow) componentDetails.openInNewStage();
            controller.setValues(selectedComponent);
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(retire);
        menu.getItems().add(delete);
        menu.getItems().add(edit);
        menu.getItems().add(details);

        return menu;
    }

    /**
     * Updates the Table view depending on the filter option chosen.
     * Reads the values again from the Table.
     */
    private void updateTableView(){
        String selectedOption = componentFilterCB.getSelectionModel().getSelectedItem();
        String sqlStmt = "SELECT id, keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel, keyStrokes, addDate, retiredDate, " +
                "isActive FROM components WHERE keyboardId = " + selectedKeyboard.getKeyboardId();
        // adds an argument to the sql string
        switch (selectedOption){
            case "Active Components": sqlStmt += " AND isActive = true";
                break;
            case "Retired Components": sqlStmt += " AND isActive = false";
                break;
        }
        setValuesTableView(sqlStmt);
    }

    /**
     * Set the values for the Table view, from the controller that opens the window.
     * Default only Active components.
     */
    protected void setValuesTableView(String sqlStmt){
        ObservableList<Component> components = ReadDb.selectAllValuesComponents(sqlStmt);
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
            component.setRetiredDate(formatDate(component.getRetiredDate()));
        }
    }

    /**
     * Formats the Data into a new Format.
     * @param passDate Date as String in the Locale Date format (MM-dd-yyyy)
     * @return Data as String in the format (dd.MM.yyyy)
     */
    private String formatDate(String passDate){
        String formattedDate = "";
        if (!passDate.isEmpty() && !passDate.equals("0000-00-00")) {
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
        keyPressedTime.setText(Float.toString(selectedKeyboard.getRoundedTotalTimeKeyPressed()));
    }

}
