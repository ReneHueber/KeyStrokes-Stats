package gui;

import database.ReadDb;
import database.WriteDb;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import keylogger.KeyLogger;
import objects.Keyboard;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

// TODO Add Source "Icon made by Freepik from www.flaticon.com"
public class ControllerSelectKeyboardWindow implements Initializable {

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

    // Menu Stats
    @FXML
    private MenuItem overview;

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
            ControllerComponentWindow controller = (ControllerComponentWindow) componentsWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
            controller.setInfoLabels();
        });

        addComponents.setOnAction(event -> {
            ProcessFxmlFiles addComponentsWindow = new ProcessFxmlFiles("../fxml/addComponentWindow.fxml", "Add Components");
            ControllerAddComponentWindow controller = (ControllerAddComponentWindow) addComponentsWindow.openInNewStage();
            controller.setSelectedKeyboard(selectedKeyboard);
        });

        overview.setOnAction(event -> {
            ProcessFxmlFiles overviewWindow = new ProcessFxmlFiles("../fxml/statOverviewWindow.fxml", "Statistic Overview");
            Stage stage = (Stage) menuBar.getScene().getWindow();
            ControllerStatOverviewWindow controller = (ControllerStatOverviewWindow) overviewWindow.openInExistingStage(stage);
            controller.setKeyboardId(selectedKeyboard.getKeyboardId());
            controller.setKeyboardTableValue(selectedKeyboard);
        });

        // start the key logger if a keyboard is selected
        start.setOnAction(actionEvent -> {
            if (selectedKeyboard != null){
                start.setDisable(true);
                stop.setDisable(false);
                CustomDialogWindow dialogWindow = new CustomDialogWindow("Started Key Logger for the Keyboard:", selectedKeyboard.getKeyboardName());
                keyLogger = new KeyLogger();
                keyLogger.setupKeyListener(selectedKeyboard.getKeyboardId());
                dialogWindow.show();
            }
        });

        stop.setOnAction(event -> {
            keyLogger.stopKeylogger();
            start.setDisable(false);
            stop.setDisable(true);
        });

        // get's the selected item if the selection is changed
        keyboardLv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Keyboard>() {
            @Override
            public void changed(ObservableValue<? extends Keyboard> observableValue, Keyboard oldValue, Keyboard newValue) {
                selectedKeyboard = newValue;
                // enables the menu items
                start.setDisable(false);
                overview.setDisable(false);
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
        String sqlStmt = "SELECT id, keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards";
        keyboardsObservableList = ReadDb.selectAllValuesKeyboard(sqlStmt);

        if (keyboardsObservableList != null)
            formatDate(keyboardsObservableList);

        keyboardLv.setItems(keyboardsObservableList);
        keyboardLv.setPlaceholder(new Label("No Keyboards added"));
    }

    /**
     * Formats the Date in the List for every Keyboard
     * @param keyboardList List of the used Keyboards
     */
    private void formatDate(ObservableList<Keyboard> keyboardList){
        for (Keyboard keyboard : keyboardList){
            // formats the dates and sets the values
            String lastUsed = keyboard.getLastUsed();
                if (lastUsed.equals("0000-00-00"))
                    keyboard.setLastUsed("never");
                else
                    keyboard.setLastUsed(formatStringDate(lastUsed));

            keyboard.setInUseSince(formatStringDate(keyboard.getInUseSince()));

            // round the float
            DecimalFormat df = new DecimalFormat("#.00");
            String timePressed = df.format(keyboard.getTotalTimeKeyPressed());
            timePressed = timePressed.replace(",", ".");
            keyboard.setTotalTimeKeyPressed(Float.parseFloat(timePressed));
        }
    }

    /**
     * Get's a string date and formats it, returns it as a String.
     * @param date Date as String
     * @return Formatted Date as String
     */
    private String formatStringDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return formatter.format(LocalDate.parse(date));
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
