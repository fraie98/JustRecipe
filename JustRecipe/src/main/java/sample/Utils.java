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
     * Every scene has associated her specific controller
     * @param fileName      The name of the file in which i can obtain the GUI (.fxml)
     * @param actionEvent   The event that leads to change the scene
     * @return The new controller, because i need to pass some parameters
     */
    public static Object changeScene (String fileName, ActionEvent actionEvent)
    {
        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader=new FXMLLoader(Utils.class.getResource(fileName));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
