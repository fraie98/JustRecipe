package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class RecipeSnapshotController {
    private final int HOW_MANY_CHAR_SNAPSHOT_TITLE = 25;
    private final int HOW_MANY_CHAR_SNAPSHOT_CATEGORIES = 50;


    /**
     * It create a snapshot for the recipe with these values
     * @param recipe    The recipe to show
     * @return The pane that is the snapshot of the recipe
     */
    public Pane createSnapRecipe (Recipe recipe)
    {
        Pane newSnap = getRecipesSnapshotFXML();

        // Title
        Text t = (Text) newSnap.getChildren().get(3);
        String title = recipe.getTitle();
        if (title.length() > HOW_MANY_CHAR_SNAPSHOT_TITLE)
        {
            title = title.substring(0, HOW_MANY_CHAR_SNAPSHOT_TITLE-1) + "...";
        }
        t.setText(title);

        // User
        Text u = (Text) newSnap.getChildren().get(1);
        u.setText(recipe.getAuthorUsername());
        // Carbs
        Text c = (Text) newSnap.getChildren().get(9);
        c.setText(String.valueOf(recipe.getCarbs()));
        // Protein
        Text p = (Text) newSnap.getChildren().get(13);
        p.setText(String.valueOf(recipe.getProtein()));
        // Fat
        Text f = (Text) newSnap.getChildren().get(10);
        f.setText(String.valueOf(recipe.getFat()));
        // Calories
        Text cal = (Text) newSnap.getChildren().get(8);
        p.setText(String.valueOf(recipe.getCalories()));
        // Categories
        String overallTags = "";
        for (String s: recipe.getCategories()) {
            overallTags = overallTags.concat(s).concat(", ");
        }
        if (overallTags.length() > HOW_MANY_CHAR_SNAPSHOT_CATEGORIES)
        {
            overallTags = overallTags.substring(0, HOW_MANY_CHAR_SNAPSHOT_CATEGORIES-1) + "...";
        }
        Text categ = (Text) newSnap.getChildren().get(11);
        categ.setText(overallTags);
        // Recipe's pic
        ImageView recipefoto = (ImageView) newSnap.getChildren().get(2);
        recipefoto.setImage(new Image("img/genericRecipe.png"));

        return newSnap;
    }

    /**
     * It retrieves the fxml file for the recipe snap
     * @return The default pane for a default recipe snap
     */
    public Pane getRecipesSnapshotFXML()
    {
        Pane view = null;
        try
        {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/recipeSnap.fxml"));
            view = (Pane)loader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return view;
    }
}
