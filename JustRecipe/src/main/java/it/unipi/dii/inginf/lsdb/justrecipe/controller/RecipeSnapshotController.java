package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Date;

public class RecipeSnapshotController {

    @FXML private Pane snapPane;
    @FXML private Label snapTitle;
    @FXML private Label snapUser;
    @FXML private Label snapCarbs;
    @FXML private Label snapCal;
    @FXML private Label snapFat;
    @FXML private Label snapProtein;
    @FXML private ImageView snapImg;
    @FXML private Label snapCategories;
    @FXML private ImageView snapDeleteRecipe;

    private Recipe recipe; // recipe shows in this snapshot
    private Neo4jDriver neo4jDriver;
    private Session appSession;

    public void initialize ()
    {
        appSession = Session.getInstance();
        neo4jDriver = Neo4jDriver.getInstance();
        snapPane.setOnMouseClicked(mouseEvent -> showMoreInformation(mouseEvent));
    }

    /**
     * This function is used to show the complete information of the recipe in a new page
     * @param mouseEvent    The event that leads to show the recipe completely (click of the mouse in the pane)
     */
    private void showMoreInformation(MouseEvent mouseEvent) {
        RecipePageController recipePageController =
                (RecipePageController) Utils.changeScene("/recipePage.fxml", mouseEvent);
        recipePageController.setRecipe(recipe);
    }


    public void setRecipe (Recipe recipe)
    {
        this.recipe = recipe;
        snapTitle.setText(recipe.getTitle());
        snapUser.setText(recipe.getAuthorUsername());
        snapCarbs.setText(String.valueOf(recipe.getCarbs()));
        snapProtein.setText(String.valueOf(recipe.getProtein()));
        snapFat.setText(String.valueOf(recipe.getFat()));
        snapCal.setText(String.valueOf(recipe.getCalories()));
        snapCategories.setText(Utils.fromListToString(recipe.getCategories()));
        if (recipe.getPicture() != null)
        {
            snapImg.setImage(new Image(recipe.getPicture()));
        }
        else
        {
            snapImg.setImage(new Image("img/genericRecipe.png"));
        }


        if ((appSession.getLoggedUser().getRole()!=2)  && !appSession.getLoggedUser().getUsername().equals(recipe.getAuthorUsername()))
            snapDeleteRecipe.setVisible(false);
        else
            snapDeleteRecipe.setOnMouseClicked(mouseEvent -> deleteRecipeAction(recipe.getTitle(),recipe.getCreationTime()));
    }

    private void deleteRecipeAction(String recipeTitle, Date ts)
    {
        neo4jDriver.deleteRecipe(recipeTitle,ts);
    }
}
