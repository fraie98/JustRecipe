package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class RecipeSnapshotController {
    private final int HOW_MANY_CHAR_SNAPSHOT_TITLE = 25;
    private final int HOW_MANY_CHAR_SNAPSHOT_CATEGORIES = 45;

    @FXML private Text snapTitle;
    @FXML private Text snapUser;
    @FXML private Text snapCarbs;
    @FXML private Text snapCal;
    @FXML private Text snapFat;
    @FXML private Text snapProtein;
    @FXML private ImageView snapImg;
    @FXML private Text snapCategories;

    private Recipe recipe; // recipe shows in this snapshot

    public void initialize ()
    {
        snapImg.setOnMouseClicked(mouseEvent -> esegui(mouseEvent));
    }

    public void esegui (MouseEvent mouseEvent)
    {
        System.out.println(snapTitle.getText());
    }

    public void setRecipe (Recipe recipe)
    {
        this.recipe = recipe;
        String title = recipe.getTitle();
        if (title.length() > HOW_MANY_CHAR_SNAPSHOT_TITLE)
        {
            title = title.substring(0, HOW_MANY_CHAR_SNAPSHOT_TITLE-1) + "...";
        }
        snapTitle.setText(title);
        snapUser.setText(recipe.getAuthorUsername());
        snapCarbs.setText(String.valueOf(recipe.getCarbs()));
        snapProtein.setText(String.valueOf(recipe.getProtein()));
        snapFat.setText(String.valueOf(recipe.getFat()));
        snapCal.setText(String.valueOf(recipe.getCalories()));
        String overallTags = "";
        for (String s: recipe.getCategories()) {
            overallTags = overallTags.concat(s).concat(", ");
        }
        if (overallTags.length() > HOW_MANY_CHAR_SNAPSHOT_CATEGORIES)
        {
            overallTags = overallTags.substring(0, HOW_MANY_CHAR_SNAPSHOT_CATEGORIES-1) + "...";
        }
        snapCategories.setText(overallTags);
        if (recipe.getPicture() != null)
        {
            snapImg.setImage(new Image(recipe.getPicture()));
        }
        else
        {
            snapImg.setImage(new Image("img/genericRecipe.png"));
        }
    }
}
