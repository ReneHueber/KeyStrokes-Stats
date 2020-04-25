package gui;

import database.ReadDb;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import keylogger.DbUpdateSchedule;
import keylogger.KeyLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ControllerMainWindow {

    @FXML Label refresh;
    @FXML Label key_strokes, key_travel, key_push_force;

    ReadDb dbConn = new ReadDb("/home/ich/Database/Keylogger/KeyStrokes.db");


    public void initialize(){
        setLabels();
    }


    public void refreshClicked(){
        setLabels();
    }

    public void startKeyLogger(ActionEvent event) throws IOException {
        // KeyLogger.setupKeyListener();
        // DbUpdateSchedule.startSchedule();

        Parent root = FXMLLoader.load(getClass().getResource("../fxml/selectKeyboardWindow.fxml"));
        Scene scene = new Scene(root);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
        // Image ergoDox = new Image(getClass().getResource("../images/ergodox.png").toExternalForm());
        // System.out.println(ergoDox);
    }

    private void setLabels(){
        int sum = setKeyStrokes();
        setKeyTravel(sum);
        setKeyPushForce(sum);
    }

    private int setKeyStrokes(){
        int sum = dbConn.getSum();
        key_strokes.setText(Integer.toString(sum));
        return sum;
    }

    private void setKeyTravel(int sum){
        float keyTravel = (float) sum * 0.003f;
        key_travel.setText(keyTravel + " M");
    }

    private void setKeyPushForce(int sum){
        int pushForce = sum * 50;
        key_push_force.setText(pushForce + " cN");
    }
}