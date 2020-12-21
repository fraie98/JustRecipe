package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAction;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Controller for the page of the recipe
 */
public class RecipePageController {

    @FXML private ImageView homeImg;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView profileImg;
    @FXML private Text recipeInstructions;
    @FXML private ImageView recipeLikeImg;
    @FXML private ImageView recipePicture;
    @FXML private ImageView logoutPic;
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
    @FXML private ImageView recipeDelete;
    @FXML private TextArea commentsArea;
    @FXML private Button sendButton;
    @FXML private Button cancelButton;

    private Recipe recipe;
    private Session appSession;
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;

    public void initialize ()
    {
        homeImg.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfileToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscoveryToChangePage(mouseEvent));
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        recipeVBox.setAlignment(Pos.CENTER);
        appSession = Session.getInstance();
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        sendButton.setOnAction(actionEvent -> handleSendButtonAction(actionEvent));
        cancelButton.setOnAction(actionEvent -> handleCancelButtonAction(actionEvent));
        recipeLikeImg.setOnMouseClicked(mouseEvent -> handleClickOnLike());
        recipeUsername.setOnMouseClicked(mouseEvent -> handleClickOnUsername(mouseEvent));
    }

    private void handleClickOnLike()
    {
        if(neo4jDriver.isThisRecipeLikedByOne(recipeTitle.getText(),appSession.getLoggedUser().getUsername()))
        {
            neo4jDriver.unlike(appSession.getLoggedUser().getUsername(),recipeTitle.getText());
            recipeLikeImg.setImage(new Image("img/like.png"));
        }
        else
        {
            neo4jDriver.like(appSession.getLoggedUser().getUsername(),recipeTitle.getText());
            recipeLikeImg.setImage(new Image("img/alreadyliked.png"));
        }

        recipeLikes.setText(String.valueOf(neo4jDriver.howManyLikes(recipeTitle.getText())));
    }

    /**
     * Function who handle the adding comments, and upload on mongoDB
     * @param actionEvent pressing the button
     */
    private void handleSendButtonAction(ActionEvent actionEvent){
        if(commentsArea.getText().equals("")) {
            Utils.showErrorAlert("No Comments in the CommentsArea");
            return;
        }
//        System.out.println(recipe.getComments());
//        System.out.println(recipe.getTitle());
        Comment comment = new Comment(appSession.getLoggedUser().getUsername(), commentsArea.getText(), new Date());
        if(recipe.getComments() != null)
            recipe.addComments(comment);
        else{
            List<Comment> comments= new ArrayList<>();
            comments.add(comment);
            recipe.setComments(comments);
        }
        Utils.showComment(recipeVBox, comment);
//        System.out.println(recipe.getComments());
        mongoDBDriver.updateComments(recipe.getTitle(), recipe.getComments());
    }

    /**
     * Cancelling the comment textArea by clicking on the cancel Button
     * @param actionEvent pressing the button
     */
    private void handleCancelButtonAction(ActionEvent actionEvent){
        if(!commentsArea.getText().equals("")) commentsArea.setText("");
    }

    /**
     * Setters for the recipe, in which we also set the correct value to show
     * @param r    Recipe to show
     */
    public void setRecipe(Recipe r) {
        this.recipe = r;
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
        recipeDate.setText("Published on: " + Utils.fromDateToString(recipe.getCreationTime()));
        //TO DO
        recipeLikes.setText(String.valueOf(neo4jDriver.howManyLikes(recipe.getTitle())));
        if(neo4jDriver.isThisRecipeLikedByOne(recipe.getTitle(),appSession.getLoggedUser().getUsername()))
            recipeLikeImg.setImage(new Image("img/alreadyliked.png"));

        if(recipe.getComments() != null) {
            Label commentsTitle = new Label("Comments:");
            commentsTitle.setFont(Font.font(24));
            recipeVBox.getChildren().add(commentsTitle);
            Utils.showComments(recipeVBox, recipe.getComments());
        }
//        List<Comment> comments = new ArrayList<>();
//        comments.add(new Comment("Pippo", "Hello World!", new Date()));
//        comments.add(new Comment("Pluto", "Fantastic recipe!", new Date()));
//        recipe.setComments(comments);
//        if (comments != null)
//        {
//            Label commentsTitle = new Label("Comments:");
//            commentsTitle.setFont(Font.font(24));
//            recipeVBox.getChildren().add(commentsTitle);
//            Utils.showComments(recipeVBox, recipe.getComments());
//        }

        if(appSession.getLoggedUser().getRole()!=2 && !appSession.getLoggedUser().getUsername().equals(recipe.getAuthorUsername()))
            recipeDelete.setVisible(false);
        else
            recipeDelete.setOnMouseClicked(mouseEvent -> neo4jDriver.deleteRecipe(recipe.getTitle(),recipe.getCreationTime()));
    }

    /**
     * Function used to handle the click on the homepage icon
     * @param mouseEvent    event that represents the click on the icon
     */
    private void clickOnHomepageToChangePage(MouseEvent mouseEvent){
        HomePageController homePageController = (HomePageController)
                Utils.changeScene("/homepage.fxml", mouseEvent);
    }

    /**
     * Function used to handle the click on the recipe's owner
     * @param mouseEvent    event that represents the click on the recipe's owner
     */
    private void handleClickOnUsername(MouseEvent mouseEvent){
        ProfilePageController profilePageController = (ProfilePageController)
                Utils.changeScene("/profilePage.fxml", mouseEvent);
        profilePageController.setProfile(neo4jDriver.getUserByUsername(recipeUsername.getText()));
    }

    /**
     * Function used to handle the click on the profile icon
     * @param mouseEvent    event that represents the click on the icon
     */
    private void clickOnProfileToChangePage(MouseEvent mouseEvent){
        ProfilePageController profilePageController = (ProfilePageController)
                Utils.changeScene("/profilePage.fxml", mouseEvent);
        profilePageController.setProfile(Session.getInstance().getLoggedUser());
    }

    /**
     * Function used to handle the click on the discovery icon
     * @param mouseEvent    event that represents the click on the icon
     */
    private void clickOnDiscoveryToChangePage(MouseEvent mouseEvent){
        DiscoveryPageController discoveryPageController = (DiscoveryPageController)
                Utils.changeScene("/discoveryPage.fxml", mouseEvent);
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
}

