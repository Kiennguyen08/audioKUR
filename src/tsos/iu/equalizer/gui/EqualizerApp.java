package tsos.iu.equalizer.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.*;

public class EqualizerApp extends Application {
     
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        stage.setTitle("Equalizer");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().add
        (EqualizerApp.class.getResource("Login.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    } 
     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
