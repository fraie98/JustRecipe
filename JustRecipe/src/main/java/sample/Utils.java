package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Utils {

    /**
     * Snippet of code for jumping in the next scene
     * @param fileName    The name of the file in which i can obtain the GUI (.fxml)
     * @param actionEvent The event that leads to change the scene
     */
    public static void changeScene (String fileName, ActionEvent actionEvent)
    {
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(Utils.class.getResource(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
