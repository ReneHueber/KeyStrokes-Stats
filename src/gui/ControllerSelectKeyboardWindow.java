package gui;

import database.ReadDb;
import database.WriteDb;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import keylogger.KeyLogger;
import objects.Keyboards;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

// TODO Add Source "Icon made by Freepik from www.flaticon.com"
public class ControllerSelectKeyboardWindow implements Initializable {

    @FXML
    private ListView<Keyboards> keyboardLv;

    // Menu Keyboards
    @FXML
    private MenuItem addNew;

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

    private Keyboards selectedKeyboard;

    private ObservableList<Keyboards> keyboardsObservableList;

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

        // click listener for the addNew keyboard menu item
        addNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                openAddKeyboardWindow();
            }
        });

        // start the key logger if a keyboard is selected
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (selectedKeyboard != null){
                    CustomDialogWindow dialogWindow = new CustomDialogWindow("Started Key Logger for the Keyboard:", selectedKeyboard.getKeyboardName());
                    KeyLogger keyLogger = new KeyLogger();
                    keyLogger.setupKeyListener(selectedKeyboard.getKeyboardId());
                    dialogWindow.show();
                }
            }
        });

        // get's the selected item if the selection is changed
        keyboardLv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Keyboards>() {
            @Override
            public void changed(ObservableValue<? extends Keyboards> observableValue, Keyboards oldValue, Keyboards newValue) {
                selectedKeyboard = newValue;
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
    private void formatDate(ObservableList<Keyboards> keyboardList){
        for (Keyboards keyboard : keyboardList){
            // formats the dates and sets the values
            String lastUsed = keyboard.getLastUsed();
                if (lastUsed.equals("0000-00-00"))
                    keyboard.setLastUsed("never");
                else
                    keyboard.setLastUsed(formatStringDate(lastUsed));

            keyboard.setInUseSince(formatStringDate(keyboard.getInUseSince()));

            DecimalFormat df = new DecimalFormat("#.00");
            keyboard.setTotalTimeKeyPressed(Float.parseFloat(df.format(keyboard.getTotalTimeKeyPressed())));
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
     * Opens the Window to add a new Keyboard.
     * Passes the Stage of the current Window to reload the fxml file from the new Controller.
     */
    private void openAddKeyboardWindow(){
        try {
            // loads the fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/splitSelectedWindow.fxml"));
            Parent rootAddKeyboard = (Parent) fxmlLoader.load();
            /*
             * get's the controller and passes the stage,
             * so the select keyboard stage can be reloaded from the add keyboard controller.
             * After a keyboard is added to the database
             */
            ControllerAddKeyboardWindow controller = fxmlLoader.getController();
            controller.parentStage = (Stage) menuBar.getScene().getWindow();

            // creates the stage and set's the property's
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Keyboard");
            stage.setScene(new Scene(rootAddKeyboard));
            stage.show();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
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

        // creates all the tables in a loop
        for (String table : tables){
            WriteDb.createNewTable(table);
        }
    }
}
