package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerAddKeyboardWindow {
    @FXML
    ImageView splitLayoutIv, standardLayoutIv;
    @FXML
    TextField name;
    @FXML
    DatePicker datePicker;
    @FXML
    Label confirm;

    public void initialize() {
        // change the layout if you click the image view
        splitLayoutIv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                changesScenes(mouseEvent, "../fxml/splitSelectedWindow.fxml");
            }
        });

        // change the layout if you click the image view
        standardLayoutIv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                changesScenes(mouseEvent, "../fxml/standardSelectedWindow.fxml");
            }
        });
    }

    // passes the values pack to the select keyboard class and get's closed
    public void passValues(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/selectKeyboardWindow.fxml"));
        fxmlLoader.load();

        ControllerSelectKeyboardWindow controller = fxmlLoader.getController();
        controller.getKeyboardObject("Test", "Text");
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Changes the Scenes in the current window
     * @param event The Mouse click event of the Image View
     * @param fxmlLayout Path to fxml file that should be loaded
     */
    public void changesScenes(MouseEvent event, String fxmlLayout) {
        // get's the current stage and changes the scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlLayout));
            Parent rootSplitSelected = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(rootSplitSelected));
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}