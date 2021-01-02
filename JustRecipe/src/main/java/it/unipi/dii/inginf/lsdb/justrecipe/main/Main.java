package it.unipi.dii.inginf.lsdb.justrecipe.main;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Class used to start the application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        FXMLLoader loadErrorPage = new FXMLLoader(getClass().getResource("/errorPage.fxml"));

        primaryStage.setTitle("JustRecipe");

        if(!Neo4jDriver.getInstance().initConnection() || !MongoDBDriver.getInstance().initConnection())
            primaryStage.setScene(new Scene(loadErrorPage.load()));
        else
            primaryStage.setScene(new Scene(loader.load()));

        primaryStage.centerOnScreen();
        primaryStage.show();
        //primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/img/icon.png"));


        // close the connection to Neo4J and MongoDB when the app closes
        primaryStage.setOnCloseRequest(actionEvent -> {
            Neo4jDriver.getInstance().closeConnection();
            MongoDBDriver.getInstance().closeConnection();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
