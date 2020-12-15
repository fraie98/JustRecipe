package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Session;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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

    private Comment comment;
    private Session appSession;


    public void initialize ()
    {
        appSession = Session.getInstance();
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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
