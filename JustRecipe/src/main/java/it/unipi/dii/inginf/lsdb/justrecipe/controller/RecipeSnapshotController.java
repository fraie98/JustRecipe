package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public class RecipeSnapshotController {
    @FXML private Text title;

    public Pane createSnapRecipe(String title)
    {
        Pane newSnap = getRecipesSnapshotFXML();
        Text t = (Text) newSnap.getChildren().get(3);
        t.setText(title);
        return newSnap;
    }

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
