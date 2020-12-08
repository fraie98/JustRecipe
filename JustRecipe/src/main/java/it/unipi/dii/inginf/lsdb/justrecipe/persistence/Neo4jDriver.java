package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

/**
 * This class is used to communicate with Neo4j
 */
public class Neo4jDriver implements DatabaseDriver{
    private Driver driver;
    private String ip;
    private int port;
    private String username;
    private String password;

    public Neo4jDriver(ConfigurationParameters configurationParameters)
    {
        this.ip = configurationParameters.getNeo4jIp();
        this.port = configurationParameters.getNeo4jPort();
        this.username = configurationParameters.getNeo4jUsername();
        this.password = configurationParameters.getNeo4jPassword();
        initConnection();
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
                                "password: $password})",
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
                                 "RETURN count(*)",
                        parameters( "username", username, "password", password ) );
                 if (result.single().get(0).asInt() != 0)
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
                                "RETURN count(*)",
                        parameters( "username", username) );
                if (result.single().get(0).asInt() != 0)
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
}
