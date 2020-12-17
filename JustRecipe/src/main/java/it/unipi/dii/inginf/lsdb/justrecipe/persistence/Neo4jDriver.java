package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.neo4j.driver.*;

import java.util.Date;
import java.util.NoSuchElementException;

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
     * It controls that a user with the given username exists or not
     * @param username  username of the user that I want to control
     * @return true if the user is registered, false otherwise
     */
    public boolean isRegistered (final String username)
    {
        Boolean isPresent;
        try ( Session session = driver.session())
        {
             isPresent = session.readTransaction((TransactionWork<Boolean>) tx -> {
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
        }
        return isPresent;
    }

    /**
     * It performs the login with the given username and password
     * @param username  Username of the target user
     * @param pw  Password of the target user
     * @return The object user if the login is done successfully, otherwise null
     */
    public User login(final String username, final String pw)
    {
        User u = null;
        try ( Session session = driver.session())
        {
            u = session.readTransaction((TransactionWork<User>) tx -> {
                Result result = tx.run( "MATCH (u:User) " +
                                "WHERE u.username = $username " +
                                "AND u.password = $password " +
                                "RETURN u.firstName, u.lastName, u.username, u.password, u.role  " +
                                "LIMIT 1",
                        parameters( "username", username,"password",pw) );
                User log = null;
                try
                {
                    Record r = result.next();
                    log = new User(r.get(0).asString(),r.get(1).asString(),null,r.get(2).asString(),r.get(3).asString(),r.get(4).asInt());
                }
                catch (NoSuchElementException ex)
                {
                    log = null;
                }

                return log;
            });
        }
        return u;
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
