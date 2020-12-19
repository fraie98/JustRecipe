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

    private final int HOW_MANY_SNAPSHOT_TO_SHOW = 20;
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
        searchButton.setOnAction(actionEvent -> search(actionEvent));

        nextButton.setOnMouseClicked(mouseEvent -> clickOnNext(mouseEvent));
        previousButton.setOnMouseClicked(mouseEvent -> clickOnPrevious(mouseEvent));
        previousButton.setVisible(false); //in the first page it is not visible

        List<User> users = new ArrayList<>();
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        users.add(new User("Pippo", "Pippo", "Pippo", "Pippo"));
        Utils.addUsersSnap(discoveryVBox, users);
    }

    private void search(ActionEvent actionEvent) {
        Utils.removeAllFromPane(discoveryVBox);
        if (String.valueOf(searchComboBox.getValue()).equals("Recipe title"))
        {
            List<Recipe> recipes = mongoDBDriver.searchRecipesFromTitle(searchBarTextField.getText(),
                    HOW_MANY_SNAPSHOT_TO_SHOW*page, HOW_MANY_SNAPSHOT_TO_SHOW);
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
                List<Recipe> recipes = mongoDBDriver.getRecipesOfCategory(category, HOW_MANY_SNAPSHOT_FOR_EACH_COMMON_CATEGORY);
                Utils.addRecipesSnap(discoveryVBox, recipes);
            }
        }
        // For the moderator
        else if (String.valueOf(searchComboBox.getValue()).equals("Last comments"))
        {
            List<Comment> comments = mongoDBDriver.searchAllComments(
                    HOW_MANY_COMMENTS_TO_SHOW*page, HOW_MANY_COMMENTS_TO_SHOW);
            Utils.showComments(discoveryVBox, comments);
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
