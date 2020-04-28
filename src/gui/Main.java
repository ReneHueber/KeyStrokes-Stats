package gui;

import database.WriteDb;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage = primaryStage;
        gui.SystemTrayIcon systemTray = new gui.SystemTrayIcon(stage);
        
        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);
        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemTray.addAppToTray();
            }
        });

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/selectKeyboardWindow.fxml"));
        stage.setTitle("Key Strokes");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
