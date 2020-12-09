package it.unipi.dii.inginf.lsdb.justrecipe.controller;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.util.ArrayList;

public class HomePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private String username; // username of the user logged
    @FXML private VBox mainPage;
    @FXML private ImageView profileImg;
    @FXML private ImageView discoveryImg;


    /**
     * Initialization function for HomePageController
     */
    public void initialize()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        mongoDBDriver.getHomepageRecipe();
        addRecipesSnap();
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfImgToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
    }

    /**
     * Function that adds the snapshots of the recipes to the homepage
     */
    public void addRecipesSnap() {
        RecipeSnapshotController repCtrl = new RecipeSnapshotController();
        ArrayList<String> categories = new ArrayList<>();
        categories.add("prova");
        categories.add("prova1");
        categories.add("prova3");

        /* All this part must be substituted with a cycle*/
        Pane rec1 = repCtrl.createSnapRecipe("Titolo1","user1",1,2,3,categories,"img/pizza.jpg");
        Pane rec2 = repCtrl.createSnapRecipe("Titolo2","user2",4,5,6,categories,"img/tiramisu.jpg");
        Pane rec3 = repCtrl.createSnapRecipe("Titolo3","user3",7,8,9,categories,"img/tiramisu.jpg");
        Pane rec4 = repCtrl.createSnapRecipe("Titolo4","user4",10,11,12,categories,"img/tiramisu.jpg");

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
