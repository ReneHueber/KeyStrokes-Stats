package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

// TODO separate Classes
public class ControllerDialogWindow {

    @FXML
    private ImageView dialogImage;
    @FXML
    private Label dialogMassage;
    @FXML
    private Button confirmButton;


    public void initialize(){
        // close the window if you click the button
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * Set's the passed Values for the Gui
     * @param image Dialog Window Image
     * @param massage Dialog Window Massage
     */
    protected void setItemValues(Image image, String massage){
        dialogImage.setImage(image);
        dialogMassage.setText(massage);
    }
}
