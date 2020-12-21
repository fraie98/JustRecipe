package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.AccessibleAction;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Controller for the single comment
 */
public class CommentController {

    @FXML private Label commentDate;
    @FXML private Label commentUsername;
    @FXML private TextArea commentTextArea;
    @FXML private Button commentModifyButton;
    @FXML private Button commentDeleteButton;
    @FXML private Button commentSaveButton;
    @FXML private VBox commentVBox;

    private Comment comment;
    private String recipeName;
    private Session appSession;


    public void initialize ()
    {
        appSession = Session.getInstance();
        commentDeleteButton.setOnAction(mouseEvent -> deleteButtonAction(mouseEvent));
    }

    /**
     * Trigger the delete comment action (from DB), and remove it from the UI
     * @param mouseEvent
     */
    public void deleteButtonAction(ActionEvent mouseEvent){
        commentVBox.getChildren().remove(0,commentVBox.getChildren().size());
        commentVBox.setVisible(false);
        MongoDBDriver.getInstance().deleteComment(recipeName, comment);
    }

    /**
     * Set the comment 'fxml'
     * @param comment   object from gets the fields to fill the 'fxml'
     * @param recipeName recipeName, used for deleting and adding comment into mongoDB
     */
    public void setComment(Comment comment, String recipeName) {
        this.comment = comment;
        this.recipeName = recipeName;
        commentDate.setText("Written at: " + Utils.fromDateToString(comment.getCreationTime()));
        commentUsername.setText("By: " + comment.getAuthorUsername());
        commentTextArea.setText(comment.getText());

        if((appSession.getLoggedUser().getRole()!=0) || (appSession.getLoggedUser().getUsername().equals(comment.getAuthorUsername())))
            commentDeleteButton.setOnMouseClicked(mouseEvent -> Neo4jDriver.getInstance().deleteComment(comment.getAuthorUsername(),comment.getCreationTime()));
        else
            commentDeleteButton.setVisible(false);

        if(appSession.getLoggedUser().getUsername().equals(comment.getAuthorUsername()))
            commentModifyButton.setOnMouseClicked(mouseEvent -> Neo4jDriver.getInstance().editComment(comment.getAuthorUsername(),comment.getCreationTime()));
        else
            commentModifyButton.setVisible(false);
    }
}
