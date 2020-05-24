package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomDialogWindow {
    private final String dialogHeading;
    private final String dialogMassage;

    public CustomDialogWindow(String dialogHeading, String dialogMassage){
        this.dialogHeading = dialogHeading;
        this.dialogMassage = dialogMassage;
    }

    public void show(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..//fxml/customDialogWindow.fxml"));
            Parent root = fxmlLoader.load();

            ControllerDialogWindow controller = fxmlLoader.getController();
            controller.setItemValues(dialogHeading, dialogMassage);

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
