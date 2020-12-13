package it.unipi.dii.inginf.lsdb.justrecipe.controller;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class HomePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private String username; // username of the logged user
    @FXML private VBox mainPage;
    @FXML private ImageView profileImg;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView logoutPic;

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
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
    }

    /**
     * Function that adds the snapshots of the recipes to the homepage
     */
    public void addRecipesSnap() {
        List<Recipe> recipes = mongoDBDriver.getHomepageRecipe(0, HOW_MANY_SNAPSHOT_TO_SHOW, null);

        Iterator<Recipe> iterator = recipes.iterator();

        while (iterator.hasNext())
        {
            HBox row = new HBox();
            row.setStyle("-fx-padding: 10px");
            row.setSpacing(20);
            Recipe recipe1 = iterator.next();
            Pane rec1 = createRecipeSnapshot(recipe1);
            row.getChildren().add(rec1);
            if (iterator.hasNext())
            {
                Recipe recipe2 = iterator.next();
                Pane rec2 = createRecipeSnapshot(recipe2);
                row.getChildren().add(rec2);
            }
            mainPage.getChildren().add(row);
        }
    }

    /**
     * Function that let the navigation into the ui ---> profilePage
     * @param mouseEvent event that represents the click on the icon
     */
    private void clickOnProfImgToChangePage(MouseEvent mouseEvent){
        try {
            ProfilePageController profilePageController = (ProfilePageController)
                    Utils.changeScene("/profilePage.fxml", mouseEvent);
            profilePageController.setUsername(username);
        }catch (NullPointerException n){System.out.println("profilePageController is null!!!!");}
    }

    /**
     * Function that let the logout action, by going into the welcome page
     * @param mouseEvent event that represents the click on the icon
     */
    private void clickOnLogoutImg(MouseEvent mouseEvent){
        try {
            WelcomePageController welcomePageController = (WelcomePageController)
                Utils.changeScene("/welcome.fxml", mouseEvent);
        }catch (NullPointerException n){System.out.println("profilePageController is null!!!!");}
    }

    /**
     * Function that let the navigation into the ui ---> discoveryPage
     * @param mouseEvent event that represents the click on the icon
     */
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

    /**
     * This function create a pane that contains a recipe snapshot
     * @param recipe    recipe to display in the snapshot
     * @return
     */
    public Pane createRecipeSnapshot(Recipe recipe)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recipeSnap.fxml"));
            pane = (Pane) loader.load();
            RecipeSnapshotController recipeSnapshotController =
                    (RecipeSnapshotController) loader.getController();
            recipeSnapshotController.setRecipe(recipe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }
}
