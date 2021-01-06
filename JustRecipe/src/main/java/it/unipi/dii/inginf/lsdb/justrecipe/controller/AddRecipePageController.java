package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.neo4j.driver.internal.InternalPath;

import java.beans.EventHandler;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddRecipePageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private Session appSession;
    private Recipe recipe;
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
    @FXML private Button clear;
    @FXML private Text titlePage;
    @FXML private ImageView iconOfTitlePage;



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
        clear.setOnMouseClicked(mouseEvent -> clearAllFields());
    }

    /**
     * Using the recipe (argument) to fill the form in order to edit an already present recipe
     * @param recipe
     */
    public void setRecipeToUpdate(Recipe recipe)
    {
        titlePage.setText("Update Recipe");
        iconOfTitlePage.setImage(new Image("img/edit.png"));
        this.recipe = recipe;
        addTitle.setText(recipe.getTitle());
        addTitle.setEditable(false);
        addUrl.requestFocus();
        addUrl.setText(recipe.getPicture());
        addCal.setText(String.valueOf(recipe.getCalories()));
        addCarbs.setText(String.valueOf(recipe.getCarbs()));
        addFat.setText(String.valueOf(recipe.getFat()));
        addProt.setText(String.valueOf(recipe.getProtein()));
        addCateg.setText(Utils.fromListToString(recipe.getCategories()));
        addIngr.setText(Utils.fromListToString(recipe.getIngredients()));
        addInstructions.setText(recipe.getInstructions());
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
            Date ts;
            if (addTitle.isEditable())
                ts = new Date();
            else
                ts = recipe.getCreationTime();
            String creator = appSession.getLoggedUser().getUsername();

            int calo=0;
            int fat = 0;
            int carbs = 0;
            int proteins = 0;

            if(addCal.getText().isEmpty())
                calo = -1;
            else
                try {
                    calo = Integer.parseInt(addCal.getText());
                }catch (NumberFormatException e){
                    Utils.showErrorAlert("Error!\nThe Calories field must conteins only numbers!");
                    return;
                }

            if(addFat.getText().isEmpty())
                fat = -1;
            else
                try {
                    fat = Integer.parseInt(addFat.getText());
                }catch (NumberFormatException e){
                    Utils.showErrorAlert("Error!\nThe Fat field must conteins only numbers!");
                    return;
                }

            if(addCarbs.getText().isEmpty())
                carbs = -1;
            else
                try {
                    carbs = Integer.parseInt(addCarbs.getText());
                }catch (NumberFormatException e){
                    Utils.showErrorAlert("Error!\nThe Carbs field must conteins only numbers!");
                    return;
                }

            if(addProt.getText().isEmpty())
                proteins = -1;
            else
                try {
                    proteins = Integer.parseInt(addProt.getText());
                }catch (NumberFormatException e){
                    Utils.showErrorAlert("Error!\nThe Protein field must conteins only numbers!");
                    return;
                }

            System.out.println(calo + " " + fat + " " + carbs +" "+ proteins);

            Recipe newRec = new Recipe(addTitle.getText(), addInstructions.getText(), ingr, categ, calo, fat, proteins, carbs, ts, addUrl.getText(), creator, null);

            if(addTitle.isEditable()) {
                if(neo4jDriver.newRecipe(newRec))
                {
                    //If neo is ok, perform mongo
                    if(!mongoDBDriver.addRecipe(newRec))
                    {
                        // if mongo is not ok, remove the previously added recipe
                        neo4jDriver.deleteRecipe(newRec);
                        Utils.showErrorAlert("Error in adding the recipe");
                    }
                    else
                    {
                        Utils.showInfoAlert("Recipe succesfully added");
                    }
                }
                clearAllFields();
            }
            else {
                Recipe oldRec = mongoDBDriver.getRecipeFromTitle(newRec.getTitle());

                if(oldRec!=null)
                {
                    if(mongoDBDriver.editRecipe(newRec))
                    {
                        //If mongo is ok, perform neo
                        if(!neo4jDriver.updateRecipe(newRec))
                        {
                            // if neo is not ok, reset the previously modified recipe
                            mongoDBDriver.editRecipe(oldRec);
                            Utils.showErrorAlert("Error in edit the recipe");
                        }
                        else
                        {
                            Utils.showInfoAlert("Recipe succesfully edited");
                        }

                    }
                }
            }
        }
    }

    private void clearAllFields()
    {
        if (addTitle.isEditable())
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
