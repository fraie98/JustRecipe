package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private Recipe recipe;
    private Session appSession;


    public void initialize ()
    {
        appSession = Session.getInstance();
        commentTextArea.setEditable(false);
        commentSaveButton.setOnAction(actionEvent -> saveButtonAction());
    }

    /**
     * Trigger the delete comment action (from DB), and remove it from the UI
     */
    public void deleteButtonAction(){
        MongoDBDriver.getInstance().deleteComment(recipe, comment);
        ((VBox) commentVBox.getParent()).getChildren().remove(commentVBox);
    }

    /**
     *  Edit a comment, by set editable the commentTextArea
     */
    public void editButtonAction(){
        if(commentTextArea.isEditable()){
            Utils.showInfoAlert("You have to press the 'save' button to save your edit comment!");
            return;
        }
        commentTextArea.setEditable(true);
    }

    /**
     * save the changes to the comment by updating the element into the list
     */
    public void saveButtonAction(){
        if(!commentTextArea.isEditable()){
            Utils.showErrorAlert("You have first to press the 'modify' button!");
            return;
        }
        comment.setText(commentTextArea.getText());
        MongoDBDriver.getInstance().modifyComment(recipe, comment);
        commentTextArea.setEditable(false);

    }

    /**
     * Set the comment 'fxml'
     * @param comment   object from gets the fields to fill the 'fxml'
     * @param recipe recipeName, used for deleting and adding comment into mongoDB
     */
    public void setComment(Comment comment, Recipe recipe) {
        this.comment = comment;
        this.recipe = recipe;
        commentDate.setText("Written at: " + Utils.fromDateToString(comment.getCreationTime()));
        commentUsername.setText("By: " + comment.getAuthorUsername());
        commentTextArea.setText(comment.getText());

        if((appSession.getLoggedUser().getRole()!=0) || (appSession.getLoggedUser().getUsername().equals(comment.getAuthorUsername())))
            commentDeleteButton.setOnAction(mouseEvent -> deleteButtonAction());
        else
            commentDeleteButton.setVisible(false);

        if(appSession.getLoggedUser().getUsername().equals(comment.getAuthorUsername()))
            commentModifyButton.setOnAction(actionEvent -> editButtonAction());
        else
            commentModifyButton.setVisible(false);
    }
}
