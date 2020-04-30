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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import objects.Keyboards;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


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

    // TODO change labels to menu bar
    /**
     * Set's the Custom Items for the list view and the click Listeners.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                    System.out.println("Item started: " + selectedKeyboard.getKeyboardName());
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
        String sqlStmt = "SELECT keyboardName, keyboardType, layout, totKeystrokes, totTimePressed, usedSince, lastUsed " +
                "FROM keyboards";
        keyboardsObservableList = ReadDb.selectValuesKeyboard(sqlStmt);
        keyboardLv.setItems(keyboardsObservableList);
        keyboardLv.setPlaceholder(new Label("No Keyboards added"));
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
                + "     id INTEGER PRIMARY KEY,\n"
                + "     keyboardName TEXT NOT NULL,\n"
                + "     keyboardType TEXT NOT NULL,\n"
                + "     layout TEXT NOT NULL,\n"
                + "     totKeystrokes INTEGER NOT NULL,\n"
                + "     totTimePressed REAL NOT NULL,\n"
                + "     usedSince DATE NOT NULL,\n"
                + "     lastUsed DATE NUT NULL\n"
                + ");";
        tables.add(keyboardTables);

        // creates all the tables in a loop
        for (String table : tables){
            WriteDb.createNewTable(table);
        }
    }
}
