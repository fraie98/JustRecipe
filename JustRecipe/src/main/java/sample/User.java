package sample;

public class User {
    private String firstName;
    private String lastName;
    private String picture;
    private String username;
    private String password;

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
}

