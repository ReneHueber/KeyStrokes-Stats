package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomDialogWindow {
    private Image dialogImage;
    private String dialogMassage;

    public CustomDialogWindow(Image dialogImage, String dialogMassage){
        this.dialogImage = dialogImage;
        this.dialogMassage = dialogMassage;
    }

    public void show(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..//fxml/customDialogWindow.fxml"));
            Parent root = (Parent) fxmlLoader.load();

            ControllerDialogWindow controller = fxmlLoader.getController();
            controller.setItemValues(dialogImage, dialogMassage);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("KeyLogger Info");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }


    }
}
