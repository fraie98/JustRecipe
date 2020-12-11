package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RecipePageController {

    @FXML private ImageView homeImg;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView profileImg;
    @FXML private Text recipeInstructions;
    @FXML private ImageView recipeLikeImg;
    @FXML private ImageView recipePicture;
    @FXML private Text recipeTitle;
    @FXML private Text recipeUsername;
    @FXML private Text recipeCarbs;
    @FXML private Text recipeCalories;
    @FXML private Text recipeFat;
    @FXML private ImageView recipeEditImg;
    @FXML private Text recipeCategories;
    @FXML private Text recipeIngredients;
    @FXML private Text recipeProtein;
    @FXML private Label recipeLikes;
    @FXML private Label recipeDate;
    @FXML private VBox recipeVBox;

    private Recipe recipe;
    private String username;

    public void initialize ()
    {
        homeImg.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfileToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscoveryToChangePage(mouseEvent));
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        recipeTitle.setText(recipe.getTitle());
        recipeInstructions.setText(recipe.getInstructions());
        if (recipe.getPicture() != null)
        {
            recipePicture.setImage(new Image(recipe.getPicture()));
        }
        else
        {
            recipePicture.setImage(new Image("img/genericRecipe.png"));
        }
        recipeUsername.setText(recipe.getAuthorUsername());
        recipeCarbs.setText(String.valueOf(recipe.getCarbs()));
        recipeCalories.setText(String.valueOf(recipe.getCalories()));
        recipeFat.setText(String.valueOf(recipe.getFat()));
        recipeProtein.setText(String.valueOf(recipe.getProtein()));
        recipeCategories.setText(Utils.fromListToString(recipe.getCategories()));
        recipeIngredients.setText(Utils.fromListToString(recipe.getIngredients()));
        recipeDate.setText(Utils.fromDateToString(recipe.getCreationTime()));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Pippo", "Hello World!", new Date()));
        comments.add(new Comment("Pluto", "Fantastic recipe!", new Date()));
        recipe.setComments(comments);
        addRecipeComments();
    }

    private void clickOnHomepageToChangePage(MouseEvent mouseEvent){
        HomePageController homePageController = (HomePageController)
                Utils.changeScene("/homepage.fxml", mouseEvent);
        homePageController.setUsername(username);
    }

    private void clickOnProfileToChangePage(MouseEvent mouseEvent){
        ProfilePageController profilePageController = (ProfilePageController)
                Utils.changeScene("/profilePage.fxml", mouseEvent);
        profilePageController.setUsername(username);
    }

    private void clickOnDiscoveryToChangePage(MouseEvent mouseEvent){
        DiscoveryPageController discoveryPageController = (DiscoveryPageController)
                Utils.changeScene("/discoveryPage.fxml", mouseEvent);
        discoveryPageController.setUsername(username);
    }

    public void addRecipeComments() {
        if (recipe.getComments() != null)
        {
            Iterator<Comment> iterator = recipe.getComments().iterator();
            while (iterator.hasNext())
            {
                Comment comment = iterator.next();
                Pane commentPane = loadComment(comment);
                commentPane.setCenterShape(true);
                recipeVBox.getChildren().add(commentPane);
            }
        }
    }

    public Pane loadComment (Comment comment)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/comment.fxml"));
            pane = (Pane) loader.load();
            CommentController commentController =
                    (CommentController) loader.getController();
            commentController.setComment(comment);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }
}

