package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Opens a fxml Files.
 * In an new Stage or in an Existing.
 * Returns the Controller of the Scene to pass data.
 */
public class ProcessFxmlFiles {
    private String fxmlPath, windowName;
    private Parent root;

    private Object controller;

    public ProcessFxmlFiles(String fxmlPath, String windowName){
        this.fxmlPath = fxmlPath;
        this.windowName = windowName;
    }

    /**
     * Loads the Scene, and creates the new Stage.
     * @return The Controller of the Scene
     */
    protected Object openInNewStage(){
        // loads the fxml an the controller
        loadFxml();

        // creates the Stage and set's the properties
        Stage stage = new Stage();
        stage.setTitle(windowName);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();

        return controller;
    }

    /**
     * Loads the scene and opens it in an existing Stage.
     * @param stage The existing Stage
     * @return The Controller of the Scene
     */
    protected Object openInExistingStage(Stage stage){
        // loads the fxml and the controller
        loadFxml();

        // set's the properties for the stage
        stage.setScene(new Scene(root));
        stage.setTitle(windowName);
        stage.show();

        return controller;
    }

    /**
     * Loads the scene from an fxml File, and get's the Controller of the Scene.
     */
    private void loadFxml(){
        try{
            // loads the fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = (Parent) fxmlLoader.load();

            // get's the controller of the fxml file
            controller = fxmlLoader.getController();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
