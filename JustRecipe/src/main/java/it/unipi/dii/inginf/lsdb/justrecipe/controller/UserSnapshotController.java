package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class UserSnapshotController {
    private Neo4jDriver neo4jDriver;
    private Session appSession;
    @FXML private ImageView userSnapImg;
    @FXML private Label userSnapUsername;
    @FXML private Text userSnapFollower;
    @FXML private Text userSnapFollowing;
    @FXML private Text userSnapRecipes;

    public void initialize()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        appSession = Session.getInstance();
    }

    public void setUserSnap(User u)
    {
        String urlPicture = u.getPicture();
        if(urlPicture==null || urlPicture.isEmpty())
            userSnapImg.setImage(new Image("/img/genericUser.png"));
        else
            userSnapImg.setImage(new Image(urlPicture));

        userSnapUsername.setText(u.getUsername());
        userSnapFollower.setText(Integer.toString(u.getFollower()));
        userSnapFollowing.setText(Integer.toString(u.getFollowing()));
        userSnapRecipes.setText(Integer.toString(u.getNumRecipes()));
    }
}
