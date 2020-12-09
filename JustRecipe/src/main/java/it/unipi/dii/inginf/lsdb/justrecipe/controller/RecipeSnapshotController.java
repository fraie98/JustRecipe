package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class RecipeSnapshotController {
    @FXML private Text title;

    /**
     * It create a snapshot for the recipe with these values
     * @param title
     * @param user
     * @param carbs
     * @param protein
     * @param fat
     * @param tags
     * @param imgPath
     * @return The pane that is the snapshot of the recipe
     */
    public Pane createSnapRecipe(String title, String user, Integer carbs, Integer protein, Integer fat, List<String> tags, String imgPath)
    {
        Pane newSnap = getRecipesSnapshotFXML();

        // Title
        Text t = (Text) newSnap.getChildren().get(3);
        t.setText(title);
        // User
        Text u = (Text) newSnap.getChildren().get(1);
        u.setText(user);
        // Carbs
        Text c = (Text) newSnap.getChildren().get(9);
        c.setText(String.valueOf(carbs));
        // Protein
        Text p = (Text) newSnap.getChildren().get(8);
        p.setText(String.valueOf(protein));
        // Fat
        Text f = (Text) newSnap.getChildren().get(10);
        f.setText(String.valueOf(fat));
        // Categories
        String overallTags = new String("");
        for (String s:tags) {
            overallTags = overallTags.concat(s).concat(", ");
        }
        Text categ = (Text) newSnap.getChildren().get(11);
        categ.setText(overallTags);
        // Recipe's pic
        ImageView recipefoto = (ImageView) newSnap.getChildren().get(2);
        recipefoto.setImage(new Image(imgPath));

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
