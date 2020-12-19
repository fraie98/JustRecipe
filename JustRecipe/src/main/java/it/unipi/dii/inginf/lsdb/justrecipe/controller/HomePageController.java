package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.awt.*;
import java.util.List;

public class HomePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private Session session;
    @FXML private VBox mainPage;
    @FXML private ImageView profileImg;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView logoutPic;
    @FXML private Button nextButton;
    @FXML private Button previousButton;

    private final int HOW_MANY_SNAPSHOT_TO_SHOW = 20; //standard case
    private int page; // number of page (at the beginning at 0), increase with nextButton and decrease with previousButton

    /**
     * Initialization function for HomePageController
     */
    public void initialize()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        session = Session.getInstance();
        Utils.addRecipesSnap(mainPage, neo4jDriver.getHomepageRecipeSnap(0, HOW_MANY_SNAPSHOT_TO_SHOW, session.getLoggedUser().getUsername()));
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfImgToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        page = 0;
        nextButton.setOnMouseClicked(mouseEvent -> clickOnNext(mouseEvent));
        previousButton.setOnMouseClicked(mouseEvent -> clickOnPrevious(mouseEvent));
        previousButton.setVisible(false); //in the first page it is not visible
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
        }catch (NullPointerException n){System.out.println("homePageController is null!!!!");}
    }

    private void clickOnPrevious(MouseEvent mouseEvent){
        Utils.removeAllFromPane(mainPage);
        page--;
        if (page < 1)
            previousButton.setVisible(false);
        Utils.addRecipesSnap(mainPage,
                neo4jDriver.getHomepageRecipeSnap(HOW_MANY_SNAPSHOT_TO_SHOW*page,
                        HOW_MANY_SNAPSHOT_TO_SHOW, session.getLoggedUser().getUsername()));
    }

    private void clickOnNext(MouseEvent mouseEvent){
        Utils.removeAllFromPane(mainPage);
        page++;
        if (page > 0)
            previousButton.setVisible(true);
        Utils.addRecipesSnap(mainPage,
                neo4jDriver.getHomepageRecipeSnap(HOW_MANY_SNAPSHOT_TO_SHOW*page,
                        HOW_MANY_SNAPSHOT_TO_SHOW, session.getLoggedUser().getUsername()));
    }

}
