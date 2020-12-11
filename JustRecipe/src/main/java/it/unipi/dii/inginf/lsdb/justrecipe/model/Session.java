package it.unipi.dii.inginf.lsdb.justrecipe.model;

public class Session {
    private User loggedUser;

    public Session(User loggedUser) {
        this.loggedUser = loggedUser;
    }
    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
    public User getLoggedUser() {
        return loggedUser;
    }
}
