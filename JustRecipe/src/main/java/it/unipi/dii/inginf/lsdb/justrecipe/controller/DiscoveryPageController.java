package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

public class DiscoveryPageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    @FXML private ImageView homepageIcon;
    @FXML private ImageView profilePageIcon;
    @FXML private ImageView logoutPic;
    @FXML private Button searchButton;
    @FXML private TextField searchBarTextField;
    @FXML private ComboBox searchComboBox;
    @FXML private VBox discoveryVBox;
    @FXML private Button nextButton;
    @FXML private Button previousButton;

    private final int HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW = 20;
    private final int HOW_MANY_USER_SNAPSHOT_TO_SHOW = 20;
    private final int HOW_MANY_MOST_COMMON_CATEGORIES_TO_SHOW = 5;
    private final int HOW_MANY_SNAPSHOT_FOR_EACH_COMMON_CATEGORY = 4;
    private final int HOW_MANY_COMMENTS_TO_SHOW = 20;
    private int page; // number of page (at the beginning at 0), increase with nextButton and decrease with previousButton

    public void initialize ()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        homepageIcon.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        profilePageIcon.setOnMouseClicked(mouseEvent -> clickOnProfImgToChangePage(mouseEvent));
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        page = 0;

        // Initializing the options of the ComboBox
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Recipe title",
                        "Recipe category",
                        "Recipe ingredients",
                        "Most common recipe categories",
                        "Best recipes",
                        "User username",
                        "User full name",
                        "Most followed and active user",
                        "Top Commentators",
                        "Most liked user",
                        "Last comments" // To show only if the user is a moderator
                );
        searchComboBox.setItems(options);
        // if some changes happens to the combobox
        searchComboBox.setOnAction(actionEvent -> comboAction((ActionEvent) actionEvent));
        searchButton.setOnAction(actionEvent -> search(actionEvent));

        nextButton.setOnMouseClicked(mouseEvent -> clickOnNext(mouseEvent));
        previousButton.setOnMouseClicked(mouseEvent -> clickOnPrevious(mouseEvent));
        previousButton.setVisible(false); //in the first page it is not visible
    }

    private void search(ActionEvent actionEvent) {
        Utils.removeAllFromPane(discoveryVBox);
        if (String.valueOf(searchComboBox.getValue()).equals("Recipe title"))
        {
            List<Recipe> recipes = mongoDBDriver.searchRecipesFromTitle(searchBarTextField.getText(),
                    HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW*page, HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW);
            Utils.addRecipesSnap(discoveryVBox, recipes);
        }
        if (String.valueOf(searchComboBox.getValue()).equals("Recipe category"))
        {
            List<Recipe> recipes = mongoDBDriver.searchRecipesFromCategory (searchBarTextField.getText(),
                    HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW*page, HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW);
            Utils.addRecipesSnap(discoveryVBox, recipes);
        }
        if (String.valueOf(searchComboBox.getValue()).equals("Recipe ingredients"))
        {
            List<String> ingredientsList = Utils.fromStringToList(searchBarTextField.getText());
            List<Recipe> recipes = mongoDBDriver.searchRecipesFromIngredients (ingredientsList,
                    HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW*page, HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW);
            Utils.addRecipesSnap(discoveryVBox, recipes);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Most common recipe categories"))
        {
            List<String> mostCommonRecipeCategories = mongoDBDriver.searchMostCommonRecipeCategories(
                            HOW_MANY_MOST_COMMON_CATEGORIES_TO_SHOW*page,
                            HOW_MANY_MOST_COMMON_CATEGORIES_TO_SHOW);
            for (String category: mostCommonRecipeCategories)
            {
                Label categoryName = new Label();
                categoryName.setText(category.concat("\n"));
                categoryName.setFont(Font.font(48));
                discoveryVBox.getChildren().add(categoryName);
                List<Recipe> recipes = mongoDBDriver.getRecipesOfCategory(category,
                        HOW_MANY_SNAPSHOT_FOR_EACH_COMMON_CATEGORY);
                Utils.addRecipesSnap(discoveryVBox, recipes);
            }
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Best recipes"))
        {
            List<Recipe> recipes = neo4jDriver.searchBestRecipes(HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW*page,
                    HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW);
            Utils.addRecipesSnap(discoveryVBox, recipes);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("User username"))
        {
            List<User> users = neo4jDriver.searchUserByUsername(HOW_MANY_USER_SNAPSHOT_TO_SHOW*page,
                    HOW_MANY_USER_SNAPSHOT_TO_SHOW, searchBarTextField.getText());
            Utils.addUsersSnap(discoveryVBox, users);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("User full name"))
        {
            List<User> users = neo4jDriver.searchUserByFullName(HOW_MANY_USER_SNAPSHOT_TO_SHOW*page,
                    HOW_MANY_USER_SNAPSHOT_TO_SHOW, searchBarTextField.getText());
            Utils.addUsersSnap(discoveryVBox, users);
        }
        // For the moderator
        else if (String.valueOf(searchComboBox.getValue()).equals("Last comments"))
        {
            /*List<Comment> comments = mongoDBDriver.searchAllComments(
                    HOW_MANY_COMMENTS_TO_SHOW*page, HOW_MANY_COMMENTS_TO_SHOW);*/
            List<List<Object>> objects = mongoDBDriver.searchAllComments(
                    HOW_MANY_COMMENTS_TO_SHOW*page, HOW_MANY_COMMENTS_TO_SHOW);
            Utils.showAllComments(discoveryVBox, objects);
            //new String where should be recipeName,which here is useless o point out the correct recipe name
        }
    }


    /**
     * Function that let the navigation into the ui ---> homepage
     * @param mouseEvent event that represents the click on the icon
     */
    private void clickOnHomepageToChangePage(MouseEvent mouseEvent){
        try{
            HomePageController homePageController = (HomePageController)
                    Utils.changeScene("/homepage.fxml", mouseEvent);
        }catch (NullPointerException n){System.out.println("homePageController is null!!!!");}
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
     * Function that let the navigation into the ui ---> profilePage
     * @param mouseEvent event that represents the click on the icon
     */
    private void clickOnProfImgToChangePage(MouseEvent mouseEvent){
        try {
            ProfilePageController profilePageController = (ProfilePageController)
                    Utils.changeScene("/profilePage.fxml", mouseEvent);
            profilePageController.setProfile(Session.getInstance().getLoggedUser());
        }catch (NullPointerException n){System.out.println("profilePageController is null!!!!");}
    }

    /**
     * Function that handle the changes to the searchComboBox
     * @param event
     */
    private void comboAction(ActionEvent event) {
        page = 0;
        Utils.removeAllFromPane(discoveryVBox);
    }

    /**
     * Handler for the next button
     * @param mouseEvent    Events that leads to this function
     */
    private void clickOnNext(MouseEvent mouseEvent) {
        page++;
        if (page > 0)
            previousButton.setVisible(true);
        searchButton.fire(); // simulate the click of the button
    }

    /**
     * Handler for the previous button
     * @param mouseEvent    Events that leads to this function
     */
    private void clickOnPrevious(MouseEvent mouseEvent) {
        page--;
        if (page < 1)
            previousButton.setVisible(false);
        searchButton.fire(); // simulate the click of the button
    }
}
