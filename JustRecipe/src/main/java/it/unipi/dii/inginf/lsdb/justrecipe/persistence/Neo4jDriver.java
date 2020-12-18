package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.neo4j.driver.Values.NULL;
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
     * It controls if a user with username @one is followed by the user with username @two
     * @param one  Username of user one
     * @param two  Username of user two
     * @return  true if one is followed by two, false otherwise
     */
    public Boolean isUserOneFollowedByUserTwo(String one, String two)
    {
        Boolean relation;
        try(Session session = driver.session())
        {
            relation = session.readTransaction((TransactionWork<Boolean>) tx -> {
                    Result r = tx.run("match (a:User{username:$two})-[r:FOLLOWS]->(b:User{username:$one}) " +
                            "return count(*)",parameters("one",one,"two",two));
                    Record rec = r.next();
                    if(rec.get(0).asInt()==0)
                        return false;
                    else
                        return true;
            });
        }
        return relation;
    }

    /**
     * It creates the relation follower-[:Follow]->following
     * @param follower  The user who starts to follow
     * @param following  The user who is followed by follower
     */
    public void follow(String follower, String following)
    {
        try(Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Integer>) tx -> {
                    tx.run("match (a:User) where a.username=$following " +
                            "match (b:User) where b.username=$follower " +
                            "merge (b)-[:FOLLOWS]->(a)",parameters("follower",follower,"following",following));
                    return 1;
                });
        }
    }

    /**
     * It deletes the relation oldFollower-[:Follow]->oldFollowing
     * @param oldFollower  The user who decide to unfollow
     * @param oldFollowing  The user unfollowed
     */
    public void unfollow(String oldFollower, String oldFollowing)
    {
        try(Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Integer>) tx -> {
                    tx.run("match (u:User{username:$oldFollower})-[r:FOLLOWS]->(u2:User{username:$oldFollowing})" +
                            " delete r",parameters("oldFollower",oldFollower,"oldFollowing",oldFollowing));
                    return 1;
                });
        }
    }

    /**
     * It counts the number of follower of a given user
     * @param user  Username of the target user
     * @return  the number of follower
     */
    public int howManyFollower(String user)
    {
        return howMany( "match (a:User)-[r:FOLLOWS]->(b:User{username:$placeholder}) return count(a)",user);
    }

    /**
     * It counts the number of following of a given user
     * @param user  Username of the target user
     * @return  The number of following
     */
    public int howManyFollowing(String user)
    {
        return howMany("match (a:User)<-[r:FOLLOWS]-(b:User{username:$placeholder}) return count(a)",user);
    }

    /**
     * It counts the number of likes of a given recipe
     * @param recipeTitle  Title of the given recipe
     * @return  The number of likes
     */
    public int howManyLikes(String recipeTitle)
    {
        return howMany("match (a:User)-[r:LIKES]->(b:Recipe{title:$placeholder}) return count(a)",recipeTitle);
    }
    /**
     * Private function which execute a given query that count how many relation enter or go out from a node
     * @param query  query text
     * @param userOrRecipe  username of the given user or title of the given recipe
     * @return  the number of incoming or outgoing relation
     */
    private int howMany(String query, String userOrRecipe)
    {
        int howMany;

        try(Session session = driver.session())
        {
            howMany = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result r = tx.run(query,parameters("placeholder",userOrRecipe));
                Record rec = r.next();
                return rec.get(0).asInt();
            });
        }
        return howMany;
    }

    /**
     * It controls if the given recipe is liked by the given user
     * @param recipeTitle  title of the given recipe
     * @param one  username of the given user
     * @return  true if the given recipe is liked by the given user, false otherwise
     */
    public Boolean isThisRecipeLikedByOne(String recipeTitle, String one)
    {
        Boolean relation;
        try(Session session = driver.session())
        {
            relation = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result r = tx.run("match (a:User{username:$one})-[r:LIKES]->(b:Recipe{title:$t}) " +
                        "return count(*)",parameters("one",one,"t",recipeTitle));
                Record rec = r.next();
                if(rec.get(0).asInt()==0)
                    return false;
                else
                    return true;
            });
        }
        return relation;
    }

    /**
     * It creates the relation user-[:LIKES]->recipe
     * @param user  Username of the target user
     * @param recipeTitle  Title of the target recipe
     */
    public void like(String user, String recipeTitle)
    {
        try(Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Integer>) tx -> {
                    tx.run("match (a:User) where a.username=$u " +
                            "match (b:Recipe) where b.title=$t " +
                            "merge (a)-[:LIKES]->(b)",parameters("u",user,"t",recipeTitle));
                    return 1;
            });
        }
    }

    /**
     * It deletes the relation user-[:LIKES]->recipe
     * @param user  Username of the target user
     * @param recipeTitle  Title of the target recipe
     */
    public void unlike(String user, String recipeTitle)
    {
        try(Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Integer>) tx -> {
                    tx.run("match (u:User{username:$u})-[r:LIKES]->(p:Recipe{title:$t})" +
                            " delete r",parameters("u",user,"t",recipeTitle));
                    return 1;
            });
        }
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

    public List<Recipe> getRecipeSnap(int howManySkip, int howMany, String username){
        List <Recipe> recipes = new ArrayList<>();
        System.out.println(username);
        try(Session session = driver.session()) {
            session.readTransaction((TransactionWork<List<Recipe>>) tx -> {
                Result result = tx.run("MATCH (u:User{username:$u})-[ADDS]->(r:Recipe)"+"RETURN r.title as Title," +
                                "r.calories as Calories, r.fat as Fat, r.protein as Protein SKIP $skip LIMIT $limit",
                        parameters("u",username, "skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String title = r.get("Title").asString();
                    int calories = 0;
                    int protein = 0;
                    int fat = 0;
                    if(r.get("Calories") != NULL)
                        calories = r.get("Calories").asInt();
                    if(r.get("Fat") != NULL)
                        fat = r.get("Fat").asInt();
                    if(r.get("Protein") != NULL)
                        protein = r.get("Protein").asInt();
                    Recipe recipe = new Recipe(title, fat, calories, protein);
                    recipe.setAuthorUsername(username);
                    recipes.add(recipe);
                }
                System.out.println(recipes);
                return recipes;
            });
        }
        return recipes;
    }
}
