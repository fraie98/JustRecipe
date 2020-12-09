package it.unipi.dii.inginf.lsdb.justrecipe.main;

import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class used to start the application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/welcome.fxml"));
        primaryStage.setTitle("JustRecipe");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //primaryStage.setOnCloseRequest(actionEvent -> {close();});
    }

    public static void main(String[] args) {
        launch(args);
    }
}
