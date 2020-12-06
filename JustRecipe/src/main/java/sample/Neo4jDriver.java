package sample;

import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4jDriver {
    private Driver driver;
    private String hostname;
    private int port;
    private String username;
    private String password;

    //DA CAMBIARE: PASSARE LA CLASSE PARAMETRI DI CONFIGURAZIONE
    public Neo4jDriver( String hostName, int port, String username, String password )
    {
        this.hostname = hostName;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void initConnection()
    {
        driver = GraphDatabase.driver( "neo4j://" + hostname + ":" + port, AuthTokens.basic( username, password ) );
    }

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

    public void closeConnection ()
    {
        driver.close();
    }
}
