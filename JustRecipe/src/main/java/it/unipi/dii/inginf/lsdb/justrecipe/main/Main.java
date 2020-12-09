package it.unipi.dii.inginf.lsdb.justrecipe.main;

import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.HomePageController;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.WelcomePageController;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class used to start the application
 */
public class Main extends Application {
    private Neo4jDriver neo4jDriver;
    private ConfigurationParameters configurationParameters = Utils.readConfigurationParameters();

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        primaryStage.setTitle("JustRecipe");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();

        // Start the Neoj driver
        neo4jDriver = new Neo4jDriver(configurationParameters);
        // Get the controller for the welcome page and transfer the driver to it
        WelcomePageController welcomePageController = (WelcomePageController) loader.getController();
        welcomePageController.transferNeo4jDriver(neo4jDriver);

        // close the connection to Neo4J when the app closes
        primaryStage.setOnCloseRequest(actionEvent -> {neo4jDriver.closeConnection();});
    }

    public static void main(String[] args) {
        launch(args);
    }

}
