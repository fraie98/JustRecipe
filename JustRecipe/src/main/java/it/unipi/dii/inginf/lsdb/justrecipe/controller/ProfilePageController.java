package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ProfilePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private Session appSession;
    @FXML private ImageView homepageIcon;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView logoutPic;
    @FXML private ImageView addRecipeImg;
    @FXML private ImageView profileDeleteUser;
    @FXML private ImageView profileEditUser;
    public void initialize ()
    {
        appSession = Session.getInstance();
        neo4jDriver = Neo4jDriver.getInstance();
        //mongoDBDriver = MongoDBDriver.getInstance();
        homepageIcon.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        addRecipeImg.setOnMouseClicked(mouseEvent -> clickOnAddRecipeImg(mouseEvent));
    }

    public void setProfile(User u)
    {
        if(appSession.getLoggedUser().getRole()!=3 && !appSession.getLoggedUser().getUsername().equals(u.getUsername()))
            profileDeleteUser.setVisible(false);
        else
            profileDeleteUser.setOnMouseClicked(mouseEvent -> neo4jDriver.deleteUser(u.getUsername()));

        if(appSession.getLoggedUser().getUsername().equals(u.getUsername()))
            profileEditUser.setOnMouseClicked(mouseEvent -> neo4jDriver.editProfile(u.getUsername()));
        else
            profileEditUser.setVisible(false);
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

    private void clickOnAddRecipeImg(MouseEvent mouseEvent){
        try{
            AddRecipePageController addRecipePageController;
            addRecipePageController = (AddRecipePageController)
                    Utils.changeScene("/addRecipe.fxml", mouseEvent);
        }catch (NullPointerException n){System.out.println("addRecipePageController is null!!!!");n.printStackTrace();}
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
}
