package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class EditProfilePageController {
    private Neo4jDriver neo4jDriver;
    private Session appSession;
    @FXML private ImageView homeImg;
    @FXML private ImageView profileImg;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView logoutImg;
    @FXML private TextField editFirstname;
    @FXML private TextField editLastname;
    @FXML private TextField editUrlPic;
    @FXML private Button preview;
    @FXML private Button moderatorButton;
    @FXML private Button adminButton;
    @FXML private Button submit;
    @FXML private Button back;
    @FXML private TextField editPw;
    @FXML private TextField confirmEditPw;
    @FXML private Text adv;
    @FXML private Label role;
    @FXML private Label username;

    public void initialize ()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        appSession = Session.getInstance();
        homeImg.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
        logoutImg.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfileToChangePage(mouseEvent));
        back.setOnMouseClicked(mouseEvent -> clickOnBackButton(mouseEvent));

        // The page is initialized as if the user that visualizes it is the owner
        adminButton.setDisable(true);
        moderatorButton.setDisable(true);

        adminButton.setOnMouseClicked(mouseEvent -> clickOnAdminButton());
        moderatorButton.setOnMouseClicked(mouseEvent -> clickOnModeratorButton());
    }

    /**
     * Function to set the page with the information of the user
     * @param u  user's info
     */
    public void setEditProfilePage(User u)
    {
        username.setText(u.getUsername());
        editFirstname.setText(u.getFirstName());
        editLastname.setText(u.getLastName());
        if(u.getPicture()!=null)
            editUrlPic.setPromptText(u.getPicture());

        // it's an admin/moderator and he is not watching his edit profile page
        if(appSession.getLoggedUser().getRole()!=0 && !appSession.getLoggedUser().getUsername().equals(u.getUsername()))
        {
            adv.setVisible(false);
            editFirstname.setDisable(true);
            editLastname.setDisable(true);
            editPw.setDisable(true);
            editUrlPic.setDisable(true);
            confirmEditPw.setDisable(true);
            preview.setDisable(true);

            moderatorButton.setDisable(false);
            adminButton.setDisable(false);
        }

        if(appSession.getLoggedUser().getRole()==1) // moderator
        {
            adminButton.setDisable(true);
        }

        switch (u.getRole())
        {
            case 1:
                role.setText("Moderator");
                moderatorButton.setText("Downgrade");
                break;
            case 2:
                role.setText("Administrator");
                adminButton.setDisable(true);
                break;
            default:
                role.setText("Normal User");
        }
    }

    /**
     * Handle the click on moderator button and changing the GUI considering the role of the owner of the page
     */
    private void clickOnModeratorButton()
    {
        if(moderatorButton.getText().equals("Elect Moderator"))
        {
            neo4jDriver.electModerator(username.getText());
            moderatorButton.setText("Downgrade");
            role.setText("Moderator");
        }
        else
        {
            neo4jDriver.downgradeToNormalUser(username.getText());
            moderatorButton.setText("Elect Moderator");
            role.setText("Normal User");
        }

        // if the viewer is an admin, he's not watching his own edit page
        if(appSession.getLoggedUser().getRole()==2 && !appSession.getLoggedUser().getUsername().equals(username.getText()))
            adminButton.setDisable(false);
    }

    /**
     * Handle the click on admin button and changing the GUI considering the role of the owner of the page
     */
    private void clickOnAdminButton()
    {
        neo4jDriver.electAdmin(username.getText());
        adminButton.setDisable(true);
        role.setText("Administrator");
        moderatorButton.setText("Elect Moderator");
    }

    /**
     * Allow to return to the profile page of the owner of the visualized edit profile page
     * @param mouseEvent
     */
    private void clickOnBackButton(MouseEvent mouseEvent)
    {
        ProfilePageController profilePageController = (ProfilePageController)
                Utils.changeScene("/profilePage.fxml", mouseEvent);
        profilePageController.setProfile(neo4jDriver.getUserByUsername(username.getText()));
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
     * Function that let the navigation into the ui ---> discoveryPage
     * @param mouseEvent event that represents the click on the icon
     */
    private void clickOnDiscImgtoChangePage(MouseEvent mouseEvent){
        try{
            DiscoveryPageController discoveryPageController = (DiscoveryPageController)
                    Utils.changeScene("/discoveryPage.fxml", mouseEvent);
        }catch (NullPointerException n){System.out.println("DiscoveryPageController is null!!!!");}
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
}
