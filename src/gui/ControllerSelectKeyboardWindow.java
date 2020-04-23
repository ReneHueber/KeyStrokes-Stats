package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import objects.Keyboards;

import java.net.URL;
import java.util.ResourceBundle;


public class ControllerSelectKeyboardWindow implements Initializable {

    @FXML
    private ListView<Keyboards> keyboardLv;

    private ObservableList<Keyboards> keyboardsObservableList;

    public ControllerSelectKeyboardWindow(){
        // initialize List
        keyboardsObservableList = FXCollections.observableArrayList();
        // load images
        Image ergoDox = new Image(ControllerMainWindow.class.getResource("../images/ergodox.png").toExternalForm());
        Image plank = new Image(ControllerMainWindow.class.getResource("../images/plank.png").toExternalForm());


        keyboardsObservableList.addAll(
                new Keyboards(ergoDox, "ErgoDox-Ez", 10000, 300.50f, "23.04.2020", "01.03.2020"),
                new Keyboards(plank, "Planke", 6000, 100.50f, "18.04.2020", "01.01.2020"),
                new Keyboards(ergoDox, "ErgoDox-Ez Work", 35000, 1000.50f, "21.04.2020", "01.06.2019")
        );
    }

    /**
     * Set's the Custom Items for the list view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyboardLv.setItems(keyboardsObservableList);
        keyboardLv.setCellFactory(KeyboardsListViewCell -> new KeyboardsListViewCell());
    }
}
