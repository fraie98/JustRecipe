package it.unipi.dii.inginf.lsdb.justrecipe.controller;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.util.Iterator;
import java.util.List;

public class HomePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private String username; // username of the user logged
    @FXML private VBox mainPage;
    @FXML private ImageView profileImg;
    @FXML private ImageView discoveryImg;

    private final int HOW_MANY_SNAPSHOT_TO_SHOW = 20;


    /**
     * Initialization function for HomePageController
     */
    public void initialize()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        addRecipesSnap();
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfImgToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
    }

    /**
     * Function that adds the snapshots of the recipes to the homepage
     */
    public void addRecipesSnap() {
        List<Recipe> recipes = mongoDBDriver.getHomepageRecipe(0, HOW_MANY_SNAPSHOT_TO_SHOW, null);
        RecipeSnapshotController repCtrl = new RecipeSnapshotController();

        Iterator<Recipe> iterator = recipes.iterator();

        while (iterator.hasNext())
        {
            HBox row = new HBox();
            row.setStyle("-fx-padding: 10px");
            row.setSpacing(20);
            Recipe recipe1 = iterator.next();
            Pane rec1 = repCtrl.createSnapRecipe(recipe1);
            row.getChildren().add(rec1);
            if (iterator.hasNext())
            {
                Recipe recipe2 = iterator.next();
                Pane rec2 = repCtrl.createSnapRecipe(recipe2);
                row.getChildren().add(rec2);
            }
            mainPage.getChildren().add(row);
        }
    }


    private void clickOnProfImgToChangePage(MouseEvent mouseEvent){
        try {
            ProfilePageController profilePageController = (ProfilePageController)
                    Utils.changeScene("/profilePage.fxml", mouseEvent);
            profilePageController.setUsername(username);
        }catch (NullPointerException n){System.out.println("profilePageController is null!!!!");}
    }

    private void clickOnDiscImgtoChangePage(MouseEvent mouseEvent){
        try{
            DiscoveryPageController discoveryPageController = (DiscoveryPageController)
                    Utils.changeScene("/discoveryPage.fxml", mouseEvent);
            discoveryPageController.setUsername(username);
        }catch (NullPointerException n){System.out.println("homePageController is null!!!!");}
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
