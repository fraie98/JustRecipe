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

        Text t = (Text) newSnap.getChildren().get(3);
        t.setText(title);

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
