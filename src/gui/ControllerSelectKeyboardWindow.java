package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import objects.Keyboards;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ControllerSelectKeyboardWindow implements Initializable {

    @FXML
    private ListView<Keyboards> keyboardLv;
    @FXML
    private Label addKeyboardLb;

    private ObservableList<Keyboards> keyboardsObservableList;

    public ControllerSelectKeyboardWindow(){
        // initialize List
        keyboardsObservableList = FXCollections.observableArrayList();
    }

    /**
     * Set's the Custom Items for the list view and the click Listeners.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupListView();

        // mouse click listener for the add keyboard label
        addKeyboardLb.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
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
                    controller.parentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

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
    }
}
