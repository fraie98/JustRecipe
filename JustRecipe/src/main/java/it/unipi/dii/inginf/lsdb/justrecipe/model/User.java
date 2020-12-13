package it.unipi.dii.inginf.lsdb.justrecipe.model;

public class User {
    private String firstName;
    private String lastName;
    private String picture;
    private String username;
    private String password;
    private int follower;
    private int following;
    private int numRecipes;

    public User(){}

    //Constructor
    public User(String firstName, String lastName, String picture, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture = picture;
        this.username = username;
        this.password = password;
    }


    //Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPicture() {
        return picture;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getFollower() {
        return follower;
    }

    public int getFollowing() {
        return following;
    }

    public int getNumRecipes() {
        return numRecipes;
    }

    //Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public void setNumRecipes(int numRecipes) {
        this.numRecipes = numRecipes;
    }
}

