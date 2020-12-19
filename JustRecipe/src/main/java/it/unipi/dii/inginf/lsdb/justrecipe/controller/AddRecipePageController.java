package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.beans.EventHandler;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddRecipePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private Session appSession;
    @FXML private ImageView homeImg;
    @FXML private ImageView profileImg;
    @FXML private ImageView discoveryImg;
    @FXML private ImageView logoutImg;
    @FXML private TextField addTitle;
    @FXML private TextField addUrl;
    @FXML private TextField addCal;
    @FXML private TextField addCarbs;
    @FXML private TextField addFat;
    @FXML private TextField addProt;
    @FXML private TextField addCateg;
    @FXML private TextArea addIngr;
    @FXML private TextArea addInstructions;
    @FXML private Button submit;



    public void initialize ()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        mongoDBDriver = MongoDBDriver.getInstance();
        appSession = Session.getInstance();
        homeImg.setOnMouseClicked(mouseEvent -> clickOnHomepageToChangePage(mouseEvent));
        discoveryImg.setOnMouseClicked(mouseEvent -> clickOnDiscImgtoChangePage(mouseEvent));
        logoutImg.setOnMouseClicked(mouseEvent -> clickOnLogoutImg(mouseEvent));
        profileImg.setOnMouseClicked(mouseEvent -> clickOnProfileToChangePage(mouseEvent));
        submit.setOnMouseClicked(mouseEvent -> submitNewRecipe(mouseEvent));
    }

    /**
     * Control the fields and add new recipe in the DBs
     */
    private void submitNewRecipe(MouseEvent mouseEvent)
    {
        // If I write nothing in the field then getText return an empty String (not null!)
        if(addTitle.getText().isEmpty() || addIngr.getText().isEmpty() || addInstructions.getText().isEmpty())
        {
            Utils.showErrorAlert("Title, Ingredients and Instructions are mandatory fields");
        }
        else
        {
            List<String> ingr = Arrays.asList(addIngr.getText().split(","));
            System.out.println(ingr);

            List<String> categ =  Arrays.asList(addCateg.getText().split(","));
            System.out.println(categ);

            //Date ts = new Date(System.currentTimeMillis());
            //Date ts = new Date(new Date().getTime());
            Date ts = new Date();
            String creator = appSession.getLoggedUser().getUsername();

            int calo, fat, carbs, proteins;

            if(addCal.getText().isEmpty())
                calo = -1;
            else
                calo = Integer.parseInt(addCal.getText());

            if(addFat.getText().isEmpty())
                fat = -1;
            else
                fat = Integer.parseInt(addFat.getText());

            if(addCarbs.getText().isEmpty())
                carbs = -1;
            else
                carbs = Integer.parseInt(addCarbs.getText());

            if(addProt.getText().isEmpty())
                proteins = -1;
            else
                proteins = Integer.parseInt(addProt.getText());

            System.out.println(calo + " " + fat + " " + carbs +" "+ proteins);

            Recipe newRec = new Recipe(addTitle.getText(), addInstructions.getText(), ingr, categ, calo, fat, proteins, carbs, ts, addUrl.getText(), creator, null);

            //neo4jDriver.addRecipe(newRec);
            mongoDBDriver.addRecipe(newRec);

            Utils.showInfoAlert("Recipe succesfully added");
            clearAllFields();
        }
    }

    private void clearAllFields()
    {
        addTitle.setText("");
        addUrl.setText("");
        addCal.setText("");
        addCarbs.setText("");
        addFat.setText("");
        addProt.setText("");
        addCateg.setText("");
        addIngr.setText("");
        addInstructions.setText("");
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
