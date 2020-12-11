package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class CommentController {

    @FXML private Label commentDate;
    @FXML private Label commentUsername;
    @FXML private TextArea commentTextArea;
    @FXML private Button commentModifyButton;
    @FXML private Button commentDeleteButton;
    @FXML private Button commentSaveButton;

    private Comment comment;

    public void initialize ()
    {

    }

    public void setComment(Comment comment) {
        this.comment = comment;
        commentDate.setText("Written at: " + Utils.fromDateToString(comment.getCreationTime()));
        commentUsername.setText("By: " + comment.getAuthorUsername());
        commentTextArea.setText(comment.getText());
    }
}
