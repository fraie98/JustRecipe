package it.unipi.dii.inginf.lsdb.justrecipe.controller;
import it.unipi.dii.inginf.lsdb.justrecipe.main.Main;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HomePageController{
    private Neo4jDriver neo4jDriver;
    @FXML private VBox mainPage;

    /**
     * Function used to pass the Neo4jDriver instance from another controller to this controller
     * @param neo4jDriver
     */
    public void transferNeo4jDriver(Neo4jDriver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    /**
     * Initialization function for HomePageController
     */
    public void initialize()
    {
        addRecipesSnap();
    }

    /**
     * Function that adds the snapshots of the recipes to the homepage
     */
    public void addRecipesSnap() {
        RecipeSnapshotController repCtrl = new RecipeSnapshotController();

        /* All this part must be substituted with a cycle*/
        Pane rec1 = repCtrl.createSnapRecipe("Titolo1","user1",20,30,40,null,"img/pizza.jpg");
        Pane rec2 = repCtrl.createSnapRecipe("Titolo2","user2",20,30,40,null,"img/tiramisu.jpg");
        Pane rec3 = repCtrl.createSnapRecipe("Titolo3","user3",20,30,40,null,"img/tiramisu.jpg");
        Pane rec4 = repCtrl.createSnapRecipe("Titolo4","user4",20,30,40,null,"img/tiramisu.jpg");

        HBox riga1 = new HBox();
        riga1.setStyle("-fx-padding: 10px");
        riga1.setSpacing(20);
        riga1.getChildren().add(rec1);
        riga1.getChildren().add(rec2);

        HBox riga2 = new HBox();
        riga2.setStyle("-fx-padding: 10px");
        riga2.setSpacing(20);
        riga2.getChildren().add(rec3);
        riga2.getChildren().add(rec4);

        mainPage.getChildren().add(0,riga1);
        mainPage.getChildren().add(1,riga2);
    }

}
