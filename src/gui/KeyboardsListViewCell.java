package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import objects.Keyboard;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Set's the custom Items for every Cell in the List View.
 */
public class KeyboardsListViewCell extends ListCell<Keyboard> {

    @FXML
    private Label labelKeyboardName;

    @FXML
    private ImageView imageViewKeyboard;

    @FXML
    private Label labelKeyStrokes;

    @FXML
    private Label labelTimePressed;

    @FXML
    private Label labelLastUsed;

    @FXML
    private Label labelInUseSince;

    @FXML
    private VBox vBox;

    private FXMLLoader fxmlLoader;

    /**
     * Set's the custom Items.
     * Is called for every visible Item in the List View.
     * @param keyboard Object of the List View
     * @param empty If the Object is empty
     */
    @Override
    protected void updateItem(Keyboard keyboard, boolean empty) {
        super.updateItem(keyboard, empty);

        // loads the fxml file if the passed object is not empty
        if(empty || keyboard == null) {
            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/keyboardListCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            // set's the values for the gui elements
            labelKeyboardName.setText(keyboard.getKeyboardName());
            labelKeyStrokes.setText(String.valueOf(keyboard.getTotalKeyStrokes()));
            labelTimePressed.setText(roundFloat(keyboard.getTotalTimeKeyPressed() / 60) + " min");
            labelLastUsed.setText(keyboard.getFormattedLastUsed());
            labelInUseSince.setText(keyboard.getFormattedInUseSince());
            imageViewKeyboard.setImage(keyboard.getKeyboardImage());

            // set the custom layout
            setText(null);
            setGraphic(vBox);
        }
    }

    /**
     * Rounds a float Number to two decimal places
     * @param number Number to round
     * @return rounded Number
     */
    private float roundFloat(float number){
        DecimalFormat df = new DecimalFormat("#.00");
        String roundNumber = df.format(number);
        return Float.parseFloat(roundNumber.replace(",", "."));
    }
}

