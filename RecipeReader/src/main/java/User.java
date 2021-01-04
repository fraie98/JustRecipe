public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private int role;

    public User(){}

    public User(String firstName, String lastName, String username, String password, int role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User (String firstName, String lastName, String username, String password)
    {
        this (firstName, lastName, username, password, 0);
    }


    //Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }
}

