package gui;

import database.ReadDb;
import keylogger.DbUpdateSchedule;
import keylogger.KeyLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    public void startKeyLogger(){
        KeyLogger.setupKeyListener();
        DbUpdateSchedule.startSchedule();
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