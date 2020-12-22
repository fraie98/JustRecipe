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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;

public class AdministrationPageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private Session appSession;
    @FXML private ImageView homepageIcon;
    @FXML private ImageView profilePageIcon;
    @FXML private ImageView logoutPic;
    @FXML private ImageView discoveryImg;
    @FXML private Button allComments;
    @FXML private Button allRecipes;
    @FXML private Button allUsers;
    @FXML private Button searchButton;
    @FXML private ComboBox chooseQuery;
    @FXML private TextField searchBar;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private VBox adminPageBox;

    private int page; // number of page (at the beginning at 0), increase with nextButton and decrease with previousButton
    private final int HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW = 20;
    private final int HOW_MANY_USER_SNAPSHOT_TO_SHOW = 20;
    private final int HOW_MANY_MOST_COMMON_CATEGORIES_TO_SHOW = 5;
    private final int HOW_MANY_SNAPSHOT_FOR_EACH_COMMON_CATEGORY = 4;
    private final int HOW_MANY_COMMENTS_TO_SHOW = 20;

    public void initialize()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        appSession = Session.getInstance();

        // Setting the menu'
        homepageIcon.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        profilePageIcon.setOnMouseClicked(mouseEvent -> clickOnProfImgToChangePage(mouseEvent));
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));

        // Setting the ComboBox
        ObservableList<String> entries =
                FXCollections.observableArrayList(
                        "User username",
                        "User full name",
                        "Recipe title"
                );
        chooseQuery.setItems(entries);
        chooseQuery.setPromptText("Click to choose");
        chooseQuery.setOnAction(event -> clickOnComboBox());

        // Set the queries that can be called from buttons
        allComments.setOnMouseClicked(mouseEvent -> clickOnAllComments());
        allRecipes.setOnMouseClicked(mouseEvent -> clickOnAllRecipes());
        allUsers.setOnMouseClicked(mouseEvent -> clickOnAllUsers());

        // Set buttons available to moderator/administrator
        if(appSession.getLoggedUser().getRole()==1) // moderator
        {
            allRecipes.setDisable(true);
            allUsers.setDisable(true);
        }

        // Search button
        searchButton.setOnMouseClicked(mouseEvent -> clickOnSearchButton());

        // Previous and next button behaviour
        page = 0;
        nextButton.setOnMouseClicked(mouseEvent -> clickOnNext(mouseEvent));
        previousButton.setOnMouseClicked(mouseEvent -> clickOnPrevious(mouseEvent));
        previousButton.setVisible(false); //in the first page it is not visible
    }

    /**
     * Handle the click on the search button
     */
    private  void clickOnSearchButton()
    {
        Utils.removeAllFromPane(adminPageBox);
        if (String.valueOf(chooseQuery.getValue()).equals("Recipe title"))
        {
            List<Recipe> recipes = mongoDBDriver.searchRecipesFromTitle(searchBar.getText(),
                    HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW*page, HOW_MANY_RECIPE_SNAPSHOT_TO_SHOW);
            Utils.addRecipesSnap(adminPageBox, recipes);
        }
        else if (String.valueOf(chooseQuery.getValue()).equals("User username"))
        {
            List<User> users = neo4jDriver.searchUserByUsername(HOW_MANY_USER_SNAPSHOT_TO_SHOW*page,
                    HOW_MANY_USER_SNAPSHOT_TO_SHOW, searchBar.getText());
            Utils.addUsersSnap(adminPageBox, users);
        }
        else if (String.valueOf(chooseQuery.getValue()).equals("User full name"))
        {
            List<User> users = neo4jDriver.searchUserByFullName(HOW_MANY_USER_SNAPSHOT_TO_SHOW*page,
                    HOW_MANY_USER_SNAPSHOT_TO_SHOW, searchBar.getText());
            Utils.addUsersSnap(adminPageBox, users);
        }
    }

    /**
     * Handle the click of an option of the ComboBox
     */
    private void clickOnComboBox()
    {
        page = 0;
        Utils.removeAllFromPane(adminPageBox);
    }

    /**
     * Show all the users of the db
     */
    private void clickOnAllUsers()
    {

    }

    /**
     * Show all the recipes of the db
     */
    private void clickOnAllRecipes()
    {

    }

    /**
     * Show the comments sorted by creation time
     */
    private void clickOnAllComments()
    {
        List<Comment> comments = mongoDBDriver.searchAllComments(
                HOW_MANY_COMMENTS_TO_SHOW*page, HOW_MANY_COMMENTS_TO_SHOW);
        Utils.showComments(adminPageBox, comments, new Recipe());
        //new String where should be recipeName,which here is useless
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
     * Function that let the navigation into the ui ---> discoveryPage
     * @param mouseEvent event that represents the click on the icon
     */
    private void clickOnDiscImgtoChangePage(MouseEvent mouseEvent){
        try{
            DiscoveryPageController discoveryPageController = (DiscoveryPageController)
                    Utils.changeScene("/discoveryPage.fxml", mouseEvent);
        }catch (NullPointerException n){System.out.println("homePageController is null!!!!");}
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
