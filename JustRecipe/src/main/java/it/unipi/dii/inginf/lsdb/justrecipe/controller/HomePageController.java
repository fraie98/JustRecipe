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


    public void initialize()
    {
        addRecipesSnap();
    }


    public void addRecipesSnap() {
        RecipeSnapshotController repCtrl = new RecipeSnapshotController();

        /* All this part must be substituted with a cycle*/
        Pane rec1 = repCtrl.createSnapRecipe("Titolo1");
        Pane rec2 = repCtrl.createSnapRecipe("Titolo2");
        Pane rec3 = repCtrl.createSnapRecipe("Titolo3");
        Pane rec4 = repCtrl.createSnapRecipe("Titolo4");

        HBox riga1 = new HBox();
        riga1.getChildren().add(rec1);
        riga1.getChildren().add(rec2);

        HBox riga2 = new HBox();
        riga2.getChildren().add(rec3);
        riga2.getChildren().add(rec4);

        mainPage.getChildren().add(0,riga1);
        mainPage.getChildren().add(1,riga2);
    }

}
