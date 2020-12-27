package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ProfilePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private Session appSession;
    private final int HOW_MANY_SNAPSHOT_TO_SHOW = 20;
    private int page; // number of page (at the beginning at 0), increase with nextButton and decrease with previousButton
    private User user;

    @FXML private ImageView homepageIcon;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView logoutPic;
    @FXML private ImageView addRecipeOrMyProfileImg;
    @FXML private ImageView profileDeleteUser;
    @FXML private ImageView profileEditUser;
    @FXML private ImageView profileImg;
    @FXML private ImageView profileGoAdminPage;
    @FXML private VBox recipeVbox;
    @FXML private Text userName;
    @FXML private Text followerNumber;
    @FXML private Text followingNumber;
    @FXML private Text recipesNumber;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private ImageView addFollow;

    public void initialize ()
    {
        appSession = Session.getInstance();
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        homepageIcon.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
        logoutPic.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        nextButton.setOnMouseClicked(mouseEvent -> clickOnNext(mouseEvent));
        previousButton.setOnMouseClicked(mouseEvent -> clickOnPrevious(mouseEvent));
        profileGoAdminPage.setOnMouseClicked(mouseEvent -> clickOnAdminPage(mouseEvent));
    }

    /**
     * Set the profile page for the user U
     * @param user  User who owns the profile page
     */
    public void setProfile(User user)
    {
        this.user = user;

        userName.setText(user.getUsername());
        if(user.getUsername().equals(appSession.getLoggedUser().getUsername()))
        {
            // Update Forced
            user = neo4jDriver.getUserByUsername(appSession.getLoggedUser().getUsername());
            appSession.updateLoggedUserInfo(user);
        }

        final User u = user;
        if(u.getPicture()!=null)
            profileImg.setImage(new Image(u.getPicture()));

        followerNumber.setText(String.valueOf(u.getFollower()));
        followingNumber.setText(String.valueOf(u.getFollowing()));
        recipesNumber.setText(String.valueOf(u.getNumRecipes()));
        if(userName.getText().equals(appSession.getLoggedUser().getUsername()))
            addFollow.setVisible(false);
        else
        {
            if(neo4jDriver.isUserOneFollowedByUserTwo(userName.getText(),appSession.getLoggedUser().getUsername()))
                addFollow.setImage(new Image("img/alreadyFollowed_profile.png"));

            addFollow.setOnMouseClicked(mouseEvent -> clickOnFollow());
        }

        // The admin page must be showed only in the admin/moderator profile page
        if(appSession.getLoggedUser().getRole()==0 || !appSession.getLoggedUser().getUsername().equals(u.getUsername()))
            profileGoAdminPage.setVisible(false);

        page = 0;
        previousButton.setVisible(false); //in the first page it is not visible

        if(appSession.getLoggedUser().getRole()!=2 && !appSession.getLoggedUser().getUsername().equals(u.getUsername()))
            profileDeleteUser.setVisible(false);
        else
            profileDeleteUser.setOnMouseClicked(mouseEvent -> handleDeleteUserEvent(mouseEvent));

        if(!appSession.getLoggedUser().getUsername().equals(u.getUsername()) && appSession.getLoggedUser().getRole()==0)
            profileEditUser.setVisible(false);
        profileEditUser.setOnMouseClicked(mouseEvent -> clickOnEditProfile(mouseEvent,u));

        if(appSession.getLoggedUser().getUsername().equals(u.getUsername()))
            addRecipeOrMyProfileImg.setOnMouseClicked(mouseEvent -> clickOnAddRecipeImg(mouseEvent));
        else
        {
            addRecipeOrMyProfileImg.setImage(new Image("img/user.png"));
            addRecipeOrMyProfileImg.setOnMouseClicked(mouseEvent -> clickOnMyProfile(mouseEvent));
        }

        Utils.addRecipesSnap(recipeVbox, mongoDBDriver.getRecipesFromAuthorUsername(0, HOW_MANY_SNAPSHOT_TO_SHOW, u.getUsername()));
    }

    /**
     * Function used to delete the user
     * @param mouseEvent
     */
    private void handleDeleteUserEvent(MouseEvent mouseEvent) {
        // Delete all his/her recipe
        neo4jDriver.deleteAllRecipesOfUser(user.getUsername());
        mongoDBDriver.deleteAllRecipesOfUser(user.getUsername());
        // Delete the user from DB
        neo4jDriver.deleteUser(user.getUsername());
        // If i am the user, go to welcome page
        if (user.getUsername().equals(appSession.getLoggedUser().getUsername()))
        {
            Utils.changeScene("/welcome.fxml", mouseEvent);
        }
        else // If i am an administrator and i have deleted another account, go to Administration Page
        {
            Utils.changeScene("/adminPage.fxml", mouseEvent);
        }
    }

    /**
     * Handle the click on the edit profile icon
     * @param mouseEvent
     */
    private void clickOnEditProfile(MouseEvent mouseEvent, User u)
    {
        EditProfilePageController editProfilePageController = (EditProfilePageController)
                Utils.changeScene("/editProfile.fxml", mouseEvent);
        editProfilePageController.setEditProfilePage(u);
    }

    /**
     * Handle the click on the administration page icon
     * @param mouseEvent
     */
    private void clickOnAdminPage(MouseEvent mouseEvent)
    {
        AdministrationPageController administrationPageController = (AdministrationPageController)
                Utils.changeScene("/adminPage.fxml", mouseEvent);
    }

    /**
     * Function used to handle the click on the my profile icon
     * @param mouseEvent    event that represents the click on the icon
     */
    private void clickOnMyProfile(MouseEvent mouseEvent){
        ProfilePageController profilePageController = (ProfilePageController)
                Utils.changeScene("/profilePage.fxml", mouseEvent);
        profilePageController.setProfile(appSession.getLoggedUser());
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
     * Handle the click on the follow/unfollow button. If the logged user follow the user's profile
     * then the click means unfollow, otherwise means follow. The image changes depending on this.
     */
    private void clickOnFollow()
    {
        if(neo4jDriver.isUserOneFollowedByUserTwo(userName.getText(),appSession.getLoggedUser().getUsername()))
        {
            // I want to unfollow an user
            neo4jDriver.unfollow(appSession.getLoggedUser().getUsername(),userName.getText());
            addFollow.setImage(new Image("img/follow_profile.png"));
            followerNumber.setText(String.valueOf(Integer.parseInt(followerNumber.getText()) - 1));
        }
        else
        {
            // I want to follow a user
            neo4jDriver.follow(appSession.getLoggedUser().getUsername(),userName.getText());
            addFollow.setImage(new Image("img/alreadyFollowed_profile.png"));
            followerNumber.setText(String.valueOf(Integer.parseInt(followerNumber.getText()) + 1));
        }
    }

    /**
     * Function that let the navigation into the ui ---> addRecipe
     * @param mouseEvent event that represents the click on the icon
     */
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

    private void clickOnPrevious(MouseEvent mouseEvent){
        Utils.removeAllFromPane(recipeVbox);
        page--;
        if (page < 1)
            previousButton.setVisible(false);
        Utils.addRecipesSnap(recipeVbox,
                mongoDBDriver.getRecipesFromAuthorUsername(HOW_MANY_SNAPSHOT_TO_SHOW*page,
                        HOW_MANY_SNAPSHOT_TO_SHOW, userName.getText()));
    }

    private void clickOnNext(MouseEvent mouseEvent){
        Utils.removeAllFromPane(recipeVbox);
        page++;
        if (page > 0)
            previousButton.setVisible(true);
        Utils.addRecipesSnap(recipeVbox,
                mongoDBDriver.getRecipesFromAuthorUsername(HOW_MANY_SNAPSHOT_TO_SHOW*page,
                        HOW_MANY_SNAPSHOT_TO_SHOW, userName.getText()));
    }
}
