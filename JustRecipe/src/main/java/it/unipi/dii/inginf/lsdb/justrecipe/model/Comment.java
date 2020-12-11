package it.unipi.dii.inginf.lsdb.justrecipe.model;

import java.util.Date;

public class Comment {
    private String authorUsername;
    private String text;
    private Date creationTime;

    //Constructors
    public Comment(){}

    public Comment(String authorUsername, String text, Date creationTime) {
        this.authorUsername = authorUsername;
        this.text = text;
        this.creationTime = creationTime;
    }


    //Getters

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getText() {
        return text;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    //Setters
    public void setText(String text) {
        this.text = text;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "authorUsername='" + authorUsername + '\'' +
                ", text='" + text + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
