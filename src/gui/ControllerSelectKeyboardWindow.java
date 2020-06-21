package gui;

import database.ReadDb;
import database.WriteDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import keylogger.KeyLogger;
import objects.Keyboard;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

// TODO Add Source "Icon made by Freepik from www.flaticon.com"
public class ControllerSelectKeyboardWindow implements Initializable {

    private boolean keyLoggerStarted = false;

    @FXML
    private ListView<Keyboard> keyboardLv;

    // Menu Keyboards
    @FXML
    private MenuItem addNew;
    @FXML
    private MenuItem showComponents, addComponents;

    // Menu Key Logger
    @FXML
    private MenuItem start, stop;
    @FXML
    private MenuItem reloadValues;

    // Menu Stats
    @FXML
    private MenuItem overview;
    @FXML
    private MenuItem detail;

    // Menu Help
    @FXML
    private MenuItem about;

    @FXML
    private MenuBar menuBar;

    private Keyboard selectedKeyboard;

    private ObservableList<Keyboard> keyboardsObservableList;

    private KeyLogger keyLogger;

    public ControllerSelectKeyboardWindow(){
        // creates a new Db if it is not existing
        WriteDb.createNewDb("KeyLoggerData.db", "/home/ich/Database/Keylogger/");
        // crates the needed tables
        createTables();
        // initialize List
        keyboardsObservableList = FXCollections.observableArrayList();
    }

    /**
     * Set's the Custom Items for the list view and the click Listeners.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // set's the items for the list view
        setupListView();
        // disables the menu items, because a keyboard has to be selected for them
        start.setDisable(true);
        stop.setDisable(true);
        overview.setDisable(true);
        detail.setDisable(true);
        showComponents.setDisable(true);
        addComponents.setDisable(true);

        // click listener for the addNew keyboard menu item
        addNew.setOnAction(actionEvent -> {
            // openAddKeyboardWindow();
            ProcessFxmlFiles openWindow = new ProcessFxmlFiles("../fxml/splitSelectedWindow.fxml", "Add Keyboard");
            ControllerAddKeyboardWindow controller = (ControllerAddKeyboardWindow) openWindow.openInNewStage();
            controller.parentStage = (Stage) menuBar.getScene().getWindow();
        });

        showComponents.setOnAction(actionEvent -> {
            ProcessFxmlFiles componentsWindow = new ProcessFxmlFiles("../fxml/componentsWindow.fxml", "Components");
            Stage stage = (Stage) menuBar.getScene().getWindow();
            ControllerComponentWindow controller = (ControllerComponentWindow) componentsWindow.openInExistingStage(stage);
            controller.setSelectedKeyboard(selectedKeyboard);
            controller.setInfoLabels();
            // get's all the Active components what is default
            String sqlStmt = "SELECT id, keyboardId, componentType, componentName, componentBrand, keyPressure, keyTravel, keyStrokes, addDate, retiredDate,  " +
                    "isActive FROM components WHERE keyboardId = " + selectedKeyboard.getKeyboardId() + " AND isActive = true";
            controller.setValuesTableView(sqlStmt);
            controller.setKeyLoggerStarted(keyLoggerStarted);
        });

        addComponents.setOnAction(event -> {
            ProcessFxmlFiles addComponentsWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Add Components");
            ControllerAddComponentWindow controller = (ControllerAddComponentWindow) addComponentsWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
            controller.checkComponentExisting("Key Switches");
        });

        overview.setOnAction(event -> {
            ProcessFxmlFiles overviewWindow = new ProcessFxmlFiles("../fxml/statOverviewWindow.fxml", "Statistic Overview");
            Stage stage = (Stage) menuBar.getScene().getWindow();
            ControllerStatOverviewWindow controller = (ControllerStatOverviewWindow) overviewWindow.openInExistingStage(stage);
            controller.setKeyboardTableValue(selectedKeyboard);
            controller.setKeyLoggerStarted(keyLoggerStarted);
            controller.setKeyLogger(keyLogger);
        });

        detail.setOnAction(event -> {
            ProcessFxmlFiles detailWindow = new ProcessFxmlFiles("../fxml/statDetailWindow.fxml", "Detail Statistic");
            ControllerStatDetailWindow controller = (ControllerStatDetailWindow) detailWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
            controller.reloadWindow(controller, "Key Strokes");
        });

        about.setOnAction(event -> {
        });

        // start the key logger if a keyboard is selected
        start.setOnAction(actionEvent -> {
            if (selectedKeyboard != null){
                CustomDialogWindow dialogWindow = new CustomDialogWindow("Started Key Logger for the Keyboard:", selectedKeyboard.getKeyboardName());
                keyLogger = new KeyLogger();
                keyLogger.setupKeyListener(selectedKeyboard.getKeyboardId());
                dialogWindow.show();
                disableAddKeyboardsAndComponents(true);
            }
        });

        stop.setOnAction(event -> {
            keyLogger.stopKeylogger();
            disableAddKeyboardsAndComponents(false);
            setupListView();
        });

        reloadValues.setOnAction(event -> setupListView());

        // get's the selected item if the selection is changed
        keyboardLv.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            selectedKeyboard = newValue;
            if (!keyLoggerStarted){
                // enables the menu items
                start.setDisable(false);
                overview.setDisable(false);
                detail.setDisable(false);
                showComponents.setDisable(false);
                addComponents.setDisable(false);
            }
        });
    }

    /**
     * Setup the list view and set's the values from the db.
     */
    public void setupListView(){
        keyboardLv.setCellFactory(KeyboardsListViewCell -> new KeyboardsListViewCell());
        updateListView();
        keyboardLv.setPlaceholder(new Label("No Keyboards added"));
        keyboardLv.setContextMenu(createContextMenu());
    }

    private void updateListView(){
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards";
        keyboardsObservableList = ReadDb.selectAllValuesKeyboard(sqlStmt);

        keyboardLv.setItems(keyboardsObservableList);
    }

    /**
     * Crates the Context Menu for the Keyboard List View.
     * @return Created Context Menu
     */
    private ContextMenu createContextMenu(){
        ContextMenu cm = new ContextMenu();

        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        edit.setOnAction(event -> {
            System.out.println("edit");
            ProcessFxmlFiles openEditWindow;
            if (keyboardLv.getSelectionModel().getSelectedItem().getLayout().equals("split")){
                openEditWindow = new ProcessFxmlFiles("../fxml/splitSelectedWindow.fxml", "Edit Keyboard");
            }
            else{
                openEditWindow = new ProcessFxmlFiles("../fxml/standardSelectedWindow.fxml", "Edit Keyboard");
            }


            ControllerAddKeyboardWindow controller = (ControllerAddKeyboardWindow) openEditWindow.openInNewStage();
            controller.parentStage = (Stage) menuBar.getScene().getWindow();
            controller.setSelectedKeyboard(keyboardLv.getSelectionModel().getSelectedItem());
            controller.changeGuiEditKeyboard(true);
            controller.keyboardStyle = selectedKeyboard.getLayout();
        });

        delete.setOnAction(event -> {
            WriteDb.deleteById("keyboards", keyboardLv.getSelectionModel().getSelectedItem().getKeyboardId());
            updateListView();
        });

        cm.getItems().addAll(edit, delete);
        return cm;
    }

    /**
     * Disable / Enables the menuItem to add a Keyboard or Component if the keyLogger is Running or not Running.
     * @param keyLoggerStarted Keylogger is Running or not.
     */
    protected void disableAddKeyboardsAndComponents(boolean keyLoggerStarted) {
        start.setDisable(keyLoggerStarted);
        stop.setDisable(!keyLoggerStarted);
        addNew.setDisable(keyLoggerStarted);
        addComponents.setDisable(keyLoggerStarted);
        this.keyLoggerStarted = keyLoggerStarted;
    }

    /**
     * Get's the Keylogger instance from the stat overview class, to stop it.
     * @param keyLogger Instance of KeyLogger class
     */
    protected void setKeyLogger(KeyLogger keyLogger){
        this.keyLogger = keyLogger;
    }

    /**
     * Creates all the needed Tables
     */
    private void createTables(){
        ArrayList<String> tables = new ArrayList<>();
        String keyboardTables = "CREATE TABLE IF NOT EXISTS keyboards (\n"
                + "     id INTEGER,\n"
                + "     keyboardName TEXT NOT NULL,\n"
                + "     keyboardType TEXT NOT NULL,\n"
                + "     layout TEXT NOT NULL,\n"
                + "     totKeystrokes INTEGER NOT NULL,\n"
                + "     totTimePressed REAL NOT NULL,\n"
                + "     usedSince DATE NOT NULL,\n"
                + "     lastUsed DATE NUT NULL,\n"
                + "     PRIMARY KEY (id)"
                + ");";
        tables.add(keyboardTables);

        String totalTodayTables = "CREATE TABLE IF NOT EXISTS totalToday (\n"
                + "     id INTEGER,\n"
                + "     keyboardId INTEGER NOT NULL,\n"
                + "     date DATE NOT NULL,\n"
                + "     keyStrokes INTEGER NOT NULL,\n"
                + "     timePressed REAL NOT NULL,\n"
                + "     PRIMARY KEY (id),\n"
                + "     FOREIGN KEY (keyboardId) REFERENCES keyboards(id) ON DELETE CASCADE"
                + ");";
        tables.add(totalTodayTables);

        String heatmapTable = "CREATE TABLE IF NOT EXISTS heatmap (\n"
                + "     id INTEGER,\n"
                + "     keyboardId INTEGER NOT NULL,\n"
                + "     date DATE NOT NULL,\n"
                + "     key Varchar(255) NOT NULL,\n"
                + "     pressed INTEGER NOT NULL,\n"
                + "     PRIMARY KEY (id),\n"
                + "     FOREIGN KEY (keyboardId) REFERENCES keyboards(id) ON DELETE CASCADE"
                + ");";
        tables.add(heatmapTable);

        String componentTable = "CREATE TABLE IF NOT EXISTS components(\n"
                + "     id INTEGER,\n"
                + "     keyboardId INTEGER NOT NULL,\n"
                + "     componentType Text NOT NULL,\n"
                + "     componentName Text,\n"
                + "     componentBrand Text,\n"
                + "     keyPressure REAL,\n"
                + "     keyTravel INTEGER,\n"
                + "     keyStrokes INTEGER,\n"
                + "     addDate Date NOT NULL,\n"
                + "     retiredDate Date NOT NULL,\n"
                + "     isActive BOOLEAN DEFAULT TRUE,\n"
                + "     PRIMARY KEY (id),\n"
                + "     FOREIGN KEY (keyboardId) REFERENCES keyboards(id) ON DELETE CASCADE"
                + ");";
        tables.add(componentTable);

        // creates all the tables in a loop
        for (String table : tables){
            WriteDb.createNewTable(table);
        }
    }
}
