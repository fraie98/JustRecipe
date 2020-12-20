package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.util.*;

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
     * Add a recipe in Neo4j databases
     * @param r  Object recipe that will be added
     */
    public void addRecipe(Recipe r)
    {
        try ( Session session = driver.session())
        {
            Map<String,Object> query = new HashMap<>();
            query.put("title",r.getTitle());
            if(r.getCalories()!=-1)
                query.put("calories",r.getCalories());
            if(r.getFat()!=-1)
                query.put("fat",r.getFat());
            if(r.getProtein()!=-1)
                query.put("protein",r.getProtein());
            if(r.getCarbs()!=-1)
                query.put("carbs",r.getCarbs());

            //System.out.println(query);

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "UNWIND $props as rnew " +
                                "MATCH (u:User{username:$name}) " +
                                "MERGE (rec:Recipe{title:rnew.title,calories:rnew.calories,fat:rnew.fat," +
                                "protein:rnew.protein,carbs:rnew.carbs}) " +
                                "MERGE (u)-[ad:ADDS]->(rec) " +
                                "SET ad.when=$ts",
                        parameters( "props", query, "name", r.getAuthorUsername(), "ts", r.getCreationTime().getTime()) );
                return null;
            });
        }
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
     * It counts the number of recipes added from the given user
     * @param user  Username of the given user
     * @return  The number of recipes added from the user
     */
    public int howManyRecipesAdded(String user)
    {
        return howMany("match (p:Recipe)<-[r:ADDS]-(b:User{username:$placeholder}) return count(p)",user);
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

    /**
     * Function that returns the recipe snapshots of one user
     * @param howManySkip   How many to skip
     * @param howMany       How many to obtain
     * @param username      Username of the user
     * @return              List of the recipes
     */
    public List<Recipe> getRecipeSnaps(int howManySkip, int howMany, String username){
        List <Recipe> recipes = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction((TransactionWork<List<Recipe>>) tx -> {
                Result result = tx.run("MATCH (u:User{username:$username})-[a:ADDS]->(r:Recipe) " +
                                "RETURN r.title as title, r.calories as calories, r.fat as fat, r.protein as protein, " +
                                "r.carbs as carbs, r.picture as picture, u.username as authorUsername " +
                                "ORDER BY a.when DESC " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username", username, "skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String title = r.get("title").asString();
                    int calories = 0;
                    int protein = 0;
                    int fat = 0;
                    int carbs = 0;
                    String picture = null;
                    String authorUsername = r.get("authorUsername").asString();
                    if(r.get("calories") != NULL)
                        calories = r.get("calories").asInt();
                    if(r.get("fat") != NULL)
                        fat = r.get("fat").asInt();
                    if(r.get("protein") != NULL)
                        protein = r.get("protein").asInt();
                    if(r.get("carbs") != NULL)
                        carbs = r.get("carbs").asInt();
                    if (r.get("picture") != NULL)
                    {
                        picture = r.get("picture").asString();
                    }
                    Recipe recipe = new Recipe(title, fat, calories, protein, carbs, picture);
                    recipe.setAuthorUsername(authorUsername);
                    recipes.add(recipe);
                }
                return recipes;
            });
        }
        return recipes;
    }

    /**
     * Function that returns the information for creating the snapshot in the homepage
     * The recipes to show are the ones that are added by the other users that I follow
     * @param howManySkip       How many recipes to skip from the results
     * @param howMany           How many recipes to return
     * @param username          The username of the user
     * @return                  The list of recipes to show
     */
    public List<Recipe> getHomepageRecipeSnap(int howManySkip, int howMany, String username)
    {
        List<Recipe> recipes = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u1:User{username:$username})-[:FOLLOWS]->(u2:User)-[a:ADDS]->(r:Recipe) "+
                                "RETURN r.title as title, r.calories as calories, r.fat as fat, r.protein as protein, " +
                                "r.carbs AS carbs, r.picture as picture, u2.username as authorUsername " +
                                "ORDER BY a.when DESC SKIP $skip LIMIT $limit",
                        parameters("username",username, "skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String title = r.get("title").asString();
                    int calories = 0;
                    int protein = 0;
                    int fat = 0;
                    int carbs = 0;
                    String picture = null;
                    String authorUsername = r.get("authorUsername").asString();
                    if(r.get("calories") != NULL)
                        calories = r.get("calories").asInt();
                    if(r.get("Fat") != NULL)
                        fat = r.get("fat").asInt();
                    if(r.get("protein") != NULL)
                        protein = r.get("protein").asInt();
                    if(r.get("carbs") != NULL)
                        carbs = r.get("carbs").asInt();
                    if (r.get("picture") != NULL)
                    {
                        picture = r.get("picture").asString();
                    }
                    Recipe recipe = new Recipe(title, fat, calories, protein, carbs, picture);
                    recipe.setAuthorUsername(authorUsername);
                    recipes.add(recipe);
                }
                return null;
            });
        }
        return recipes;
    }

    /**
     * Function that returns a list of the users that contains in their username the word passed
     * @param howManySkip           How many to skip
     * @param howMany               How many to return
     * @param usernameWritten       Username passed by the user
     * @return                      The list of users
     */
    public List<User> searchUserByUsername (int howManySkip, int howMany, String usernameWritten)
    {
        List<User> users = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:User) " +
                                "WHERE toLower(u.username) CONTAINS toLower($username) " + //case insensitive search
                                "OPTIONAL MATCH (u)<-[f1:FOLLOWS]-(:User) " +
                                "OPTIONAL MATCH (u)-[f2:FOLLOWS]->(:User) " +
                                "OPTIONAL MATCH (u)-[a:ADDS]->(:Recipe) " +
                                "RETURN u.firstName AS firstName, u.lastName AS lastName, u.picture AS picture, " +
                                "u.username AS username, u.password AS password, u.role AS role, " +
                                "COUNT(DISTINCT f1) AS follower, COUNT (DISTINCT f2) AS following, COUNT (DISTINCT a) AS numRecipes " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username",usernameWritten, "skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String firstName = r.get("firstName").asString();
                    String lastName = r.get("lastName").asString();
                    String picture = null;
                    if (r.get("picture") != NULL)
                    {
                        picture = r.get("picture").asString();
                    }
                    String username = r.get("username").asString();
                    String password = r.get("password").asString();
                    int role = r.get("role").asInt();
                    User user = new User(firstName, lastName, picture, username, password, role);
                    user.setFollower(r.get("follower").asInt());
                    user.setFollowing(r.get("following").asInt());
                    user.setNumRecipes(r.get("numRecipes").asInt());
                    users.add(user);
                }
                return null;
            });
        }
        return users;
    }

    /**
     * Function that returns the list of users whom contains in their full name the string passed
     * @param howManySkip       How many to skip
     * @param howMany           How many to retrieve
     * @param fullName          Part of the full name
     * @return                  The list of users
     */
    public List<User> searchUserByFullName (int howManySkip, int howMany, String fullName)
    {
        List<User> users = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:User) " +
                                // consider firstName-lastName and lastName-firstName
                                "WHERE toLower(u.firstName + ' ' + u.lastName) CONTAINS toLower($fullName) " +
                                "OR toLower(u.lastName + ' ' + u.firstName) CONTAINS toLower($fullName) " +
                                "OPTIONAL MATCH (u)<-[f1:FOLLOWS]-(:User) " +
                                "OPTIONAL MATCH (u)-[f2:FOLLOWS]->(:User) " +
                                "OPTIONAL MATCH (u)-[a:ADDS]->(:Recipe) " +
                                "RETURN u.firstName AS firstName, u.lastName AS lastName, u.picture AS picture, " +
                                "u.username AS username, u.password AS password, u.role AS role, " +
                                "COUNT(DISTINCT f1) AS follower, COUNT (DISTINCT f2) AS following, COUNT (DISTINCT a) AS numRecipes " +
                                "SKIP $skip LIMIT $limit",
                        parameters("fullName",fullName, "skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String firstName = r.get("firstName").asString();
                    String lastName = r.get("lastName").asString();
                    String picture = null;
                    if (r.get("picture") != NULL)
                    {
                        picture = r.get("picture").asString();
                    }
                    String username = r.get("username").asString();
                    String password = r.get("password").asString();
                    int role = r.get("role").asInt();
                    User user = new User(firstName, lastName, picture, username, password, role);
                    user.setFollower(r.get("follower").asInt());
                    user.setFollowing(r.get("following").asInt());
                    user.setNumRecipes(r.get("numRecipes").asInt());
                    users.add(user);
                }
                return null;
            });
        }
        return users;
    }
}
