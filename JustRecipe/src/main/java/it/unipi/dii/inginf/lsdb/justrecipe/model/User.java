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
    private int role; // 0:user, 1:moderator, 2:admin

    public User(){}

    //Constructor
    public User(String firstName, String lastName, String picture, String username, String password, int role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture = picture;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User (String firstName, String lastName, String username, String password)
    {
        this(firstName, lastName, null, username, password, 0);
    }

    public User (String firstName, String lastName, String username)
    {
        this(firstName, lastName, null, username, null, 0);
    }

    public User(String firstName, String lastName, String username, int follower, int following, int added)
    {
        this(firstName,lastName,username);
        this.follower = follower;
        this.following = following;
        this.numRecipes = added;
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

    public int getRole() {
        return role;
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

    public void setRole(int role) {
        this.role = role;
    }
}

