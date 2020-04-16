package keylogger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControllerMainWindow {

    @FXML Label refresh;
    @FXML Label key_strokes, key_travel, key_push_force;

    DbConnection dbConn = new DbConnection("/home/ich/Database/Keylogger/KeyStrokes.db");


    public void initialize(){
        int sum = dbConn.getSum();
        key_strokes.setText(Integer.toString(sum));
    }


    public void refreshClicked(){
        int sum = dbConn.getSum();
        key_strokes.setText(Integer.toString(sum));
    }
}
