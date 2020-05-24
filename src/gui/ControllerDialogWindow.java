package gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControllerDialogWindow {

    @FXML
    private Label dialogHeading, dialogMassage;
    @FXML
    private Label confirmLabel;


    public void initialize(){
        // closes the window is you click okay
        confirmLabel.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Set's the passed Values for the Gui
     * @param heading Dialog Window Heading
     * @param massage Dialog Window Massage
     */
    protected void setItemValues(String heading, String massage){
        dialogHeading.setText(heading);
        dialogMassage.setText(massage);
    }
}
