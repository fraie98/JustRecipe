package sample;

import java.sql.Timestamp;

public class Comment {
    private String text;
    private Timestamp timestamp;

    //Constractors
    public Comment(){}

    public Comment(String text, Timestamp timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }


    //Getters
    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


    //Setters
    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
