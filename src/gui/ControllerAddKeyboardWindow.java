package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import objects.Keyboards;

import java.io.IOException;

public class ControllerAddKeyboardWindow {
    @FXML
    private TextField input1, input2;


    // passes the values pack to the select keyboard class and get's closed
    public void passValues(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/selectKeyboardWindow.fxml"));
        fxmlLoader.load();

        ControllerSelectKeyboardWindow controller = fxmlLoader.getController();
        controller.getKeyboardObject("Test", "Text");
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void splitLayoutClicked(){
        System.out.println("split clicked");
    }

    public void standardLayoutClicked(){
        System.out.println("standard clicked");
    }

}