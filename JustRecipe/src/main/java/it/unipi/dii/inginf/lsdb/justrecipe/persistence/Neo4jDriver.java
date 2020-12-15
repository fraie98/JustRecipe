package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.neo4j.driver.*;

import java.util.Date;

import static org.neo4j.driver.Values.parameters;

/**
 * This class is used to communicate with Neo4j
 */
public class Neo4jDriver implements DatabaseDriver{
    private static Neo4jDriver instance = null; // Singleton Instance

    private Driver driver;
    private String ip;
    private int port;
    private String username;
    private String password;

    private Neo4jDriver(ConfigurationParameters configurationParameters)
    {
        this.ip = configurationParameters.getNeo4jIp();
        this.port = configurationParameters.getNeo4jPort();
        this.username = configurationParameters.getNeo4jUsername();
        this.password = configurationParameters.getNeo4jPassword();
        initConnection();
    }

    public static Neo4jDriver getInstance()
    {
        if (instance == null)
        {
            instance = new Neo4jDriver(Utils.readConfigurationParameters());
        }
        return instance;
    }

    /**
     * Method that inits the Driver
     */
    @Override
    public void initConnection()
    {
        driver = GraphDatabase.driver( "neo4j://" + ip + ":" + port, AuthTokens.basic( username, password ) );
    }

    /**
     * Method for closing the connection of the Driver
     */
    @Override
    public void closeConnection ()
    {
        if (driver != null)
            driver.close();
    }

    /**
     * Method that creates a new node in the graphDB with the information of the new user
     * @param firstName     first name of the new user
     * @param lastName      last name of the new user
     * @param username      username of the new user
     * @param password      password of the new user
     */
    public void addUser( final String firstName, final String lastName, final String username,
                         final String password)
    {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (u:User {firstName: $firstName, lastName: $lastName, username: $username," +
                                "password: $password, role:0})",
                        parameters( "firstName", firstName, "lastName", lastName, "username",
                                username, "password", password ) );
                return null;
            });
        }
    }

    /**
     * Method that checks if the user already exists
     * @param username  username to check
     * @param password  password to check
     * @return          true if the user exists in the graph, otherwise false
     */
    public boolean checkUser (final String username, final String password)
    {
        try ( Session session = driver.session())
        {
             Boolean present = session.readTransaction((TransactionWork<Boolean>) tx -> {
                 Result result = tx.run( "MATCH (u:User) " +
                                 "WHERE u.username = $username " +
                                 "AND u.password = $password " +
                                 "RETURN u " +
                                 "LIMIT 1",
                        parameters( "username", username, "password", password ) );
                 if (result.stream().count() != 0)
                    return true;
                 else
                     return false;
            });
            if (present)
                return true;
            else
                return false;
        }
    }

    /**
     * Method that checks if the username has already been used
     * @param username  username to check
     * @return          true if the username already exists, otherwise false
     */
    public boolean checkUsername(final String username) {
        try ( Session session = driver.session())
        {
            Boolean present = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run( "MATCH (u:User) " +
                                "WHERE u.username = $username " +
                                "RETURN u " +
                                "LIMIT 1",
                        parameters( "username", username) );
                if (result.stream().count() != 0)
                    return true;
                else
                    return false;
            });
            if (present)
                return true;
            else
                return false;
        }
    }

    /**
     *
     * @param one  Username of user one
     * @param two  Username of user two
     * @return  true if one is followed by two, false otherwise
     */
    public boolean isUserOneFollowedByUserTwo(String one, String two)
    {
        // Mock-up
        // In the future this funtion must interrogate neo4j db
        // in order to know if user one follow user two
        return true;
    }

    /**
     * It cretes the relation follower-[:Follow]->following
     * @param follower  The user who starts to follow
     * @param following  The user who is followed by follower
     */
    public void follow(String follower, String following)
    {
        
    }

    /**
     * It deletes the relation oldFollower-[:Follow]->oldFollowing
     * @param oldFollower  The user who decide to unfollow
     * @param oldFollowing  The user unfollowed
     */
    public void unfollow(String oldFollower, String oldFollowing)
    {

    }

    /**
     * It returns the info about the user
     * @param username  Username of the target user
     * @return  The entity User with all informations about the user with the indicated username
     */
    public User getUserInfo(String username)
    {
        User u = null;
        u = new User("pippo","pippo",null,"pippo","pippo",0);
        return u;
    }

    /**
     * It deletes the user with the given username
     * @param username username of the user that I want to delete
     */
    public void deleteUser(String username)
    {

    }

    /**
     * It deletes a recipe given the title and the creation timestamp (the title is not unique)
     * @param title  title of the recipe that I want to delete (target recipe)
     * @param creationTs  creation timestamp of the target recipe
     */
    public void deleteRecipe(String title, Date creationTs)
    {

    }

    /**
     * It deletes a comment given the author and the creationTs
     * @param author author of the target comment
     * @param creationTs creation timestamp of the target comment
     */
    public void deleteComment(String author, Date creationTs)
    {

    }

    /**
     * Edit a user given its username
     * @param username username of the target user
     */
    public void editProfile(String username)
    {

    }

    /**
     * It edit a comment given its title and its creation timestamp
     * @param title
     * @param creationTs
     */
    public void editComment(String title, Date creationTs)
    {

    }
}
